const path = require("path");
const Video = require("../models/Video");
const jwt = require("jsonwebtoken");
const key = "Some super secret key";
const mongoose = require("mongoose");
const fs = require("fs");

const express = require("express");
const net = require("net");

const app = express();
app.use(express.json());
const CppServerHost = "localhost"; // Host of the C++ server
const CppServerPort = 9090; // Port of the C++ server

var relatedVideos = [];

function communicateWithCppServer(message, callback) {
  const client = new net.Socket();
  let responseData = "";

  client.connect(CppServerPort, CppServerHost, () => {
    client.write(message);
  });

  client.on("data", (data) => {
    responseData += data.toString();
  });

  client.on("end", () => {
    callback(null, responseData);
  });

  client.on("error", (err) => {
    callback(err, null);
  });
}

// Create a new video
exports.createVideo = async (req, res) => {
  // Log incoming request body
  console.log("Received request body:", req.body);

  const {
    title,
    description,
    tags,
    video,
    thumbnail,
    upload_date,
    duration,
    username,
  } = req.body;

  try {
    // Basic validation
    if (
      !title ||
      !description ||
      !video ||
      !thumbnail ||
      !upload_date ||
      !duration ||
      !username
    ) {
      console.log("Validation failed: Missing fields");

      if (!title) {
        console.log("Missing or empty field: title");
      }
      if (!description) {
        console.log("Missing or empty field: description");
      }
      if (!video) {
        console.log("Missing or empty field: video");
      }
      if (!thumbnail) {
        console.log("Missing or empty field: thumbnail");
      }
      if (!upload_date) {
        console.log("Missing or empty field: upload_date");
      }
      if (!duration) {
        console.log("Missing or empty field: duration");
      }
      if (!username) {
        console.log("Missing or empty field: username");
      }

      return res.status(400).json({ message: "All fields are required" });
    }

    console.log("Passed validation checks");

    // Save base64 video to server
    const base64Video = video.split(";base64,").pop();
    const videoBuffer = Buffer.from(base64Video, "base64");
    const videoName = `${Date.now()}-${username}.mp4`;
    const videoPath = path.join(
      __dirname,
      "..",
      "..",
      "web-devlopment-build",
      "build",
      "Videos",
      videoName
    );

    // Log before writing the video file
    console.log("Saving video file to:", videoPath);

    fs.writeFileSync(videoPath, videoBuffer);

    // Log after successfully saving the video
    console.log("Video saved at:", videoPath);

    // Save base64 thumbnail to server
    const base64Thumbnail = thumbnail.split(";base64,").pop();
    const thumbnailBuffer = Buffer.from(base64Thumbnail, "base64");
    const thumbnailName = `${Date.now()}-${username}.jpg`;
    const thumbnailPath = path.join(
      __dirname,
      "..",
      "..",
      "web-devlopment-build",
      "build",
      "pictures",
      "thumbnails",
      thumbnailName // Ensure this path matches your static file serving path
    );

    // Log before writing the thumbnail file
    console.log("Saving thumbnail file to:", thumbnailPath);

    fs.writeFileSync(thumbnailPath, thumbnailBuffer);

    // Log after successfully saving the thumbnail
    console.log("Thumbnail saved at:", thumbnailPath);

    // Create and save the new video
    const newVideo = new Video({
      title,
      description,
      source: `/Videos/${videoName}`, // Store relative path to the video
      thumbnail: `/pictures/thumbnails/${thumbnailName}`, // Update to match the working path
      tags,
      upload_date,
      duration,
      username,
      likeCount: 0, // Initialize like count to 0
      views: 0, // Initialize view count to 0
      usersLikes: [],
      comments: [],
    });

    // Log the new video object before saving to the database
    console.log("New video object to be saved:", newVideo);

    await newVideo.save();

    console.log(
      "New video has been created and saved to database: " + newVideo.title
    );
    res.status(201).send(newVideo);
  } catch (error) {
    console.error("Error creating video:", error);
    res.status(400).send({ message: "An error occurred", error });
  }
};

exports.getVideos = async (req, res) => {
  try {
    // Fetch top 10 videos by views
    const topVideos = await Video.find().sort({ views: -1, _id: 1 }).limit(10);
    const topVideoIds = topVideos.map((video) => video._id);

    // Fetch 10 random videos excluding the top 10
    const randomVideos = await Video.aggregate([
      { $match: { _id: { $nin: topVideoIds } } },
      { $sample: { size: 10 } },
    ]);

    // Combine both results
    const videos = [...topVideos, ...randomVideos];

    // Send the combined results
    res.send(videos);
  } catch (error) {
    console.error("Error fetching videos:", error);
    res.status(500).send(error);
  }
};

exports.getAllVideos = async (req, res) => {
  try {
    // Extract query parameters from the request
    const { title, username, uploadDate } = req.query;

    // Initialize an array to hold individual query conditions
    let queryConditions = [];

    if (title) {
      queryConditions.push({ title: { $regex: title, $options: "i" } }); // Case-insensitive regex search
    }

    if (username) {
      queryConditions.push({ username: username });
    }

    if (uploadDate) {
      // Assuming uploadDate is in ISO format (YYYY-MM-DD)
      const startDate = new Date(uploadDate);
      const endDate = new Date(uploadDate);
      endDate.setDate(endDate.getDate() + 1);
      queryConditions.push({ uploadDate: { $gte: startDate, $lt: endDate } });
    }

    // Build the final query using $or operator if there are conditions
    let query = queryConditions.length > 0 ? { $or: queryConditions } : {};

    // Fetch videos from the database based on the query
    const videos = await Video.find(query);
    res.send(videos);
  } catch (error) {
    res.status(500).send(error);
  }
};

exports.getRelatedVideos = async (req, res) => {
  try {
    const video = await Video.findById(req.params.id);
    const { _id, username, tags } = video;

    // Fetch videos from the database based on the query
    let pipeline = [
      {
        $match: {
          _id: { $ne: _id },
        },
      },
      {
        $addFields: {
          priorityScore: {
            $add: [
              {
                $multiply: [
                  { $size: { $setIntersection: [tags, "$tags"] } },
                  1,
                ],
              }, // Score for common tags
              {
                $cond: {
                  if: { $in: ["$_id", relatedVideos] },
                  then: 100000,
                  else: 1,
                },
              }, // Score for related videos
              {
                $cond: {
                  if: { $eq: ["$username", username] },
                  then: 5,
                  else: 0,
                },
              }, // Score for matching username
            ],
          },
        },
      },
      {
        $sort: { priorityScore: -1, _id: 1 },
      },
    ];

    const page = parseInt(req.query.page) || 1; // Get the page from the query or default to 1
    const limit = parseInt(req.query.limit);
    if (limit) {
      const skip = (page - 1) * limit;
      pipeline = [
        ...pipeline,
        { $skip: skip },
        { $limit: limit },
        { $sample: { size: limit } },
      ];
    }

    const videos = await Video.aggregate(pipeline);
    const totalVideos = await Video.countDocuments();
    res.json({
      videos: videos,
      totalVideos: totalVideos,
      currentPage: page,
      totalPages: Math.ceil(totalVideos / limit),
    });
  } catch (error) {
    res.status(500).send(error);
  }
};
// Get a specific video by ID
exports.getVideoById = async (req, res) => {
  try {
    const video = await Video.findById(req.params.id);
    if (!video) {
      return res.status(404).send();
    }
    res.send(video);
  } catch (error) {
    res.status(500).send(error);
  }
};

// Update a video by ID (PUT)
exports.updateVideo = async (req, res) => {
  try {
    const test = await Video.findById(req.params.id);
    if (!test) {
      return res.status(404).send();
    }
    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== test.username) {
      return res.status(403).send("Forbidden");
    }

    const video = await Video.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });

    res.send(video);
  } catch (error) {
    res.status(400).send(error);
  }
};

// Update a video by ID (PATCH)
exports.partialUpdateVideo = async (req, res) => {
  try {
    const test = await Video.findById(req.params.id);
    if (!test) {
      return res.status(404).send();
    }
    if (req.headers.authorization) {
      // Extract the token from that header
      try {
        const token = req.headers.authorization.split(" ")[1];
        const data = jwt.verify(token, key);
        if (data.username !== test.username) {
          if (
            "title" in req.body ||
            "description" in req.body ||
            "source" in req.body ||
            "thumbnail" in req.body ||
            "tags" in req.body ||
            "upload_date" in req.body ||
            "duration" in req.body ||
            "username" in req.body
          ) {
            return res.status(403).send("Forbidden");
          }

          if ("views" in req.body) {
            if (
              req.body.views !== test.views + 1 &&
              req.body.views !== test.views
            ) {
              return res.status(403).send("Forbidden");
            }
          }
          if ("likeCount" in req.body) {
            if (
              req.body.likeCount !== test.likeCount + 1 &&
              req.body.likeCount !== test.likeCount - 1
            ) {
              return res.status(403).send("Forbidden");
            }
          }

          if ("usersLikes" in req.body) {
            if (
              req.body.usersLikes.length !== test.usersLikes.length + 1 &&
              req.body.usersLikes.length !== test.usersLikes.length - 1
            ) {
              return res.status(403).send("Forbidden");
            }
          }
          if ("comments" in req.body) {
            if (
              req.body.comments.length !== test.comments.length + 1 &&
              req.body.comments.length !== test.comments.length - 1
            ) {
              return res.status(403).send("Forbidden");
            }
          }
        }
        const video = await Video.findByIdAndUpdate(req.params.id, req.body, {
          new: true,
          runValidators: true,
        });
        if (req.body.views === test.views + 1) {
          const message = `WATCH ${data.username} ${req.params.id}\n`;
          // Communicate with C++ server
          communicateWithCppServer(message, (err, response) => {
            if (err) {
              console.error("Error communicating with C++ server:", err);
              return res.status(500).send("Internal Server Error");
            }
            console.log("C++ server response1:", response);
            relatedVideos = JSON.parse(response).recommendations;
          });
        }
        res.send(video);
      } catch (err) {
        if (
          "title" in req.body ||
          "description" in req.body ||
          "source" in req.body ||
          "thumbnail" in req.body ||
          "tags" in req.body ||
          "upload_date" in req.body ||
          "duration" in req.body ||
          "username" in req.body ||
          "likeCount" in req.body ||
          "usersLikes" in req.body ||
          "comments" in req.body
        ) {
          return res.status(403).send("Forbidden");
        } else if ("views" in req.body) {
          if (
            req.body.views !== test.views + 1 &&
            req.body.views !== test.views
          ) {
            return res.status(403).send("Forbidden");
          }
          try {
            const video = await Video.findByIdAndUpdate(
              req.params.id,
              req.body,
              { new: true, runValidators: true }
            );
            if (req.body.views === test.views + 1) {
              const message = `WATCH ${data.username} ${req.params.id}\n`;
              // Communicate with C++ server
              communicateWithCppServer(message, (err, response) => {
                if (err) {
                  console.error("Error communicating with C++ server:", err);
                  return res.status(500).send("Internal Server Error");
                }
                console.log("C++ server response2:", response);
                relatedVideos = JSON.parse(response).recommendations;
              });
            }
            res.send(video);
          } catch (error) {
            res.status(400).send(error);
          }
        }
      }
    } else return res.status(403).send("Token required");
  } catch (error) {
    res.status(400).send(error);
  }
};
// Delete a video by ID
exports.deleteVideo = async (req, res) => {
  try {
    const test = await Video.findById(req.params.id);
    if (!test) {
      return res.status(404).send();
    }
    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== test.username) {
      return res.status(403).send("Forbidden");
    }

    const video = await Video.findByIdAndDelete(req.params.id);

    res.send(video);
  } catch (error) {
    res.status(500).send(error);
  }
};

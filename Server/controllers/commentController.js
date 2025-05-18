const Video = require("../models/Video");
const jwt = require("jsonwebtoken");
const key = "Some super secret key";

// Create a comment
exports.createComment = async (req, res) => {
  const { videoId } = req.params;
  const { user, content, date, usersLikes, replies } = req.body;
  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const newComment = {
      user,
      content,
      date,
      usersLikes,
      replies,
    };

    video.comments.push(newComment);
    await video.save();

    res.status(201).send(video.comments[video.comments.length - 1]);
  } catch (error) {
    res.status(400).send(error.message);
  }
};

// Get all comments for a video
exports.getComments = async (req, res) => {
  const { videoId } = req.params;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).json({ message: "Video not found" });
    }
    const sortedComments = video.comments.sort((a, b) => b.date - a.date);
    const limit = parseInt(req.query.limit) || sortedComments.length;
    const page = parseInt(req.query.page) || 1;
    const skip = (page - 1) * limit;
    const paginatedComments = sortedComments.slice(skip, skip + limit);

    res.json({
      comments: paginatedComments,
      totalComments: sortedComments.length,
      currentPage: page,
      totalPages: Math.ceil(sortedComments.length / limit),
    });
  } catch (error) {
    console.error("Error fetching comments:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

// Get a comment for a video
exports.getComment = async (req, res) => {
  const { videoId, commentId } = req.params;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      return res.status(404).send("Comment not found");
    }
    res.send(comment);
  } catch (error) {
    res.status(500).send(error.message);
  }
};

// Update a comment (PUT)
exports.updateComment = async (req, res) => {
  const { videoId, commentId } = req.params;
  const updatedComment = req.body;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      return res.status(404).send("Comment not found");
    }

    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== comment.user) {
      return res.status(403).send("Forbidden");
    }

    comment.user = updatedComment.user || comment.user; // retain the old value if the new value is not provided
    comment.content = updatedComment.content || comment.content;
    comment.date = updatedComment.date || comment.date;
    comment.usersLikes = updatedComment.usersLikes || comment.usersLikes;
    comment.replies = updatedComment.replies || comment.replies;

    await video.save();

    res.send(comment);
  } catch (error) {
    res.status(400).send(error.message);
  }
};

// Update a comment (PATCH)
exports.partialUpdateComment = async (req, res) => {
  const { videoId, commentId } = req.params;
  const updatedFields = req.body;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      return res.status(404).send("Comment not found");
    }

    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== comment.user) {
      if (
        "user" in updatedFields ||
        "content" in updatedFields ||
        "date" in updatedFields
      ) {
        console.log("body problem");
        return res.status(403).send("Forbidden");
      }
      if ("usersLikes" in updatedFields) {
        if (
          updatedFields.usersLikes.length !== comment.usersLikes.length + 1 &&
          updatedFields.usersLikes.length !== comment.usersLikes.length - 1
        ) {
          console.log("usersLikes problem");
          return res.status(403).send("Forbidden");
        }
      }
      if ("replies" in updatedFields) {
        if (
          updatedFields.replies.length !== comment.replies.length + 1 &&
          updatedFields.replies.length !== comment.replies.length - 1 &&
          updatedFields.replies.length !== comment.replies.length
        ) {
          console.log("replies problem");
          return res.status(403).send("Forbidden");
        }
      }
    }

    // Update only the provided fields
    Object.keys(updatedFields).forEach((field) => {
      comment[field] = updatedFields[field];
    });

    await video.save();

    res.send(comment);
  } catch (error) {
    res.status(400).send(error.message);
  }
};

// Delete a comment
exports.deleteComment = async (req, res) => {
  const { videoId, commentId } = req.params;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      return res.status(404).send("Comment not found");
    }

    const token = req.headers.authorization.split(" ")[1];

    const data = jwt.verify(token, key);
    if (data.username !== comment.user) {
      return res.status(403).send("Forbidden");
    }

    video.comments.remove(comment);
    await video.save();

    res.send("Comment deleted successfully");
  } catch (error) {
    res.status(500).send(error.message);
  }
};

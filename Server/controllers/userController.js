const User = require("../models/User");
const Video = require("../models/Video");
const path = require("path");
const fs = require("fs");
const jwt = require("jsonwebtoken");
const key = "Some super secret key";

exports.createUser = async (req, res) => {
  const { username, displayname, password, passwordAgain, image } = req.body;
  try {
    // Basic validation
    if (!username || !displayname || !password || !passwordAgain || !image) {
      console.log("Validation failed: Missing fields");
      return res.status(400).json({ message: "All fields are required" });
    }

    const displaynameWords = displayname.trim().split(/\s+/);
    if (displaynameWords.length < 2) {
      console.log(
        "Validation failed: Display name must contain at least two words"
      );
      return res
        .status(400)
        .json({ message: "Must input first and last name" });
    }

    if (password !== passwordAgain) {
      console.log("Validation failed: Passwords do not match");
      return res.status(400).json({ message: "Password fields do not match" });
    }

    if (password.length < 8) {
      console.log("Validation failed: Password too short");
      return res
        .status(400)
        .json({ message: "Password must be at least 8 characters" });
    }

    const hasLetter = /[a-zA-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);

    if (!hasLetter || !hasNumber) {
      console.log(
        "Validation failed: Password does not contain both letters and numbers"
      );
      return res
        .status(400)
        .json({ message: "Password must contain both letters and numbers" });
    }

    // Check if the username is already taken
    const existingUser = await User.findOne({ username: username });
    if (existingUser) {
      console.log("Validation failed: Username already taken");
      return res.status(409).json({ message: "Username already taken" });
    }

    // Save base64 image to server
    const base64Image = image.split(";base64,").pop();
    const imageBuffer = Buffer.from(base64Image, "base64");
    const imageName = `${Date.now()}-${username}.jpg`;
    const imagePath = path.join(
      __dirname,
      "..",
      "..",
      "web-devlopment-build",
      "build",
      "pictures",
      "users",
      imageName
    );

    fs.writeFileSync(imagePath, imageBuffer);
    console.log("Image saved at:", imagePath);

    // Create and save the new user
    const newUser = new User({
      username,
      displayname,
      password,
      image: `/pictures/users/${imageName}`,
    });

    await newUser.save();
    console.log("new user has been created" + newUser.username);
    res.status(201).send(newUser);
  } catch (error) {
    res.status(400).send({ message: "An error occurred", error });
  }
};

// Get all users
exports.getAllUsers = async (req, res) => {
  try {
    const users = await User.find().select("-password");
    res.send(users);
  } catch (error) {
    res.status(500).send(error);
  }
};

// Get user by id
exports.getUser = async (req, res) => {
  try {
    const user = await User.findById(req.params.id).select("-password");
    if (!user) {
      return res.status(404).send();
    }
    res.send(user);
  } catch (error) {
    res.status(500).send(error);
  }
};
// Get user by name
exports.getUserByUserName = async (req, res) => {
  try {
    const user = await User.findOne({ username: req.params.username }).select(
      "-password"
    );
    if (!user) {
      return res.status(404).send({ message: "User not found" });
    }
    res.send(user);
  } catch (error) {
    res.status(500).send({ error: "Internal Server Error" });
  }
};

// Update a user (PUT)
exports.updateUser = async (req, res) => {
  const { username, displayname, password, passwordAgain, image } = req.body;
  console.log(passwordAgain);
  try {
    const test = await User.findById(req.params.id);
    if (!test) {
      console.log("id is wrong");
      return res.status(404).send();
    }
    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== test.username) {
      console.log("user is wrong");
      return res.status(403).send("Forbidden");
    }
    if (req.body.username !== test.username) {
      console.log("username is wrong");
      return res.status(400).send({ message: "Cannot change username" });
    }
    if (
      req.body._id !== test._id ||
      req.body._id !== new String(test._id).valueOf()
    ) {
      console.log(req.body._id);
      console.log(new String(test._id).valueOf());
      return res.status(400).send({ message: "Cannot change _id" });
    }
    if (!displayname || !password || !passwordAgain || !image) {
      console.log("empty fields");
      return res.status(400).json({ message: "All fields are required" });
    }

    const displaynameWords = displayname.trim().split(/\s+/);
    if (displaynameWords.length < 2) {
      console.log("name is wrong");
      return res
        .status(400)
        .json({ message: "Must input first and last name" });
    }

    if (password !== passwordAgain) {
      console.log("password is wrong");
      return res.status(400).json({ message: "Password fields do not match" });
    }

    if (password.length < 8) {
      console.log("password length is wrong");
      return res
        .status(400)
        .json({ message: "Password must be at least 8 characters" });
    }

    const hasLetter = /[a-zA-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);

    if (!hasLetter || !hasNumber) {
      console.log("empty");
      return res
        .status(400)
        .json({ message: "Password must contain both letters and numbers" });
    }

    // Save base64 image to server
    const base64Image = image.split(";base64,").pop();
    const imageBuffer = Buffer.from(base64Image, "base64");
    const imageName = `${Date.now()}-${username}.jpg`;
    const imagePath = path.join(
      __dirname,
      "..",
      "..",
      "web-devlopment-build",
      "build",
      "pictures",
      "users",
      imageName
    );
    fs.writeFileSync(imagePath, imageBuffer);
    req.body.image = `/pictures/users/${imageName}`;

    const user = await User.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });
    console.log("no problem...");
    res.send(user);
  } catch (error) {
    console.log(error);
    res.status(400).send(error);
  }
};

// Update a user
exports.partialUpdateUser = async (req, res) => {
  try {
    const test = await User.findById(req.params.id);
    if (!test) {
      return res.status(404).send();
    }

    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== test.username) {
      return res.status(403).send("Forbidden");
    }
    if (req.body.username && req.body.username !== test.username) {
      return res.status(400).send({ message: "Cannot change username" });
    }

    if (req.body.displayname) {
      const displaynameWords = req.body.displayname.trim().split(/\s+/);
      if (displaynameWords.length < 2) {
        return res
          .status(400)
          .json({ message: "Must input first and last name" });
      }
    }
    if (req.body.password) {
      if (!req.body.passwordAgain) {
        return res.status(400).send({ message: "Please confirm password" });
      }
      const password = req.body.password;
      const passwordAgain = req.body.passwordAgain;
      if (password !== passwordAgain) {
        return res
          .status(400)
          .json({ message: "Password fields do not match" });
      }

      if (password.length < 8) {
        return res
          .status(400)
          .json({ message: "Password must be at least 8 characters" });
      }
      const hasLetter = /[a-zA-Z]/.test(password);
      const hasNumber = /[0-9]/.test(password);
      if (!hasLetter || !hasNumber) {
        return res
          .status(400)
          .json({ message: "Password must contain both letters and numbers" });
      }
    }

    if (req.body.image) {
      const { username, image } = req.body;
      // Save base64 image to server
      const base64Image = image.split(";base64,").pop();
      const imageBuffer = Buffer.from(base64Image, "base64");
      const imageName = `${Date.now()}-${username}.jpg`;
      const imagePath = path.join(
        __dirname,
        "..",
        "..",
        "web-devlopment-build",
        "build",
        "pictures",
        "users",
        imageName
      );

      fs.writeFileSync(imagePath, imageBuffer);

      req.body.image = `/pictures/users/${imageName}`;
    }

    const user = await User.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });
    res.send(user);
  } catch (error) {
    res.status(400).send(error);
  }
};

// Delete a user
exports.deleteUser = async (req, res) => {
  try {
    const test = await User.findById(req.params.id);
    if (!test) {
      return res.status(404).send();
    }
    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== test.username) {
      return res.status(403).send("Forbidden");
    }

    // Delete all user videos
    await Video.deleteMany({ username: test.username });
    // Delete all user comments
    const videos = await Video.find({ "comments.user": test.username });

    for (const video of videos) {
      video.comments = video.comments.filter(
        (comment) => comment.user !== test.username
      );
      await video.save(); // Save the updated video document
    }

    // Delete all user replies
    const videosReplies = await Video.find({});

    // Iterate over the videos and remove the replies made by the user
    for (const video of videosReplies) {
      for (const comment of video.comments) {
        // Filter out replies made by the user
        comment.replies = comment.replies.filter(
          (reply) => reply.user !== test.username
        );
      }
      await video.save(); // Save the updated video document
    }

    // Delete the user
    await User.findByIdAndDelete(req.params.id);
    res.status(204).send("User deleted");
  } catch (error) {
    console.log(error);
    res.status(500).send(error);
  }
};

// Get a user videos by ID
exports.getUserVideos = async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).send();
    }
    const username = user.username;
    const videos = await Video.find({ username: username });
    if (!videos) {
      return res.status(404).send("No videos found for this user");
    }
    res.send(videos);
  } catch (error) {
    res.status(500).send("Error fetching user videos: " + error.message);
  }
};

const Video = require("../models/Video");
const jwt = require("jsonwebtoken");
const key = "Some super secret key";

// Create a reply
exports.createReply = async (req, res) => {
  const { videoId, commentId } = req.params;
  const { user, content, date, usersLikes } = req.body;
  console.log(user);
  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      console.log("comment not found");
      return res.status(404).send("Comment not found");
    }
    const newReply = {
      user,
      content,
      date,
      usersLikes,
    };
    console.log(comment.content);
    // console.log(newReply);

    comment.replies.push(newReply);
    await video.save();

    res.status(201).send(newReply);
  } catch (error) {
    console.log(error.message);
    res.status(400).send(error.message);
  }
};

// Get all replies for a comment
exports.getReplies = async (req, res) => {
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

    res.send(comment.replies);
  } catch (error) {
    res.status(500).send(error.message);
  }
};

// Get a reply for a comment
exports.getReply = async (req, res) => {
  const { videoId, commentId, replyId } = req.params;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      return res.status(404).send("Comment not found");
    }

    const reply = comment.replies.id(replyId);
    if (!reply) {
      return res.status(404).send("Reply not found");
    }
    res.send(reply);
  } catch (error) {
    res.status(500).send(error.message);
  }
};

// Update a reply (PATCH)
exports.partialUpdateReply = async (req, res) => {
  const { videoId, commentId, replyId } = req.params;
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

    const reply = comment.replies.id(replyId);
    if (!reply) {
      return res.status(404).send("Reply not found");
    }

    const token = req.headers.authorization.split(" ")[1];
    const data = jwt.verify(token, key);
    if (data.username !== reply.user) {
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
          updatedFields.usersLikes.length !== reply.usersLikes.length + 1 &&
          updatedFields.usersLikes.length !== reply.usersLikes.length - 1
        ) {
          console.log("usersLikes problem");
          return res.status(403).send("Forbidden");
        }
      }
    }

    // Update only the provided fields
    Object.keys(updatedFields).forEach((field) => {
      reply[field] = updatedFields[field];
    });

    await comment.save();
    await video.save();

    res.send(reply);
  } catch (error) {
    res.status(400).send(error.message);
  }
};

// Update a reply
exports.updateReply = async (req, res) => {
  const { videoId, commentId, replyId } = req.params;
  const { content } = req.body;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      console.log("video not found problem");

      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      console.log("comment not found problem");

      return res.status(404).send("Comment not found");
    }

    const reply = comment.replies.id(replyId);
    if (!reply) {
      console.log("reply not found problem");

      return res.status(404).send("Reply not found");
    }

    reply.content = content;
    await video.save();
    res.send(reply);
  } catch (error) {
    console.log(error.message);

    res.status(400).send(error.message);
  }
};

// Delete a reply
exports.deleteReply = async (req, res) => {
  const { videoId, commentId, replyId } = req.params;

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send("Video not found");
    }

    const comment = video.comments.id(commentId);
    if (!comment) {
      return res.status(404).send("Comment not found");
    }

    const reply = comment.replies.id(replyId);
    if (!reply) {
      return res.status(404).send("Reply not found");
    }
    // Log the reply to be removed
    console.log(reply);

    // reply.remove();
    comment.replies.remove(reply);
    await comment.save();
    await video.save();

    res.send("Reply deleted successfully");
  } catch (error) {
    console.log(error.message);

    res.status(500).send(error.message);
  }
};

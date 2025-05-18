const express = require("express");
const router = express.Router();
const replyController = require("../controllers/replyController");
const tokenController = require("../controllers/tokenController");

router.post(
  "/:videoId/comments/:commentId/replies",
  tokenController.isLoggedIn,
  replyController.createReply
);

router.get("/:videoId/comments/:commentId/replies", replyController.getReplies);

router.get(
  "/:videoId/comments/:commentId/replies/:replyId",
  replyController.getReply
);

router.put(
  "/:videoId/comments/:commentId/replies/:replyId",
  tokenController.isLoggedIn,
  replyController.updateReply
);

router.patch(
  "/:videoId/comments/:commentId/replies/:replyId",
  tokenController.isLoggedIn,
  replyController.partialUpdateReply
);

router.delete(
  "/:videoId/comments/:commentId/replies/:replyId",
  tokenController.isLoggedIn,
  replyController.deleteReply
);

module.exports = router;

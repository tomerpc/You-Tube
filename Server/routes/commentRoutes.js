const express = require('express');
const router = express.Router();
const commentController = require('../controllers/commentController');
const tokenController = require('../controllers/tokenController');


router.get('/:videoId/comments', commentController.getComments);

router.get('/:videoId/comments/:commentId', commentController.getComment);



router.post('/:videoId/comments',tokenController.isLoggedIn, commentController.createComment);

router.put('/:videoId/comments/:commentId',tokenController.isLoggedIn, commentController.updateComment);

router.patch('/:videoId/comments/:commentId',tokenController.isLoggedIn, commentController.partialUpdateComment);

router.delete('/:videoId/comments/:commentId',tokenController.isLoggedIn, commentController.deleteComment);

module.exports = router;

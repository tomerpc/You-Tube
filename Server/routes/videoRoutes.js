
const express = require('express');
const router = express.Router();
const commentRoutes = require('./commentRoutes')
const replyRoutes = require('./replyRoutes')
const videoController = require('../controllers/videoController');
const tokenController = require('../controllers/tokenController');


// Get videos
router.get('', videoController.getVideos);


// Get all videos
router.get('/all', videoController.getAllVideos);

router.get('/:id/related', videoController.getRelatedVideos);

// Get a specific video by ID
router.get('/:id', videoController.getVideoById);


// Create a new video
router.post('',tokenController.isLoggedIn, videoController.createVideo);



// Update a video by ID (PUT)
router.put('/:id',tokenController.isLoggedIn, videoController.updateVideo);

// Update a video by ID (PATCH)
router.patch('/:id', videoController.partialUpdateVideo);

// Delete a video by ID
router.delete('/:id',tokenController.isLoggedIn, videoController.deleteVideo);


router.use('', commentRoutes);
router.use('', replyRoutes);

module.exports = router;

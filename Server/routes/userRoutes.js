const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const videoRoutes = require('./videoRoutes')
const tokenController = require('../controllers/tokenController');


// Create a new user
router.post('', userController.createUser);

// Get all users
router.get('', userController.getAllUsers);

// Get a user by ID
router.get('/:id', userController.getUser);
// Get a user videos by ID
router.get('/:id/videos', userController.getUserVideos);

// Get a user by username
router.get('/username/:username', userController.getUserByUserName);




// Update a user (PUT)
router.put('/:id',tokenController.isLoggedIn, userController.updateUser);

// Update a user
router.patch('/:id',tokenController.isLoggedIn, userController.partialUpdateUser);

// Delete a user
router.delete('/:id',tokenController.isLoggedIn, userController.deleteUser);


router.use('/:id/videos', videoRoutes);

module.exports = router;

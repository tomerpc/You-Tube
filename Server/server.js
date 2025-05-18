const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors'); // Import CORS middleware
const fileUpload = require('express-fileupload');
const path = require('path');
const app = express();
const port = process.env.PORT || 4000;
const mongodbUri = process.env.MONGODB_URI || "mongodb://localhost:27017/mydatabase";

// Middleware to increase payload size limit
app.use(express.json({ limit: '1000mb' })); // You can adjust the limit as needed
app.use(express.urlencoded({ limit: '1000mb', extended: true })); // Also increase for URL-encoded payloads
const jwt = require("jsonwebtoken")

if (!mongodbUri) {
  console.error('MONGODB_URI is not defined');
  process.exit(1);
}

// Middleware
app.use(express.json());

// Allow all origins in CORS
app.use(cors());

// Use express-fileupload middleware
app.use(fileUpload());

// Serve static files
app.use('/images', express.static(path.join(__dirname, 'public/images')));

// Connect to MongoDB
mongoose.connect(mongodbUri, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log(err));

// Import routes
const userRoutes = require('./routes/userRoutes');
const videoRoutes = require('./routes/videoRoutes');
const tokenRoutes = require('./routes/tokenRoutes');

// Use routes
app.use('/api/users', userRoutes);
app.use('/api/videos', videoRoutes);
app.use('/api/tokens', tokenRoutes);

// Serve static files from the React app
app.use(express.static(path.join(__dirname, '../web-devlopment-build/build')));

// The "catchall" handler: for any request that doesn't
// match one above, send back React's index.html file.
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '../web-devlopment-build/build', 'index.html'));
});

// Start the server
app.listen(port, () => {
  console.log(`Server is running on localhost:${port}`);
});

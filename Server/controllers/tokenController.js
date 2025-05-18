const jwt = require("jsonwebtoken");
const key = "Some super secret key";
const User = require("../models/User");

// Ensure that the user sent a valid token
exports.isLoggedIn = (req, res, next) => {
  // If the request has an authorization header
  if (req.headers.authorization) {
    // Extract the token from that header
    const token = req.headers.authorization.split(" ")[1];
    try {
      // Verify the token is valid
      const data = jwt.verify(token, key);
      console.log(req.headers.authorization);
      return next();
    } catch (err) {
      console.log(req.headers.authorization);
      return res.status(401).send("Invalid Token");
    }
  } else {
    console.log(req.headers.authorization);
    return res.status(403).send("Token required");
  }
};

// Handle login form submission
exports.processLogin = async (req, res) => {
  // Check credentials
  let user = false;
  try {
    user = await User.findOne({ username: req.body.username });
    if (!user) {
      return res.status(404).send("Invalid username");
    }
  } catch (error) {
    return res.status(500).send({ error: "Internal Server Error" });
  }
  if (user) {
    if (user.password !== req.body.password) {
      // Incorrect password. The user should try again.
      return res.status(404).send("Invalid password");
    } else {
      const data = { username: req.body.username };
      // Generate the token.
      const token = jwt.sign(data, key);
      // Return the token to the browser
      return res.status(201).json({ token });
    }
  }
};

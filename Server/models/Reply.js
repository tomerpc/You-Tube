const mongoose = require('mongoose');

const replySchema = new mongoose.Schema({
    user: { type: String, required: true },
    content: { type: String, required: true },
    date: { type: Date, required: true },
    usersLikes: { type: [String], default: [] }
});

module.exports = mongoose.model('Reply', replySchema);
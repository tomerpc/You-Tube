const mongoose = require('mongoose');

const Reply = require('./Reply');

const commentSchema = new mongoose.Schema({
    user: { type: String, required: true },
    content: { type: String, required: true },
    date: { type: Date, required: true },
    usersLikes: { type: [String], default: [] },
    replies: { type: [Reply.schema], default: [] }
});
module.exports = mongoose.model('Comment', commentSchema);


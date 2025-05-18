// Comment.js
import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import Reply from './Reply'; // Import Reply component
import './Comment.css';
import config from '../../config';

const Comment = ({
  cid,
  comment,
  onCommentChange,
  isSignedIn,
  videoId,
  handleDeleteComment
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isShowingReplies, setIsShowingReplies] = useState(false);
  const [isReplyFormVisible, setIsReplyFormVisible] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);
  const [author, setAuthor] = useState(null);
  const editTextareaRef = useRef(null);
  const [newReply, setNewReply] = useState({});

  const [thisComment, setThisComment] = useState(comment);
  const [usersLikedComment, setUsersLikedComment] = useState([]);
  const [userLikedComment, setUserLikedComment] = useState(false);
  const [totalUserLikes, setTotaluserLikes] = useState(0);
  const [currentCommentReplies, setCurrentCommentReplies] = useState([]);
  const [currentCommentRepliesLength, setCurrentCommentRepliesLength] = useState(0);
  const navigate = useNavigate();

  const getAuthorByUserName = async (username) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/users/username/${username}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('Failed to fetch user');
      }
      const userFromServer = await response.json();
      setAuthor(userFromServer);
    } catch (error) {
      console.error('Error fetching user:', error);
    }
  };

  const getCommentWithoutChangingState = async (cid) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}/comments/${cid}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('Failed to fetch comment');
      }
      const commentFromServer = await response.json();
      return commentFromServer;
    } catch (error) {
      console.error('Error fetching comment:', error);
    }
  };

  /*

  const updateComment = async (updatedComment) => {
    try {
      const updatedCommentFromServer = await sendUpdateRequest(updatedComment, 'PATCH');
      setThisComment(updatedCommentFromServer);
      onCommentChange(updatedCommentFromServer);
    } catch (error) {
      console.error('Error updating comment:', error);
    }
  };

  */

  const partialUpdateComment = async (updatedComment) => {
    const originalComment = await getCommentWithoutChangingState(updatedComment._id);
    if (!originalComment) {
      throw new Error('Comment not found in the local state');
    }
    try {
      const updatedFields = Object.keys(updatedComment).reduce((fields, key) => {
        if (updatedComment[key] !== originalComment[key]) {
          fields[key] = updatedComment[key];
        }
        return fields;
      }, {});
      updatedFields._id = originalComment._id;
      const updatedCommentFromServer = await sendUpdateRequest(updatedFields, 'PATCH');
      setThisComment(updatedCommentFromServer);
      onCommentChange(updatedCommentFromServer);
    } catch (error) {
      console.error('Error updating comment:', error);
    }
  };


  const sendUpdateRequest = async (updatedComment, method) => {
    const url = `${config.apiBaseUrl}/api/videos/${videoId}/comments/${updatedComment._id}`;
    let bodyData = {};

    if (method === 'PATCH') {
      // Construct bodyData with only the updated fields
      const fieldsToUpdate = ['user', 'content', 'tags', 'usersLikes', 'date', 'replies'];
      bodyData = {};
      fieldsToUpdate.forEach(field => {
        if (updatedComment.hasOwnProperty(field)) {
          bodyData[field] = updatedComment[field];
        }
      });
    } else if (method === 'PUT') {
      // For PUT, send the entire updatedComment object
      bodyData = updatedComment;
    }

    const options = {
      method: method,
      headers: {
        'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(bodyData),
    };

    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error('Failed to update comment');
      }
      return await response.json();
    } catch (error) {
      console.error('Error updating comment:', error);
      throw error; // Rethrow the error for handling in the calling function
    }
  };

  const deleteComment = async (commentId) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}/comments/${commentId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete comment');
      }
      handleDeleteComment(commentId);
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  useEffect(() => {
    if (isEditing && editTextareaRef.current) {
      editTextareaRef.current.style.height = 'auto';
      editTextareaRef.current.style.height = editTextareaRef.current.scrollHeight + 'px';
    }
  }, [isEditing, editContent]);
  
  useEffect(() => {
    setEditContent(thisComment.content);
  }, [isEditing, thisComment.content]);

  useEffect(() => {

    const getComment = async () => {
      try {
        const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}/comments/${comment._id}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        if (!response.ok) {
          throw new Error('Failed to fetch comment');
        }
        const commentFromServer = await response.json();
        setThisComment(commentFromServer);
      } catch (error) {
        console.error('Error fetching comment:', error);
      }
    };
    getComment();
    getAuthorByUserName(comment.user);
  }, [comment, videoId]);

  useEffect(() => {
    if (thisComment) {
      setUsersLikedComment(thisComment.usersLikes);
      setTotaluserLikes(usersLikedComment.length || 0);
      setCurrentCommentReplies(thisComment.replies);
      setCurrentCommentRepliesLength(currentCommentReplies.length || 0);
      if (usersLikedComment && usersLikedComment.length > 0 && usersLikedComment.find(user => user === isSignedIn.username)) {
        setUserLikedComment(true);
      } else {
        setUserLikedComment(false);
      }
    }
  }, [cid, thisComment, currentCommentReplies.length, isSignedIn.username, usersLikedComment]);

  useEffect(() => {
    setUsersLikedComment(thisComment.usersLikes);
    setTotaluserLikes(usersLikedComment.length);
  }, [thisComment, usersLikedComment]);

  useEffect(() => {
    setCurrentCommentReplies(thisComment.replies);
  }, [thisComment]);

  useEffect(() => {
    setCurrentCommentRepliesLength(currentCommentReplies.length);
  }, [currentCommentReplies]);

  useEffect(() => {
    if (usersLikedComment && usersLikedComment.length > 0 && usersLikedComment.find(user => user === isSignedIn.username)) {
      setUserLikedComment(true);
    } else {
      setUserLikedComment(false);
    }
  }, [isSignedIn, thisComment, usersLikedComment]);

  const handleEditComment = async (editContent) => {
    try {
      const updatedComment = {
        _id: thisComment._id,
        content: editContent
      };
      await partialUpdateComment(updatedComment);
    } catch (error) {
      console.error('Error editing comment:', error);
    }
  };
  const handleLikeComment = async () => {

    if (isSignedIn) {
      try {
        const newUsersLikes = [...thisComment.usersLikes, isSignedIn.username];
        const updatedComment = {
          _id: thisComment._id,
          usersLikes: newUsersLikes
        };
        await partialUpdateComment(updatedComment);
      } catch (error) {
        console.error('Error editing comment:', error);
      }
    }
    else {
      navigate('/signin');
    }
  };

  const handleUnlikeComment = async () => {
    try {
      const newUsersLikes = thisComment.usersLikes.filter(user => user !== isSignedIn.username);
      const updatedComment = {
        _id: thisComment._id,
        usersLikes: newUsersLikes
      };
      await partialUpdateComment(updatedComment);
    } catch (error) {
      console.error('Error editing comment:', error);
    }
  };

  const toggleShowReplies = () => {
    setIsShowingReplies(!isShowingReplies);
  };

  const toggleReadMore = () => {
    setIsExpanded(!isExpanded);
  };

  const showReplyForm = () => {
    if (isSignedIn) {
      setIsReplyFormVisible(true);

    }
    else {
      navigate('/signin');
    }
  };

  const hideReplyForm = () => {
    if (isReplyFormVisible) {
      handleReplyChange({ target: { value: '' } }, comment._id);
    }
    setIsReplyFormVisible(!isReplyFormVisible);
  };

  const handleSaveEdit = async () => {
    if (editContent.trim() !== '') {
      await handleEditComment(editContent);
    }
    else{
      setEditContent(thisComment.content);
    }
    setIsEditing(false);
  };
  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditContent(thisComment.content);
  };

  const handleEditContentChange = (e) => {
    setEditContent(e.target.value);
    e.target.style.height = 'auto';
    e.target.style.height = e.target.scrollHeight + 'px';
  };

  const handleReplyContentChange = (e) => {
    handleReplyChange(e, comment._id);
    e.target.style.height = 'auto';
    e.target.style.height = e.target.scrollHeight + 'px';
  };

  const handleReplyChange = (e, commentId) => {
    const { value } = e.target;
    setNewReply(prevState => ({ ...prevState, [commentId]: value }));
  };

  const handleCommentReplyChange = async (newReply) => {
    try {
      const updatedReplies = currentCommentReplies.map(reply =>
        reply._id === newReply._id ? newReply : reply
      );
      if (!updatedReplies.some(reply => reply._id === newReply._id)) {
        updatedReplies.push(newReply);
      }
      const updatedComment = {
        _id: thisComment._id,
        replies: updatedReplies
      };
      await partialUpdateComment(updatedComment);
    } catch (error) {
      console.error('Error editing comment:', error);
    }
  };


  const handleAddReply = async (e, commentId) => {
    e.preventDefault();
    const replyContent = newReply[commentId];

    if (replyContent && replyContent.trim() !== '') {
      try {
        const newReplyObject = {
          user: isSignedIn.username,
          content: replyContent,
          date: Date.now(),
          usersLikes: [],

        };
        const updatedReplies = [...thisComment.replies, newReplyObject];
        const updatedComment = {
          _id: thisComment._id,
          replies: updatedReplies
        };
        setNewReply(prevState => ({ ...prevState, [commentId]: '' }));
        await partialUpdateComment(updatedComment);
      } catch (error) {
        console.error('Error adding reply:', error);
      }
    }
  };

  const handleDeleteReply = async (replyId) => {
    const updatedReplies = thisComment.replies.filter(reply => reply._id !== replyId);
    try {
      const updatedComment = {
        _id: thisComment._id,
        replies: updatedReplies
      };
      await partialUpdateComment(updatedComment);
    } catch (error) {
      console.error('Error deleting reply:', error);
    }
  };

  const handleReply = async (e) => {
    e.preventDefault();
    await handleAddReply(e, comment._id);
    hideReplyForm();
  };

  const handleSendDeleteReply = (replyId) => {
    handleDeleteReply(replyId);
  };

  const handleReplyKeyUp = async (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      await handleReply(e);
    }
  };

  const handleEditKeyUp = async (e) => {
    if (e.key === "Enter" && !e.shiftKey && editContent.trim() !== '') {
      await handleSaveEdit(e);
    }
  }

  const isLongComment = comment.content.length > 100;


  const handleProfileClick = (username) => {
    navigate(`/user/${username}`);
  };

  return (
    <div id="outercomment">
      {author && (
        <div  >
          <img onClick={() => handleProfileClick(comment.user)}  className='profilePic clickable' alt={author.username} src={author.image} height="50px" width="50px" ></img></div>
      )
      }

      <div className="comment" id="innercomment" key={comment._id}>
        <div className='clickable' id="commentUsername" onClick={() => handleProfileClick(comment.user)} >@{comment.user}</div>
        {isEditing ? (
          <div>
            <textarea
              ref={editTextareaRef}
              value={editContent}
              onChange={handleEditContentChange}
              onKeyUp={handleEditKeyUp}
              className="edit-textarea"
            />
            <div className="button-container">
              <button
                type="button"
                className="btn"
                onClick={handleSaveEdit}
                aria-label="Save edit"
              >
                Save
              </button>
              <button
                type="button"
                className="btn"
                onClick={handleCancelEdit}
                aria-label="Cancel edit"
              >
                Cancel
              </button>
            </div>
          </div>
        ) : (
          <p>{isExpanded || !isLongComment ? comment.content : `${comment.content.substring(0, 100)}...`}</p>
        )}
        {isLongComment && (
          <button className="btn" onClick={toggleReadMore}>
            {isExpanded ? 'Show less' : '...more'}
          </button>
        )}
        <div className="button-container">
          {(!isEditing && (comment.user === isSignedIn.username)) && (
            <>
              <button
                type="button"
                className="btn"
                onClick={() => setIsEditing(true)}
                aria-label="Edit comment"
              >

                <i className="bi bi-pencil"></i>
                <span className="icon-text">Edit</span>

              </button>
              <button
                type="button"
                className="btn "
                onClick={() => deleteComment(comment._id)}
                aria-label="Delete comment"
              >
                <i className="bi bi-trash"></i>
                <span className="icon-text">Delete</span>
              </button>
            </>
          )}
        </div>
        <div className='button-container-like-reply'>
          {(isSignedIn && userLikedComment) && (
            <button className="btn  " onClick={handleUnlikeComment}>
              <i className="bi bi-hand-thumbs-up-fill"></i>
              <span className="icon-text"> {totalUserLikes}</span>
            </button>
          )}

          {((isSignedIn && !userLikedComment) || !isSignedIn) && (
            <button className="btn  " onClick={handleLikeComment}>
              <i className="bi bi-hand-thumbs-up"></i>
              <span className="icon-text"> {totalUserLikes}</span>
            </button>
          )}
          {
            <button className="btn" onClick={showReplyForm}>
              {'Reply'}
            </button>
          }
        </div>
        {isReplyFormVisible && (
          <>
            <div className='newReply'>
              <div><img className='profilePic' alt={author.username} src={isSignedIn.image} height="50px" width="50px" ></img></div>
              <form onSubmit={handleReply}>
                <textarea
                  value={newReply[comment._id] || ''}
                  onChange={handleReplyContentChange}
                  onKeyUp={handleReplyKeyUp}
                  placeholder="Reply to this comment..."
                  className="reply-textarea"
                ></textarea>
                <div className="button-container">
                  <button className="btn " onClick={hideReplyForm}>
                    {'Cancel'}
                  </button>
                  <button
                    className="btn "
                    type="submit"
                    aria-label="Add reply"
                  >
                    Reply
                  </button>
                </div>
              </form>
            </div>

          </>
        )}
        <div>
          {currentCommentRepliesLength > 0 && (
            <button className="btn " onClick={toggleShowReplies}>
              {isShowingReplies ? '^ ' + comment.replies.length + ' replies' : '˅ ' + comment.replies.length + ' replies'}
            </button>
          )}
        </div>
        <div className={`replies ${isShowingReplies ? 'show' : 'hide'}`}>
          {currentCommentReplies.map((reply, index) => (
            <Reply
              key={index}
              reply={reply}
              rid={reply._id}
              handleDeleteReply={handleSendDeleteReply}
              handleCommentReplyChange={handleCommentReplyChange}
              comment={comment}
              isSignedIn={isSignedIn}
              partialUpdateComment={partialUpdateComment}

            />
          ))}
        </div>
      </div>
    </div >
  );
};

export default Comment;

// Reply.js
import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './Reply.css'
import config from '../../config';

const Reply = ({
  rid,
  reply,
  handleDeleteReply,
  comment,
  isSignedIn,
  handleCommentReplyChange,
  partialUpdateComment
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState(reply.content);
  const [isReplyFormVisible, setIsReplyFormVisible] = useState(false);
  const editTextareaRef = useRef(null);
  const [newReply, setNewReply] = useState({});
  const [author, setAuthor] = useState(null);

  const [thisReply, setThisReply] = useState(reply);
  const [usersLikedReply, setUsersLikedReply] = useState([]);
  const [userLikedReply, setUserLikedReply] = useState(false);
  const [totalUserLikes, setTotaluserLikes] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    if (isEditing && editTextareaRef.current) {
      editTextareaRef.current.style.height = 'auto';
      editTextareaRef.current.style.height = editTextareaRef.current.scrollHeight + 'px';
    }
    getAuthorByUserName(reply.user);
  }, [isEditing, editedContent, reply.user]);


  useEffect(() => {
    const fetchReply = () => {
      const currentReply = comment.replies.find(r => r._id === rid);
      if (currentReply) {
        setThisReply(currentReply);
        setUsersLikedReply(currentReply.usersLikes);
        setTotaluserLikes(usersLikedReply.length || 0);
        getAuthorByUserName(currentReply.user);
      }
      if (usersLikedReply && usersLikedReply.length > 0 && usersLikedReply.find(user => user === isSignedIn.username)) {
        setUserLikedReply(true);
      } else {
        setUserLikedReply(false);
      }
    };
    fetchReply();
  }, [rid, comment.replies, isSignedIn.username, usersLikedReply]);

  useEffect(() => {
    setUsersLikedReply(thisReply.usersLikes);
  }, [thisReply]);

  useEffect(() => {
    setUsersLikedReply(thisReply.usersLikes);
    if (usersLikedReply) {
      setTotaluserLikes(usersLikedReply.length);
    }
  }, [usersLikedReply, thisReply.usersLikes]);

  useEffect(() => {
    if (usersLikedReply && usersLikedReply.length > 0 && usersLikedReply.find(user => user === isSignedIn.username)) {
      setUserLikedReply(true);
    } else {
      setUserLikedReply(false);
    }
  }, [isSignedIn, usersLikedReply]);

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


  const handleLikeReply = () => {
    if (isSignedIn) {
      const newUsersLikes = [...thisReply.usersLikes, isSignedIn.username];
      const updatedReply = { ...thisReply, usersLikes: newUsersLikes };
      setThisReply(updatedReply);
      handleCommentReplyChange(updatedReply);

    }
    else {
      navigate('/signin');
    }
  };

  const handleUnlikeReply = () => {
    const newUsersLikes = thisReply.usersLikes.filter(user => user !== isSignedIn.username);
    const updatedReply = { ...thisReply, usersLikes: newUsersLikes };
    setThisReply(updatedReply);
    handleCommentReplyChange(updatedReply);
  };

  const handleEditReply = (editContent) => {
    const updatedReply = {
      ...thisReply,
      content: editContent
    };
    setThisReply(updatedReply);
    handleCommentReplyChange(updatedReply);
  };

  const handleAddReply = async (e, commentId) => {
    e.preventDefault();
    const replyContent = newReply[commentId];

    if (replyContent && replyContent.trim() !== '') {
      try {
        const newReplyObject = {
          user: isSignedIn.username,
          content: "@" + reply.user + " " + replyContent,
          date: Date.now(),
          usersLikes: [],

        };
        const updatedReplies = [...comment.replies, newReplyObject];
        const updatedComment = {
          _id: comment._id,
          replies: updatedReplies
        };
        setNewReply(prevState => ({ ...prevState, [commentId]: '' }));
        await partialUpdateComment(updatedComment);
      } catch (error) {
        console.error('Error adding reply:', error);
      }
    }
  };

  const handleReply = async (e) => {
    e.preventDefault();
    await handleAddReply(e, comment._id);
    hideReplyForm();
  };

  const toggleReadMore = () => {
    setIsExpanded(!isExpanded);
  };

  const toggleEdit = () => {
    setIsEditing(!isEditing);
  };



  const handleReplyChange = (e, commentId) => {
    const { value } = e.target;
    setNewReply(prevState => ({ ...prevState, [commentId]: value }));
  };

  const handleReplyContentChange = (e) => {
    handleReplyChange(e, comment._id);
    e.target.style.height = 'auto';
    e.target.style.height = e.target.scrollHeight + 'px';
  };

  const handleEditContentChange = (e) => {
    setEditedContent(e.target.value);
    e.target.style.height = 'auto';
    e.target.style.height = e.target.scrollHeight + 'px';
  };

  const handleSaveEdit = async () => {
    if (editedContent.trim() !== '') {
      await handleEditReply(editedContent);
    }
    else setEditedContent(thisReply.content);
    setIsEditing(false);
  };

  const handleCancelEdit = () => {
    setEditedContent(thisReply.content);
    setIsEditing(false);
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

  const isLongReply = reply.content.length > 100;

  const handleProfileClick = (username) => {
    navigate(`/user/${username}`);
  };



  const handleReplyEditKeyUp = async (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      await handleSaveEdit(e);
    }
  };

  const handleReplyKeyUp = async (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      await handleReply(e);
    }
  };


  return (
    <div id="outerreply">
      {author && (
        <div ><img className='profilePic clickable' onClick={() => handleProfileClick(reply.user)} alt={author.username} src={author.image} height="50px" width="50px" ></img></div>
      )}
      <div className="reply" id="innerreply" key={reply._id}>
        {!isEditing && (<>
          <div className='clickable' id="replyUsername" onClick={() => handleProfileClick(reply.user)}>@{reply.user}</div>
          <div>
            <p>{isExpanded ? reply.content : reply.content.substring(0, 100)}</p>
            {isLongReply && (
              <button className="btn" onClick={toggleReadMore}>
                {isExpanded ? "Show less" : "...more"}
              </button>
            )}
          </div>
        </>)
        }
        {!isEditing && (isSignedIn.username === reply.user) && (
          <div className="button-container">

            <button className="btn   " onClick={toggleEdit}>
              <i className="bi bi-pencil"></i>
              <span className="icon-text">Edit</span>
            </button>
            <button className="btn  " onClick={() => handleDeleteReply(reply._id)}>
              <i className="bi bi-trash"></i>
              <span className="icon-text">Delete</span>
            </button>

          </div>
        )}
        <div>
          {!isEditing && (
            <>
              <div className='button-container-like-reply'>
                {((isSignedIn && !userLikedReply) || !isSignedIn) && (
                  <button
                    className="btn   like-button"
                    onClick={() => handleLikeReply(reply._id)}
                    aria-label="Like reply"
                  >
                    <i className="bi bi-hand-thumbs-up"></i>
                    <span className="icon-text"> {totalUserLikes}</span>
                  </button>
                )}
                {(isSignedIn && userLikedReply) && (
                  <button
                    className="btn   unlike-button"
                    onClick={() => handleUnlikeReply(reply._id)}
                    aria-label="Unlike reply"
                  >
                    <i className="bi bi-hand-thumbs-up-fill"></i>
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
                    <div><img className='profilePic' alt={isSignedIn.username} src={isSignedIn.image} height="50px" width="50px" ></img></div>
                    <form onSubmit={handleReply}>
                      <textarea
                        placeholder={`@${reply.user} `}
                        value={newReply[comment._id] || ''}
                        onChange={handleReplyContentChange}
                        onKeyUp={handleReplyKeyUp}
                        className="reply-textarea"
                      ></textarea>
                      <div className="button-container">
                        <button className="btn" onClick={hideReplyForm}>
                          {'Cancel'}
                        </button>
                        <button
                          className="btn"
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

            </>
          )}
          {isEditing && (
            <div>
              <textarea
                ref={editTextareaRef}
                value={editedContent}
                onChange={handleEditContentChange}
                onKeyUp={handleReplyEditKeyUp}
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
          )}
        </div>
      </div>
    </div>
  );
};

export default Reply;

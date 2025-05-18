import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import './Comments.css';
import Comment from './Comment';
import config from '../../config';

const Comments = ({
  isSignedIn,
  videoId
}) => {
  const [newComment, setNewComment] = useState('');
  const [commentList, setCommentList] = useState([]);
  const [isCommentFormVisible, setIsCommentFormVisible] = useState(false);
  const commentTextareaRef = useRef(null);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const navigate = useNavigate();
  const initialRender = useRef(true);
  const [count, setCount] = useState(0);

  const getVideoComments = useCallback(async (pageToLoad) => {
    try {
      setLoading(true);
      const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}/comments?page=${pageToLoad}&limit=10`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('Failed to fetch video comments');
      }
      const data = await response.json();
      setCommentList(prevComments => [...prevComments, ...data.comments]);
      setCount(data.totalComments);

      if (data.comments.length < 10) {
        setHasMore(false);
      }
      setPage(pageToLoad + 1);

    } catch (error) {
      console.error('Error fetching video comments:', error);
    } finally {
      setLoading(false);
    }
  }, [videoId]);

  useEffect(() => {
    if (initialRender.current) {
      initialRender.current = false;
      return;
    }
    setCount(0);
    setIsCommentFormVisible(false);
    setNewComment('');
    setPage(1);
    setHasMore(true);
    setCommentList([]);
    setLoading(false);
    const getComments = async () => {
      await getVideoComments(1);
    };
    getComments();
  }, [videoId, getVideoComments]);

  useEffect(() => {
    if (commentTextareaRef.current) {
      commentTextareaRef.current.style.height = 'auto';
      commentTextareaRef.current.style.height = commentTextareaRef.current.scrollHeight + 'px';
    }
  }, [newComment]);

  useEffect(() => {
    const handleScroll = () => {
      if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 120 && !loading && hasMore) {
        getVideoComments(page);
      }
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  },);

  const addComment = async (comment) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(comment),
      });
      if (!response.ok) {
        throw new Error('Failed to add new comment');
      }
      const newCommentFromServer = await response.json();
      setCommentList(prevComments => [newCommentFromServer, ...prevComments]);
      setCount(count + 1);
    } catch (error) {
      console.error('Error adding new comment:', error);
    }
  };
  const handleAddComment = async (e) => {
    e.preventDefault();
    if (newComment.trim() !== '') {
      let author = isSignedIn.username;
      const commentToAdd = { content: newComment, replies: [], usersLikes: [], user: author, date: Date.now() }
      try {
        await addComment(commentToAdd);
      } catch (error) {
        console.error('Error Adding comment:', error);
      }
    }

    setIsCommentFormVisible(false);
    setNewComment('');
  };
  const handleDeleteComment = (commentId) => {
    const updatedComments = commentList.filter(comment => comment._id !== commentId);
    setCount(count - 1);
    setCommentList(updatedComments);
  };
  const handleCancelEdit = () => {
    setNewComment('');
    setIsCommentFormVisible(false);
  };
  const handleTextareaFocus = () => {
    if (isSignedIn) {
      setIsCommentFormVisible(true);

    }
    else {
      navigate('/signin');
    }
  };
  const handleTextareaInput = (e) => {
    e.target.style.height = 'auto';
    e.target.style.height = e.target.scrollHeight + 'px';
  };
  const handleCommentChange = (newComment) => {
    setCommentList(prevComments => {
      const commentIndex = prevComments.findIndex(comment => comment._id === newComment._id);

      let updatedComments;
      if (commentIndex !== -1) {
        updatedComments = prevComments.map((comment, index) => index === commentIndex ? newComment : comment);
      } else {
        updatedComments = [...prevComments, newComment];
      }
      return updatedComments;
    });
  };
  const handleAddKeyUp = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      handleAddComment(e);
      commentTextareaRef.current.blur();
    }
  }

  return (
    <div className="comments-container">
      <h2>{count} Comments</h2>
      {(isSignedIn) && (
        <>
          <div className='newComment'>
            <div><img src={isSignedIn.image} alt={isSignedIn.username} height="50px" width="50px" ></img></div>
            <form onSubmit={handleAddComment}>
              <textarea
                ref={commentTextareaRef}
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                onFocus={handleTextareaFocus}
                onInput={handleTextareaInput}
                onKeyUp={handleAddKeyUp}
                placeholder="Add your comment..."
                className="comment-textarea"
              ></textarea>
              {isCommentFormVisible && (
                <div className="button-container">
                  <button
                    type="button"
                    className="btn "
                    onClick={handleCancelEdit}
                    aria-label="Cancel editing"
                  >
                    Cancel
                  </button>
                  <button
                    className="btn"
                    type="submit"
                    aria-label={'Comment'}
                  >
                    {'Comment'}
                  </button>
                </div>
              )}
            </form>


          </div>

        </>
      )}
      {(!isSignedIn) && (
        <>
          <div className='newComment'>
            <div><img className='profilePic' alt='notSignedIn' src="/pictures/users/notSignedin.jpg" height="50px" width="50px" ></img></div>
            <form onSubmit={handleAddComment}>
              <textarea
                ref={commentTextareaRef}
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                onFocus={handleTextareaFocus}
                onInput={handleTextareaInput}
                placeholder="Add your comment..."
                className="comment-textarea"
              ></textarea>
            </form>
          </div>
        </>
      )}
      {commentList.map((comment, index) => (
        <Comment
          key={index}
          isSignedIn={isSignedIn}
          cid={comment._id}
          comment={comment}
          handleDeleteComment={handleDeleteComment}
          onCommentChange={handleCommentChange}
          videoId={videoId}
        />
      ))}
    </div>
  );
};

export default Comments;

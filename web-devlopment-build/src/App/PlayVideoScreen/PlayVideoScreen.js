import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './PlayVideoScreen.css';
import RelatedVideos from './RelatedVideos/RelatedVideos';
import Comments from './Comments/Comments';
import Description from './Description/Description';
import config from '../config';



const PlayVideoScreen = ({ toggleScreen, isSignedIn }) => {
  const { id } = useParams();
  const [video, setVideo] = useState(false);
  const [liked, setLiked] = useState(false);
  const [author, setAuthor] = useState(null);
  const [currentTitle, setCurrentTitle] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const navigate = useNavigate();
  const textareaRef = useRef(null);
  const [newTitle, setNewTitle] = useState(false);

  useEffect(() => {
    toggleScreen("PlayVideoScreen");
    const fetchVideo = async () => {
      await getVideo(id);
    };
    fetchVideo();
  }, [id, toggleScreen]);

  useEffect(() => {
    if (video) {
      getAuthorByUserName(video.username);
      setCurrentTitle(video.title);
      setIsEditing(false);
      setNewTitle(video.title);
    }
  }, [video]);

  const partialUpdateVideo = useCallback(async (updatedVideo) => {
    try {
      const originalVideo = await getVideoWithoutChangingState(updatedVideo._id);
      if (!originalVideo) {
        throw new Error('Video not found in the local state');
      }
      // Prepare an object to store updated fields
      const updatedFields = Object.keys(updatedVideo).reduce((fields, key) => {
        if (updatedVideo[key] !== originalVideo[key]) {
          fields[key] = updatedVideo[key];
        }
        return fields;
      }, {});
      updatedFields._id = originalVideo._id;

      // Update the video with the updated fields
      const updatedVideoFromServer = await sendUpdateRequest(updatedFields, 'PATCH');
      setVideo(updatedVideoFromServer);
    } catch (error) {
      console.error('Error updating video:', error);
    }
  }, []);

  useEffect(() => {
    window.scrollTo(0, 0);
    const addVideoView = async (videoId) => {
      try {
        const videoToUpdate = await getVideoWithoutChangingState(videoId);
        if (!videoToUpdate) {
          throw new Error('Video not found');
        }
        const updatedVideo = {
          _id: id,
          views: videoToUpdate.views + 1
        };
        await partialUpdateVideo(updatedVideo);
      } catch (error) {
        console.error('Error viewing video:', error);
      }
    };
    addVideoView(id);
  }, [id, partialUpdateVideo]);


  useEffect(() => {
    if (isSignedIn && video && video.usersLikes.find(user => user === isSignedIn._id)) {
      setLiked(true);
    }
    else {
      setLiked(false)
    }
  }, [video, video.usersLikes, isSignedIn]);


  const getVideo = async (videoId) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('Failed to fetch video');
      }
      const videoFromServer = await response.json();
      setVideo(videoFromServer);
    } catch (error) {
      console.error('Error fetching video:', error);
    }
  };

  const getVideoWithoutChangingState = async (videoId) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('Failed to fetch video');
      }
      const videoFromServer = await response.json();
      return videoFromServer;
    } catch (error) {
      console.error('Error fetching video:', error);
    }
  };

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
  /* 
  const addVideo = async (newVideo) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/videos`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + `${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(newVideo),
      });
      if (!response.ok) {
        throw new Error('Failed to add new video');
      }
      const newVideoFromServer = await response.json();
    } catch (error) {
      console.error('Error adding new video:', error);
    }
  };
  const updateVideo = async (updatedVideo) => {
    try {
      const updatedVideoFromServer = await sendUpdateRequest(updatedVideo, 'PUT');
    } catch (error) {
      console.error('Error updating video:', error);
    }
  }; */


  const sendUpdateRequest = async (updatedVideo, method) => {
    const url = `${config.apiBaseUrl}/api/videos/${updatedVideo._id}`;
    let bodyData = {};

    if (method === 'PATCH') {
      // Construct bodyData with only the updated fields
      const fieldsToUpdate = ['title', 'description', 'source', 'thumbnail', 'tags', 'upload_date', 'duration', 'username', 'likeCount', 'views', 'usersLikes', 'comments'];
      bodyData = {};
      fieldsToUpdate.forEach(field => {
        if (updatedVideo.hasOwnProperty(field)) {
          bodyData[field] = updatedVideo[field];
        }
      });
    } else if (method === 'PUT') {
      // For PUT, send the entire updatedVideo object
      bodyData = updatedVideo;
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
        throw new Error('Failed to update video');
      }
      return await response.json();
    } catch (error) {
      console.error('Error updating video:', error);
      throw error; // Rethrow the error for handling in the calling function
    }
  };
  const deleteVideo = async (video) => {
    const videoId = video._id;
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/videos/${videoId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete video');
      }
    } catch (error) {
      console.error('Error deleting video:', error);
    }
  };

  const likeVideo = async () => {
    try {
      const updatedVideo = {
        _id: id,
        likeCount: video.likeCount + 1,
        usersLikes: [...video.usersLikes, isSignedIn]
      };
      await partialUpdateVideo(updatedVideo);
    } catch (error) {
      console.error('Error liking video:', error);
    }
  };
  const unlikeVideo = async () => {
    try {
      const updatedVideo = {
        _id: id,
        likeCount: Math.max(video.likeCount - 1, 0),
        usersLikes: video.usersLikes.filter(user => user !== isSignedIn._id)
      };
      await partialUpdateVideo(updatedVideo);
    } catch (error) {
      console.error('Error unliking video:', error);
    }
  };
  const handleSaveDescription = async (newDescription) => {
    try {
      const updatedVideo = {
        _id: id,
        description: newDescription
      };
      await partialUpdateVideo(updatedVideo);
    } catch (error) {
      console.error('Error saving Description:', error);
    }
  };

  const handleLike = () => {
    if (!isSignedIn) {
      navigate('/signin');
    }
    else {
      likeVideo(video._id);
    }
  };
  const handleUnlike = () => {
    unlikeVideo(video._id);
  };
  const handleSaveClick = async () => {
    if (newTitle === "") {
      handleCancelClick()
    }
    else {
      setCurrentTitle(newTitle);
      setIsEditing(false);

      try {
        const updatedVideo = {
          _id: id,
          title: newTitle
        };
        await partialUpdateVideo(updatedVideo);
      } catch (error) {
        console.error('Error saving Title:', error);
      }
    }
  };

  const handleDeleteClick = () => {
    setIsEditing(false);
    deleteVideo(video);
    navigate('/');
  };
  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancelClick = () => {
    setNewTitle(currentTitle);
    setIsEditing(false);
  };

  const handleShare = async () => {
    try {
      if (navigator.share) {
        await navigator.share({
          title: `Check out this video: ${currentTitle}`,
          text: video.description,
          url: `/video/${id}`
        });
        console.log('Successfully shared.');
      } else {
        console.error('Web Share API is not supported in your browser.');}
    } catch (error) {
      console.error('Error sharing:', error);
    }
  };

  const handleTextareaChange = () => {
    setNewTitle(textareaRef.current.value);
  };

  const handleProfileClick = (username) => {
    navigate(`/user/${username}`);
  };

  const handleKeyUp = async (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      await handleSaveClick(e);
    }
  }

  while (!(video && author)) {
    if (!video) {
      getVideo(id);
    }
    else if (!author) {
      getAuthorByUserName(video.username);
    }
    return (

      <div className='loading'>
          <h3>Loading...</h3>
      </div>

    );
  }

  return (
    <div className='PlayVideoScreen'>
      <div className="videoContainer">
        <video src={video.source} type="video/mp4" className="VideoPlayer" controls />
        {!isEditing && (
          <>
            <div className="videoTitle">{currentTitle}</div>

            <div className="videoProfile">
              <div id="profilepicandname">
                <img className='profilePic clickable' onClick={() => handleProfileClick(author.username)} alt={author.username} src={author.image} height="50px" width="50px" ></img>
                <div className='clickable' id="profilename" onClick={() => handleProfileClick(author.username)} >
                  {author.username}
                </div>

              </div>
              <div className="buttonContainer">
                {isSignedIn && liked && (
                  <div>

                    <button type="button" className="btn" onClick={handleUnlike}>
                      <i className="bi bi-hand-thumbs-up-fill"></i>
                      <span className="icon-text"> {video.likeCount}</span>
                    </button>
                  </div>
                )}

                {((isSignedIn && !liked) || !isSignedIn) && (
                  <div>
                    <button type="button" className="btn" onClick={handleLike}>
                      <i className="bi bi-hand-thumbs-up"></i>
                      <span className="icon-text"> {video.likeCount}</span>
                    </button>
                  </div>
                )}
                <button type="button" className="btn" onClick={handleShare}>
                  <i className="bi bi-share"></i>
                  <span id="shareVideo" className="icon-text"> Share</span>
                </button>

                {((isSignedIn.username === video.username)) && (
                  <>
                    <button type="button" className="btn" onClick={handleEditClick}>
                      <i className="bi bi-pencil"></i>
                      <span id="editVideo" className="icon-text"> edit</span>
                    </button>

                    <button type="button" className="btn" onClick={handleDeleteClick}>
                      <i className="bi bi-trash"></i>
                      <span id="deleteVideo" className="icon-text"> Delete</span>
                    </button>
                  </>
                )}
              </div>
            </div>
          </>
        )}
        {isEditing && (
          <>
            <textarea
              ref={textareaRef}
              id="title-textarea"
              name="title"
              value={newTitle}
              onChange={handleTextareaChange}
              onKeyUp={handleKeyUp}
            ></textarea>

            <div className="videoProfile">
              <div id="profilepicandname">
                <img className='profilePic' alt={author.username} src={author.image} height="50px" width="50px" ></img>
                <div id="profilename">
                  {author.username}
                </div>

              </div>
              <div className="buttonContainer">

                <button type="button" className="btn" onClick={handleSaveClick}>
                  Save
                </button>

                <button type="button" className="btn" onClick={handleCancelClick}>
                  Cancel
                </button>

              </div>
            </div>
          </>
        )}

        <Description views={video.views} description={video.description} username={video.username} isSignedIn={isSignedIn} onSave={handleSaveDescription} />
        <div className="sidebarSmall">
          <RelatedVideos id={id} itsBig={false} />
        </div>
        <Comments
          videoId={video._id}
          isSignedIn={isSignedIn}
        />
      </div>
      <div className="sidebarBig">
        <RelatedVideos id={id} itsBig={true} />
      </div>
    </div>
  );
};

export default PlayVideoScreen;

import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './UserProfile.css';
import UserBox from './UserBox/UserBox'
import config from '../config';




const UserProfile = ({ toggleScreen }) => {
  const [userVideos, setUserVideos] = useState([]);
  const [author, setAuthor] = useState(null);
  const { key } = useParams();
  const items = Array.from({ length: 10 });
  const [loading, setLoading] = useState(false);



  useEffect(() => {
    toggleScreen("UserProfile");
    window.scrollTo(0, 0);
  },);


  useEffect(() => {
    const getAuthorByUserName = async (key) => {
      try {
        setLoading(true);
        const response = await fetch(`${config.apiBaseUrl}/api/users/username/${key}`, {
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
      } finally {
        setLoading(false);
      }
    };
    const getVideosByKeyAndFilter = async (filter1, key) => {
      try {
        setLoading(true);
        // Fetch videos data
        const videosResponse = await fetch(`${config.apiBaseUrl}/api/videos/all?${filter1}=${key}`);
        const videosData = await videosResponse.json();
        setUserVideos(shuffleArray([...videosData]));
      } catch (error) {
        console.error('Error fetching videos:', error);
      } finally {
        setLoading(false);
      }
    };
    const fetchVideos = async () => {
      await getVideosByKeyAndFilter("username", key);
    };
    getAuthorByUserName(key);
    fetchVideos();
  }, [key]);

  const shuffleArray = (array) => {
    const shuffledArray = [...array];
    for (let i = shuffledArray.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [shuffledArray[i], shuffledArray[j]] = [shuffledArray[j], shuffledArray[i]];
    }
    return shuffledArray;
  };

  return (
    <div className="userVideos">
      <div className="userProfile">

        {!(author === null) && (
          <div className="userProfile">
            <img alt={author.username} src={author.image} className='userProfileImage' ></img>
            <div className='userDetails'>
              <p className='userName' id="displayNameUserProfile">{author.displayname}</p>
              <p className='userName'>@{author.username} • {userVideos.length} videos</p>
            </div>
          </div>
        )}

      </div>
      {loading && ( <div className="loading">Loading...</div>)}


      {(!loading && author === null) && (
        <div>
          <p></p>
          <h1> No Such User</h1>
        </div>
      )}
      {userVideos.length === 0 && !(author === null) && (
        <div>
          <p></p>
          <h1> No Videos</h1>
        </div>
      )}
      <ul>
        {userVideos.map((video, index) => (
          <UserBox
            key={index}
            video={video}
          />
        ))}
        {items.map((_, index) => (
          <li key={index} className='hidden-flex-item' ></li>
        ))}
      </ul>
    </div>
  );
};

export default UserProfile;
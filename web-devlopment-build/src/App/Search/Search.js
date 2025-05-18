import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './Search.css';
import VideoBox from './VideoBox/VideoBox';
import config from '../config';


const Search = ({ toggleScreen }) => {
  const [searchVideos, setSearchVideos] = useState([]);
  const { key } = useParams();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    toggleScreen("Search");
    window.scrollTo(0, 0);
  },);

  useEffect(() => {
    const getVideosByKeyAndFilter = async (filter1, filter2, key) => {
      try {
        setLoading(true);
        // Fetch videos data
        const videosResponse = await fetch(`${config.apiBaseUrl}/api/videos/all?${filter1}=${key}&${filter2}=${key}`);
        const videosData = await videosResponse.json();
        setSearchVideos(shuffleArray([...videosData]));
      } catch (error) {
        console.error('Error fetching videos:', error);
      } finally {
        setLoading(false);
      }
    };
    const fetchVideos = async () => {
      await getVideosByKeyAndFilter("title", "username", key);
    };
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

  while (loading) {
    return (
      <div className="searchVideos">
        <h3>Loading...</h3>
      </div>
    );
  }

  return (
    <div className="searchVideos">
      <ul>
        {searchVideos.length === 0 && (
          <div>
            <p></p>
            <h3 className='noResults'>No Results Found</h3>
          </div>
        )}
        {searchVideos.map((video, index) => (
          <VideoBox
            key={index}
            video={video}
          />
        ))}
      </ul>
    </div>
  );
};

export default Search;
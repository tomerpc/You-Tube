import { React, useEffect, useState } from 'react';
import RecommendedVideos from './RecommendedVideos/RecommendedVideos';
import config from '../config';

import './Home.css';


const Home = ({ toggleScreen, menuOpen }) => {
  const [recommendedVideos, setRecommendedVideos] = useState([]);
  const [loading, setLoading] = useState(false);


  useEffect(() => {
    toggleScreen("Home");
    getVideos();
    window.scrollTo(0, 0);
  }, [toggleScreen]);

  const getVideos = async () => {
    try {
      setLoading(true);
      // Fetch videos data
      const videosResponse = await fetch(`${config.apiBaseUrl}/api/videos`);
      const videosData = await videosResponse.json();
      setRecommendedVideos(videosData);
    } catch (error) {
      console.error('Error fetching videos:', error);
    } finally {
      setLoading(false);
    }
  };
  while (loading) {
    return (
      <div className="loading">
        <h3>Loading...</h3>
      </div>
    );
  }

  return (
    <div>
      <div className={`${menuOpen ? 'opend' : 'closed'}`}>
        <RecommendedVideos videos={recommendedVideos} menuOpen={menuOpen} />
      </div>
    </div>
  );
};

export default Home;

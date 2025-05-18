import React, { useState, useEffect } from 'react';
import './RecommendedVideos.css';
import RecommendedBox from './RecommendedBox/RecomendedBox';


const RecommendedVideos = ({ videos, menuOpen}) => {
  const [relatedVideos, setRelatedVideos] = useState([]);
  useEffect(() => {
    setRelatedVideos(shuffleArray(videos));
  }, [videos]);
  const items = Array.from({ length: 10 });

  const shuffleArray = (array) => {
    const shuffledArray = [...array];
    for (let i = shuffledArray.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [shuffledArray[i], shuffledArray[j]] = [shuffledArray[j], shuffledArray[i]];
    }
    return shuffledArray;
  };
  
  return (
    <div className={`RecommendedVideos ${menuOpen ? 'RecommendedVideosOpen' : 'RecommendedVideosClose'}`}>
      <ul>
        {relatedVideos.map((video, index) => (
          <RecommendedBox
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

export default RecommendedVideos;

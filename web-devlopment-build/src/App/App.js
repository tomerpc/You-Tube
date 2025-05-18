// App.js
import React, { useState, useEffect, useCallback, useRef } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import config from './config';

import TopBar from './TopBar/TopBar';
import Menu from './Menu/Menu';
import PlayVideoScreen from './PlayVideoScreen/PlayVideoScreen';
import Home from './Home/Home';
import DropDown from './DropDown/DropDown';
import SignIn from './SignIn/SignIn';
import './App.css';
import CreateAccount from './SignIn/CreateAccount';
import Search from './Search/Search';
import UserProfile from './UserProfile/UserProfile';
import EditAccount from './SignIn/EditAccount';
import AddVideo from './AddVideo/AddVideo'; // Import the AddVideo component

function App() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [dropDownOpen, setDropDownOpen] = useState(false);
  const [theme, setTheme] = useState('light');
  const [screen, setScreen] = useState(false);
  const [isSignedIn, setSignedInStatus] = useState(() => {
    const savedStatus = localStorage.getItem('isSignedIn');
    return savedStatus ? JSON.parse(savedStatus) : false;
  });
  const dropdownRef = useRef(null);
  const bigProfilePicRef = useRef(null); 
  const smallProfilePicRef = useRef(null);

  useEffect(() => {
  }, [dropDownOpen]);

  useEffect(() => {
    localStorage.setItem('isSignedIn', JSON.stringify(isSignedIn));
  }, [isSignedIn]);

  useEffect(() => {
    document.body.className = theme;
  }, [theme]);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  const toggleDropDown = () => {
    setDropDownOpen(!dropDownOpen);
  };

  const toggleTheme = () => {
    setTheme((prevTheme) => (prevTheme === 'light' ? 'dark' : 'light'));
  };

  const getUserByUserName = async (username) => {
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
      setSignedInStatus(userFromServer);
    } catch (error) {
      console.error('Error fetching user:', error);
    }
  };
  
  const toggleSignendIn = async (username) => {

    if (username) {
      try {
        await getUserByUserName(username);
      } catch (error) {
        console.error('Error fetching user:', error);
      }
    }
    else {
      localStorage.setItem('token', '');
      setSignedInStatus(false);
    }
  };

  const toggleScreen = useCallback((screen) => {
    setScreen(screen);
  }, []);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target) &&
        !(bigProfilePicRef.current && bigProfilePicRef.current.contains(event.target)) &&
        !(smallProfilePicRef.current && smallProfilePicRef.current.contains(event.target))
      ) {
        setDropDownOpen(false);
      }
    };
    if (dropDownOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    } else {
      document.removeEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [dropDownOpen]);

  return (
    <Router>
      <div className={`App ${theme}`}>
        {!(screen === "SignIn" || screen === "CreateAccount" || screen === "EditAccount") && (
          <>
            <TopBar smallProfilePicRef={smallProfilePicRef} bigProfilePicRef={bigProfilePicRef} setDropDownOpen={setDropDownOpen} dropDownOpen={dropDownOpen} theme={theme} toggleMenu={toggleMenu} toggleDropDown={toggleDropDown} isSignedIn={isSignedIn} />
            <Menu screen={screen} isOpen={menuOpen} />
            {(isSignedIn && (
              <DropDown dropdownRef={dropdownRef} setIsOpen={setDropDownOpen} isOpen={dropDownOpen} isSignedIn={isSignedIn} toggleTheme={toggleTheme} toggleSignendIn={toggleSignendIn} />
            ))
            }
            {(menuOpen && screen !== "Home") && (<div className="Overlay" onClick={toggleMenu}></div>)}
          </>
        )}

        <Routes>
          <Route path="/search/:key" element={<Search toggleScreen={toggleScreen} />} />
          <Route path="/user/:key" element={<UserProfile toggleScreen={toggleScreen} />} />
          <Route path="/signin" element={<SignIn
            toggleScreen={toggleScreen}
            isSignedIn={isSignedIn}
            toggleSignendIn={toggleSignendIn}
          />} />
          <Route path="/createaccount" element={<CreateAccount
            isSignedIn={isSignedIn}
            toggleScreen={toggleScreen}
            toggleSignendIn={toggleSignendIn}
          />} />
          <Route path="/editaccount" element={<EditAccount
            isSignedIn={isSignedIn}
            toggleScreen={toggleScreen}
            toggleSignendIn={toggleSignendIn}
          />} />
          <Route path="/" element={<Home
            toggleScreen={toggleScreen}
            isSignedIn={isSignedIn}
            menuOpen={menuOpen}
          />} />
          <Route path="/video/:id" element={<PlayVideoScreen
            toggleScreen={toggleScreen}
            isSignedIn={isSignedIn}
          />} />
          <Route path="/AddVideo" element={<AddVideo
            isSignedIn={isSignedIn}
            toggleScreen={toggleScreen}
          />} /> {/* AddVideo Route */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;

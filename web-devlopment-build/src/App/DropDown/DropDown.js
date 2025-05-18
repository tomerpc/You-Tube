import React from 'react';
import { useNavigate } from 'react-router-dom';
import './DropDown.css';
import config from '../config';

const DropDown = ({ isOpen, topBarHeight, toggleTheme, isSignedIn, toggleSignendIn, setIsOpen, dropdownRef }) => {
  const navigate = useNavigate();


  const handleDeleteAccount = async () => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/api/users/${isSignedIn._id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
      });
      if (!response.ok) {
        throw new Error('Failed to delete Account');
      }
      else {
        handleLogOut();
      }
    } catch (error) {
      console.error('Error deleting Account:', error);
    }
  };


  const dropDownStyle = {
    bottom: topBarHeight + 'px',
  };

  const handleLogOut = () => {
    toggleSignendIn(false);
    navigate('/');
    setIsOpen(false);
  };

  const handleEditAccount = () => {
    navigate('/editaccount');
    setIsOpen(false);
  };



  const handleProfileClick = (username) => {
    navigate(`/user/${username}`);
    setIsOpen(false);
  };


  return (
    <div ref={dropdownRef} className={`DropDown ${isOpen ? 'open' : ''}`} style={dropDownStyle}>
      <div className="profile">
        {isSignedIn && (
          <>
            <div className='dropDownList'>
              <div className='clickable profile-container' onClick={() => handleProfileClick(isSignedIn.username)}>
                <img src={isSignedIn.image} height="50px" width="50px" alt="Profile" />
                <div>
                  <div>{isSignedIn.displayname}</div>
                  <div>@{isSignedIn.username}</div>
                </div>
              </div>

              <p></p>

              <button className="theme-toggle btn dropDownButton" onClick={toggleTheme}>
                <i className="bi bi-moon"></i>
                <span className="icon-text"> Toggle Theme</span>
              </button>
              <button className="dropDownButton btn" onClick={handleEditAccount}>
                <i className="bi bi-pencil"></i>
                <span className="icon-text"> Edit Account</span>
              </button>
              <button className="dropDownButton btn" onClick={handleDeleteAccount}>
                <i className="bi bi-trash"></i>
                <span className="icon-text"> Delete Account</span>
              </button>
              <button className="dropDownButton btn" onClick={handleLogOut}>
                <i className="bi bi-box-arrow-right"></i>
                <span className="icon-text"> Sign out</span>
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default DropDown;

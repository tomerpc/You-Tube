import React, { useState, useRef, useEffect } from 'react';
import './Description.css';

const Description = ({ description, onSave, isSignedIn, username, views }) => {
  const [currentDescription, setCurrentDescription] = useState(description);
  const [savedDescription, setSavedDescription] = useState(description);
  const [isEditing, setIsEditing] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);
  const textareaRef = useRef(null);

  useEffect(() => {
    setCurrentDescription(description);
  }, [description]);

  useEffect(() => {
    if (textareaRef.current && isEditing) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;
    }
  }, [isEditing]);

  const handleEditClick = () => {
    setIsEditing(true);
    setIsExpanded(true);
  };

  const handleSaveClick = async () => {
    if (currentDescription && currentDescription.trim() !== '') {
      await onSave(currentDescription);
      setSavedDescription(currentDescription);
    }
    else{
      setCurrentDescription(savedDescription);

    }
      setIsEditing(false);
  };

  const handleCancelClick = () => {
    setCurrentDescription(savedDescription);
    setIsEditing(false);
  };

  const handleTextareaChange = () => {
    setCurrentDescription(textareaRef.current.value);
    textareaRef.current.style.height = 'auto';
    textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;
  };

  const handleKeyUp = async (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      await handleSaveClick(e);
    }
  }

  const toggleReadMore = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="description-container">
      {isEditing ? (
        <div className="description-edit">
          <textarea
            ref={textareaRef}
            id="description-textarea"
            name="description"
            value={currentDescription}
            onChange={handleTextareaChange}
            onKeyUp={handleKeyUp}
          ></textarea>
          <div className="button-container">
            <button
              className="btn "
              onClick={handleSaveClick}
              aria-label="Save description"
            >
              Save
            </button>
            <button
              className="btn"
              onClick={handleCancelClick}
              aria-label="Cancel editing"
            >
              Cancel
            </button>
          </div>
        </div>
      ) : (
        <div className="description-view">
          <div>{views} views</div>
          <p>{isExpanded ? currentDescription : currentDescription.substring(0, 200)}</p>
          {currentDescription.length > 200 && (
            <button className="btn " onClick={toggleReadMore}>
              {isExpanded ? "Show less" : "...more"}
            </button>
          )}
          {(username === isSignedIn.username) && (
            <div className="button-container">
              <button
                className="btn"
                onClick={handleEditClick}
                aria-label="Edit description"
              >

                <i className="bi bi-pencil"></i>
                <span className="icon-text">Edit</span>


              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Description;

import React, { useState, useEffect } from 'react';
import './EditAccount.css';
import { useNavigate } from 'react-router-dom';
import config from '../config';

const EditAccount = ({ toggleScreen, isSignedIn, toggleSignendIn }) => {
    const [displayname, setDisplayname] = useState('');
    const [password, setPassword] = useState('');
    const [passwordAgain, setPasswordAgain] = useState('');
    const [error, setError] = useState('');
    const [image, setImage] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        toggleScreen("EditAccount");
        if (!isSignedIn) {
            navigate("/");
        }
    });

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setImage(reader.result);
            };
            reader.readAsDataURL(file);
        } else {
            setImage('');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        // Construct the payload

        const payload = { _id: isSignedIn._id, username: isSignedIn.username };
        if (displayname !== '') {
            payload.displayname = displayname;
        }

        if (password !== '') {
            payload.password = password;
        }

        if (passwordAgain !== '') {
            payload.passwordAgain = passwordAgain;
        }

        if (image !== null) {
            payload.image = image;
        }
        try {
            // Send the registration data to the server
            const response = await fetch(`${config.apiBaseUrl}/api/users/${isSignedIn._id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(payload)
            });

            const result = await response.json();

            if (!response.ok) {
                // If the server responds with an error, set the error message
                setError(result.message);
                return;
            }
            toggleSignendIn(isSignedIn.username);
            navigate("/");
        } catch (error) {
            setError('An error occurred. Please try again.');
            console.error('Error:', error);
        }
    };


    return (
        <div className="container">
            <div className="create-account-box">
                <h2>Edit Account</h2>
                <p>Fill in the fields you want to change</p>
                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <label htmlFor="displayname">First and last Name</label>
                        <input
                            type="text"
                            id="displayname"
                            placeholder="Enter both first and last name"
                            value={displayname}
                            onChange={(e) => setDisplayname(e.target.value)}
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            placeholder="Password length need to be atleast 8"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="passwordAgain">Retype Password</label>
                        <input
                            type="password"
                            id="passwordAgain"
                            placeholder="Include both letters and numbers "
                            value={passwordAgain}
                            onChange={(e) => setPasswordAgain(e.target.value)}
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="image">Profile Image</label>
                        <input
                            type="file"
                            id="image"
                            accept="image/*"
                            onChange={handleImageChange}
                        />
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    <button type="submit" className="create-account">Edit Account</button>

                </form>
            </div>
        </div>
    );
};

export default EditAccount;

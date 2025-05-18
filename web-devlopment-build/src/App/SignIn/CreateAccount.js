import React, { useState, useEffect } from 'react';
import './CreateAccount.css';
import { useNavigate } from 'react-router-dom';
import config from '../config';

const CreateAccount = ({ toggleSignendIn, toggleScreen, isSignedIn }) => {
    const [displayname, setDisplayname] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [passwordAgain, setPasswordAgain] = useState('');
    const [error, setError] = useState('');
    const [image, setImage] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        toggleScreen("CreateAccount");
        if (isSignedIn) {
            navigate("/");
        }
    });

    const logIn = async () => {
        localStorage.setItem('token', '');
        const data = { username: username, password: password }
        try {
            const response = await fetch(`${config.apiBaseUrl}/api/tokens`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                throw new Error('Failed to fetch token');
            }
            const { token } = await response.json();
            localStorage.setItem('token', token);
            return true
        } catch (error) {
            console.error('Error loggin in:', error);
            return false
        }
    };

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
        const payload = { username, displayname, password, passwordAgain, image };
        try {
            // Send the registration data to the server
            const response = await fetch(`${config.apiBaseUrl}/api/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const result = await response.json();

            if (!response.ok) {
                // If the server responds with an error, set the error message
                setError(result.message);
                return;
            }

            // If registration is successful, proceed with login or other actions
            await logIn();
            toggleSignendIn(username);
            navigate("/");
        } catch (error) {
            setError('An error occurred. Please try again.');
            console.error('Error:', error);
        }
    };


    return (
        <div className="container">
            <div className="create-account-box">
                <h2>Create Account</h2>
                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <label htmlFor="displayname">First and last Name</label>
                        <input
                            type="text"
                            id="displayname"
                            placeholder="Enter both first and last name"
                            value={displayname}
                            onChange={(e) => setDisplayname(e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="username">Username</label>
                        <input
                            type="text"
                            id="username"
                            placeholder="Enter a unique Username "
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
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
                            required
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
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="image">Profile Image</label>
                        <input
                            type="file"
                            id="image"
                            accept="image/*"
                            onChange={handleImageChange}
                            required
                        />
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    <button type="submit" className="create-account">Create Account</button>

                </form>
            </div>
        </div>
    );
};

export default CreateAccount;

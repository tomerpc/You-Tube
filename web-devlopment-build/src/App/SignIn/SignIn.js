import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './SignIn.css';
import config from '../config';

const SignIn = ({ toggleScreen, isSignedIn, toggleSignendIn }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(false);
    const navigate = useNavigate();

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


    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSignedIn) {
            toggleScreen("Home")
            navigate("/");
        }
        else {
            if (await logIn()) {
                setError(false);
                toggleSignendIn(username);
                toggleScreen("Home");
                navigate("/");
            } else {
                setError(true);
            }
        }
        setError(true);
    }

    const handleCreateAccount = () => {
        toggleScreen("CreateAccount")
        navigate("/createaccount");
    };


    useEffect(() => {
        toggleScreen("SignIn");
    });

    return (
        <div className='container'>
            <div className="signin-box">
                <h2>Sign In</h2>
                {(
                    <form onSubmit={handleSubmit}>
                        <div className="input-group">
                            <label htmlFor="username">Username</label>
                            <input
                                type="text"
                                id="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />
                            <label htmlFor="password">Password</label>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        {error && <div className="error-message">Invalid username and/or password</div>}
                        <div className="userNameButtons">

                            <button type="button" className="SignIn btn   " onClick={handleCreateAccount}>Create account</button>
                            <button type="submit" className='SignIn btn   '>Next</button>

                        </div>
                    </form>
                )}
            </div>

        </div>
    );
};

export default SignIn;

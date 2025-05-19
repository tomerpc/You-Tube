# YouTube-Clone – Full-Stack App with Web, Android, Server & Database

This project is a **YouTube-like video platform** built as a full-stack application including:

- A responsive **web application**
- A native **Android application**
- A **Node.js + C++ backend server**
- A **MongoDB** database (for video/user data)
- A **Room database** (for local data persistence on Android)
- Structured with a clean **MVVM architecture** on Android

---

## 🔧 Technologies Used

### 🖥️ Web:
- React.js (frontend)
- RESTful API integration

### 📱 Android:
- MVVM architecture
- Retrofit for API calls
- Room for local persistence

### 🌐 Server:
- Node.js (API server)
- C++ module (cpp_server) used for optimized processing and implementing a recommendation algorithm that delivers relevant videos to the user
- MongoDB (data storage)

📸 Full visual documentation and app screenshots can be found in the [`wiki/`](./wiki) directory, including separate guides for the web and Android versions:
- [📱 Android App Visual Guide](./wiki/Android_readMe.md)
- [🌐 Web App Visual Guide](./wiki/Web_readMe.md)

---

## 🚀 Running the Server

To run the server, follow these steps:

1. Open your terminal or command prompt.
2. navigate to the dirctory where the web development build is located (web-development-build).
3. Open the .env file and add the following line with your local IP:
   ```
   REACT_APP_API_BASE_URL = 'http://<your_local_IP>:4000
    ```

   
3. run the following command to install the required packages:
    ```
   npm install
    ```

4. Run the following command to build the production version of the web development files:
    ```
   npm run build
    ```

5. Navigate to the directory where the server files are located : /server.
6. navigate to the data directory: server/data.
7. run the script as instructed in the README.md file in the data directory.
8. return to server directory and run the following command to install the required packages:
    ```
   npm install
    ```
9. after that Run the following commands to start the servers:
    ```
   ./cpp_server
   node server.js
    ```
10. The server should now be running and accessible. Check the terminal output for the address and port it is listening on, it should be `http://localhost:4000`.

11. For the Android part you can open the project through IDE and run it on the emulator.
    If you want to run it on your personal device you will need to change line 16 and 17 on res/values/strings.xml
    to
    ```
    <string name="BaseURL">http://<yourIP>:4000/api/</string>
    <string name="Base_Url">http://<yourIP>:4000/</string>
    ```
    Notice that you have to connect at your phone to the same Wifi as your PC connected to.



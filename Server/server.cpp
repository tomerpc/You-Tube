#include <iostream>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <vector>
#include <algorithm>
#include <cstring>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <thread>
#include <mutex>

using namespace std;

// Simple in-memory structures to store user watch history and video watchers
unordered_map<string, unordered_set<string>> watchHistory;  // userId -> list of videoIds
unordered_map<string, unordered_set<string>> relatedVideos; // videoId -> list of videoIds

// Mutex for synchronizing access to global data structures
mutex mtx;

// Function to handle each client connection
void handleClient(int client_socket)
{
    // Buffer to hold received data
    char buffer[1024] = {0};
    read(client_socket, buffer, 1024);

    // Process the received message
    string request(buffer);
    if (request.find("WATCH") == 0)
    {
        size_t pos1 = request.find(" ") + 1;
        size_t pos2 = request.find(" ", pos1);
        string userId = request.substr(pos1, pos2 - pos1);
        string videoId = request.substr(pos2 + 1);
        videoId = videoId.substr(0, videoId.find("\n"));

        // Update the watch history
        mtx.lock();
        watchHistory[userId].insert(videoId);
        for (const auto &video : watchHistory[userId])
        {
            relatedVideos[videoId].insert(video);
            relatedVideos[video].insert(videoId);
        }
        
        unordered_set<string> recommendationsSet = relatedVideos[videoId];
        vector<string> recommendations(recommendationsSet.begin(), recommendationsSet.end());

        // Format recommendations as a JSON-like response
        string response = "{ \"recommendations\": [";
        for (size_t i = 0; i < recommendations.size(); ++i)
        {
            response += "\"" + recommendations[i] + "\"";
            if (i < recommendations.size() - 1)
                response += ", ";
        }
        response += "] }";
        mtx.unlock();

        // Send response back to Node.js server
        send(client_socket, response.c_str(), response.size(), 0);
    }
    close(client_socket);
}

int main()
{
    // Create a TCP socket
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd == 0)
    {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    // Bind the socket to port 9090
    sockaddr_in address;
    int addrlen = sizeof(address);
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(9090);

    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0)
    {
        perror("Bind failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    // Listen for incoming connections
    if (listen(server_fd, 3) < 0)
    {
        perror("Listen failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    cout << "C++ server listening on port 9090" << endl;

    while (true)
    {
        int client_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t *)&addrlen);
        if (client_socket < 0)
        {
            perror("Accept failed");
            close(server_fd);
            exit(EXIT_FAILURE);
        }

        // Create a new thread to handle the client connection
        thread(handleClient, client_socket).detach();
    }

    close(server_fd);
    return 0;
}

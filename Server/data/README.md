# Prerequisites to run the script:

1. Ensure that MongoDB is installed and running on your localhost. You can start MongoDB by executing the following command in a terminal or command prompt:

    ```
    mongod
    ```

2. Make sure you have Python installed on your system.

3. Install the required Python packages by running the following command:

    ```
    pip install pymongo
    ```

4. Before running the script, make sure to update the `mongodbURI` variable in the script with the appropriate MongoDB connection string. This string should match the MongoDB instance running on your localhost.

5. Once you have updated the `mongodbURI`, you can execute the script by running the following command:

    ```
    python exportData.py
    ```





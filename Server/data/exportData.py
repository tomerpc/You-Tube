import json
from pymongo import MongoClient
from bson import json_util

def connect_to_mongodb(uri):
    client = MongoClient(uri)
    return client

def create_database(client, db_name):
    db = client[db_name]
    return db

def read_json_file(file_path):
    with open(file_path, 'r') as file:
        data = json.load(file)
    return data


def insert_data_to_mongodb(collection, data):
    collection.insert_many(data)

def export_json_to_mongodb(uri, db_name, collections_files):
    client = connect_to_mongodb(uri)
    db = create_database(client, db_name)
    
    for collection_name, file_path in collections_files.items():
        data = read_json_file(file_path)
        collection = db[collection_name]
        data_processed = json_util.loads(json_util.dumps(data))
        insert_data_to_mongodb(collection, data_processed)
        print(f"Data from {file_path} has been successfully exported to MongoDB collection '{collection_name}' in database '{db_name}'.")

if __name__ == "__main__":
    MONGODB_URI = "mongodb://localhost:27017"  # Using localhost URI
    NEW_DB_NAME = "mydatabase"  # Replace with the name of the new database
    
    # Define your file paths and collection names
    COLLECTIONS_FILES = {
        "users": "mydatabase.users.json",
        "videos": "mydatabase.videos.json"
    }
    
    export_json_to_mongodb(MONGODB_URI, NEW_DB_NAME, COLLECTIONS_FILES)

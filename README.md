# Social Media Application
A mobile social media platform similar to Instagram, built with Android Studio (Java) frontend and Spring Boot backend. The application uses PostgreSQL and MongoDB for data storage.
Features

User Authentication: Secure login and registration system
Profile Management: Create and edit user profiles with profile pictures
Post Creation: Share images with captions
Feed: View posts from followed users
Interactions: Like and comment on posts
Follow System: Follow/unfollow other users
Search: Find users and posts by keywords
Chatbot Assistant: AI-powered chatbot to help users navigate the platform and answer questions

Tech Stack
# Frontend

Android Studio
Java
XML layouts
Retrofit for API calls
Glide for image loading

# Backend

Spring Boot
Spring Security
Spring Data JPA
REST API architecture

# Database

PostgreSQL: User data, relationships, and authentication
MongoDB: Media content and posts

# Architecture
The application follows a client-server architecture:

Mobile Client: Android application handling UI and user interactions
RESTful API: Spring Boot server providing endpoints for all functionalities
Database Layer: Hybrid database approach with relational and NoSQL databases

# Installation and Setup
Prerequisites

JDK 11 or higher
Android Studio 4.0+
PostgreSQL 12+
MongoDB 4.4+
Maven 3.6+

# Backend Setup

Clone the repository
bashgit clone https://github.com/William2207/MyAndroidProject.git 
cd social-media-app/backend

Configure database connections in application.properties
properties# PostgreSQL configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/socialmediadb
spring.datasource.username=postgres
spring.datasource.password=your_password

# Frontend Setup

Open the project in Android Studio
bashcd ../frontend

Update the API base URL in ApiClient.java to match your backend server address
javapublic static final String BASE_URL = "http://your-backend-url:8080/api/";

Build and run the application on an emulator or physical device

# Database Schema
PostgreSQL Tables

users - User accounts and profile information
follow - User following relationships
likes - Post likes
comments - Post comments
save - Post save

MongoDB Collections

posts - User posts including media, captions, and metadata

# Future Enhancements

Direct messaging functionality
Stories feature
Explore page
Post tagging and location
Real time notification

# Contributing

Fork the repository
Create your feature branch (git checkout -b feature/amazing-feature)
Commit your changes (git commit -m 'Add some amazing feature')
Push to the branch (git push origin feature/amazing-feature)
Open a Pull Request

# License
This project is licensed under the MIT License - see the LICENSE.md file for details
Acknowledgments

Thanks to all contributors
Inspired by Instagram's user experience
Built as a learning project

Contact
Nguyen Hong Phuc - @william - william1111227@gmail.com
Project Link: https://github.com/William2207/MyAndroidProject

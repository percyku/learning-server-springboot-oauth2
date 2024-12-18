# Learning Service SpringBoot with OAuth2

This program is for RESTful API practice

# Programming Languages
- Java(1.7)
- SQL

# Technologies

- Spring boot background (3.1.5)
- Spring Security
- OAuth2 
- JPA/Hibernate
- MySQL(8.0.22) -> Using [create-schema.sql](https://github.com/percyku/learning-server-springboot-oauth2/blob/master/create-schema.sql) / [create-tables.sql](https://github.com/percyku/learning-server-springboot/blob/master/create-tables.sql) /[create-users-data.sql](https://github.com/percyku/learning-server-springboot-oauth2/blob/master/create-users-data.sql) to create schema / tables /users data

  [<img src="images/relative-table.png">](https://github.com/percyku/learning-server-springboot-oauth2/blob/master/images/relative-table.png)

# Function

- Member Registration -> Role:Instrutor,Student
- Member Login/Logout
- Member By OAuth2.0 Login with Google & GitHub
- Member Update Profile
- Instructor Create class
- Student Search class
- Student Enroll class

# RESTful API

You can use postman to test those APIs.
(notes: import this file to postman [restful-api-file](https://github.com/percyku/learning-server-springboot-oauth2/blob/master/learning-restful-api.postman_collection.json))

Please follow this [detail](https://github.com/percyku/learning-server-springboot/blob/master/restful-api-operation.md) to operate



# OAuth2

Please get authorizing from [Google](https://console.cloud.google.com/welcome) and [GitHub](https://github.com/settings/developers),
then add client-id & client-secret in Google/GitHub Social login(folder:/main/resources/application.properties)

This function must use front-end website to operate,but my front-end website is not complete yet.
Below gif will show you how to work with front-end website

- Google Login

[<img src="images/google-login-l.gif">](https://github.com/percyku/learning-server-springboot-oauth2/blob/master/images/google-login-l.gif)

- GitHub Login

[<img src="images/github-login-l.gif">](https://github.com/percyku/learning-server-springboot-oauth2/blob/master/images/github-login-l.gif)



# Ref

[[NEW] Spring Boot 3, Spring 6 & Hibernate for Beginners](https://www.udemy.com/course/spring-hibernate-tutorial/?couponCode=BFCPSALE24)

[資安一把罩！Spring Security 零基礎入門](https://hahow.in/courses/64d4acbf018e4b0acfaac3a3)

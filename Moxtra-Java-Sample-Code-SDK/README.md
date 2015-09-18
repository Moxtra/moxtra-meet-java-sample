Java Sample Code for Moxtra APIs
================================

This repository contains Java code sample to launch a real-time meeting and upload the selected files to it using Moxtra SDKs and APIs.



Please feel free to clone the repository and follow the steps below.

// For the detailed documentation on Moxtra APIs please visit http://developer.moxtra.com.


Setup:

Clone the sample project from github:
git clone https://github.com/Moxtra/xxxxxx


Register your App:
After ([registering your app with Moxtra]), you will be provided with a unique client id and client secret key that is used to initialize the Moxtra SDK.


Authenticating your App:
You'll need the 



This Core API allows you to start a Moxtra meet by uploading one or more files. 
The Core API is implemented in Java and is based on HTTP and SSO authentication and provides low level calls to upload files to a Moxtra meet.

If you want to follow along, first register a new app on the App Console. You'll need the CLIENT_ID and the CLIENT_SECRET to access the Core API. Then install the Java SDK and you'll be ready to go.


Authenticating your app

The Core API uses the Simplified SSO (Single Sign-On) approach, but the Java SDK will take care of most of it so you don't have to start from scratch. You can find out more about Simplified SSO in our Simplified SSO guide.

You'll need to provide your client Id and secret to the new DbxWebAuthNoRedirect object.



## How to run

This sample is a web application "apiutil.war" that is on /target/apiutil.war, which can be deployed on any 
Java Web Server. For example on Tomcat, you can visit the first page on http://localhost:8080/apiutil/index.html to 
see the samples as follows:

 
  + Upload File into Meet


## Samples

Using web pages to drive server operations. Each web page performs one function. The upload file operations are to get 
files from server, not client. In other words, server codes are client to Moxtra REST API Service. 

  + The Servlet is handled by /src/main/java/com/moxtra/webapp/api/APIServlet.java
  + The server operations are handled by /src/main/java/com/moxtra/util/MoxtraAPIUtil.java

## Build by Maven

This project can be built by Maven via "mvn clean install".




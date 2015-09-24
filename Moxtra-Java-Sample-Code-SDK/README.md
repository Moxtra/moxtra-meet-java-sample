Java Sample Code for Moxtra APIs
================================

This repository contains Java sample code to launch a real-time meeting and 
upload the selected files to it using Moxtra SDKs and APIs.



Please feel free to clone the repository and follow the steps below.


## Setup

## Clone the sample project from github
	https://github.com/sanjayiyerkudaliprasannakumar/Java-SampleCode-SDK.git


## Register your App
	You can register your Moxtra App here: https://developer.moxtra.com/nextapps. Once you register, 
	you will be provided with a unique client id and client secret key that is used to initialize 
	the Moxtra SDK.


## Authenticating your App
	You'll need your CLIENT_ID and CLIENT_SECRET to authenticate your app and get your access_token.

	Go to Moxtra-Java-Sample-Code-SDK/src/main/webapp.
	Open index.html and input your CLIENT_ID as shown below:
		
		var client_id = "INPUT YOUR CLIENT_ID"; 
		


	Go to Moxtra-Java-Sample-Code-SDK/src/main/java/com/moxtra/webapp/api
	Open APIServlet.java and locate the getAccessToken() method and input your CLIENT_ID and CLIENT_SECRET as shown below:

		String client_id = "INPUT YOUR CLIENT_ID";
		String client_secret = "INPUT YOUR CLIENT_SECRET";

## Set the path of the folder containing the files to be uploaded:
	You need to now set the path of the temp folder to your path as shown below:
		
		<input class="cbox" type="checkbox" align="middle" name="pic[]" 
		value="/Users/sanjayiyer/Documents/Tomcat/webapps/apiutil/temp/sample.pdf" 
		id="file_path" />
		



## Running your App
	Now you're all set to run your App:

	Configure an application server like Tomcat or Weblogic etc. Please install maven 
	if you haven't done it before.

	The required dependencies have already been added in pom.xml

	Go to the same folder as pom.xml and run the following command: "mvn clean install".

	Go to the following folder Moxtra-Java-Sample-Code-SDK/target and copy the .war file 
	in the webapps folder of tomcat (Tomcat/webapps). 
	
	You can also create a folder of your 
	own inside the webapps folder and copy the .war file into it (Tomcat/webapps/your_folder/.war)

	Once you have it deployed (check the /logs dir for any problems), it should be accessible via: http://host:port/{your_folder}/apiutil/index.html, if your .war is in Tomcat/webapps/your_folder/.war

	For example if you are running your app from localhost then visit the page on http://localhost:8080/{your_folder}/apiutil/index.html, if your .war is in Tomcat/webapps/your_folder/.war
	
	Otherwise, if your app is in Tomcat/webapps/.war, then you can access your app via:
		http://localhost:8080/apiutil/index.html
	
 



## Step by Step tutorial


Here we are using a web page to drive server operations. The web page performs the following operations:

**1. Authenticate the user by generating the access token**

**2. Initialize the user**

**3. Start a Moxtra meet**

**4. Upload selected files to meet**

The upload file operations are to get files from server, not client. In other words, 
server codes are clients to Moxtra REST API Service. 

  + The Servlet is handled by /src/main/java/com/moxtra/webapp/api/APIServlet.java
  + The server operations are handled by /src/main/java/com/moxtra/util/MoxtraAPIUtil.java


## Step 1: Authenticate the user by generating the access token.
		The Core API uses Simple Single Sign On (SSO), but the Java SDK will take care 
		of most of it so you don't have to start from scratch. 

		You'll need to provide your CLIENT_ID inside the getToken function in index.html

				getToken = function()
	            {
	               	var uniqueid = "user001"; // You can replace this 
	               	with any Unique value of your own.
					var client_id = "INPUT YOUR CLIENT_ID HERE";
	                var req_url = "http://localhost:8080/apiutil/api?action=getAccessToken&uniqueid=" + uniqueid;


		You'll need to provide your CLIENT_SECRET inside the method getAcessToken in APIServlet.java

		protected void getAccessToken(HttpServletRequest request, HttpServletResponse response)
				throws MoxtraAPIUtilException, IOException {

			String client_id = request.getParameter("client_id");
			String client_secret = "INPUT_YOUR_CLIENT_SECRET";
			String uniqueid = request.getParameter("uniqueid");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");

	        System.out.println("Client Id " + client_id);
	        System.out.println("Client secret " + client_secret);

			String jsonString = null;

			Sending the Request parameters to the getAccessTokenAPI Java API:
			_________________________________________________________________
			Once the CLIENT_ID is input in the index.html, we will construct 
			the URL to fetch the data from the application server:
				var req_url = "http://localhost:8080/apiutil/api?action=getAccessToken&uniqueid=" + uniqueid;

			Now we will make an AJAX call to send this request to the JAVA API 
			on the App server to autenticate the user:
				jQuery.ajax({
	                    type: "GET",
	                    url: req_url,
	                    dataType: 'jsonp',
	                    cache: false,
	                    jsonpCallback: "getdata",
	                    success: function(response, status, xhr) {
	                        access_token = response.access_token;

	        This would return the access_token on successful authentication. 


## Step 2: Initialize the user
        Using the access_token generated in the previous user, we initialize the user:

        	if (access_token) {
                        
                            var options = {
                                mode: "sandbox", 
                                client_id: client_id,
                                access_token: access_token,
                                invalid_token: function(event) {
                            // Triggered when the access token is expired or invalid
                                alert("Access Token expired for session id: " + event.session_id);
                                }
                            };

                            Moxtra.init(options); // Initialise the moxtra user

                        } 


## Step 3: Start a meet
		The user is authenticated and initialized onload of the webpage.
		Now the user clicks on the "Start Moxtra Meet", the start_meet() function gets invoked.
		In the start_meet() function, the meet_options variable is set with the required 
		parameters to start a moxtra meet.
		Now we make a call to Moxtra Javascript SDK to start a meet with the required parameters:
				Moxtra.meet(meet_options);

		On succesful start of the meet, the Javascript SDK returns the session_id and 
		the session_key of the meet.
		Any other user can be invited to join this meet using the session_key
		function start_meet() {
		                
		                var meet_options = {
		                    iframe: false, //To open the meet in the same window in a different iFrame.
		                    // tab: true, //To open the meet in a new browser tab, N/A if iframe option is set to true.
		                    tagid4iframe: "meet-container", //ID of the HTML tag within which the Meet window will show up. Refer https://developer.grouphour.com/moxo/docs-js-sdk/#meet
		                    iframewidth: "1000px",
		                    iframeheight: "750px",
		                    extension: { 
		                        "show_dialogs": { "meet_invite": true } 
		                    },
		                    start_meet: function(event) {
		                        console.log("Meet Started - session_id: "+event.session_id+"session_key: "+event.session_key);

		                        //Your application server can upload files to meet using the session_id and session_key
		                        var session_id=event.session_id;
		                        var session_key=event.session_key;
            
		                       uploadMeetFile(access_token,session_id,session_key);
		                    },
		                    error: function(event) {
		                        console.log("error code: " + event.error_code + " message: " + event.error_message);
		                    },
		                    end_meet: function(event) {
		                        console.log("Meet Ended");
		                    }
		                };
		                
		               Moxtra.meet(meet_options); //JAVA SDK call for Moxtra meet
		            }



## Step 4: Upload selected files to meet
		Once the moxtra meet is started, the user can access the session_id and session_key.
		Using this data, we now make a call to the the uploadMeetFile(access_token,session_id,session_key) in the index.html

		The selected file(s) data is captured and sent to the app server using the following url:
			var req_url = "http://localhost:8080/apiutil/api?action=uploadFileToMeet&session_id=" + encodeURIComponent(session_id) + "&session_key=" + encodeURIComponent(session_key) + "&file_path=" + encodeURIComponent(file_path);

		This in turn calls the uploadFileToMeet Java API method defined in APIServlet.java
					protected void uploadFileToMeet(HttpServletRequest request, HttpServletResponse response) 
				throws MoxtraAPIUtilException, IOException {
				
				//String access_token = request.getParameter("access_token");
				String session_id = request.getParameter("session_id");
				String session_key = request.getParameter("session_key");
				String file_path = request.getParameter("file_path");

		The JAVA API methods getAcessToken and uploadFileToMeet defined in APIServlet.java are wrapper methods to the actual getAcessToken and uploadFileToMeet methods defined in MoxtraAPIUtil.java



For the detailed documentation on Moxtra APIs please visit [Moxtra Developer Website](http://developer.moxtra.com)







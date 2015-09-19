Java Sample Code for Moxtra APIs
================================

This repository contains Java code sample to launch a real-time meeting and upload the selected files to it using Moxtra SDKs and APIs.



Please feel free to clone the repository and follow the steps below.


Setup:

Clone the sample project from github:
git clone https://github.com/Moxtra/xxxxxx


Register your App:
After ([registering your app with Moxtra]), you will be provided with a unique client id and client secret key that is used to initialize the Moxtra SDK.


Authenticating your App:
You'll need your CLIENT_ID and CLIENT_SECRET to authenticate your app and get your access_token.

Go to Moxtra-Java-Sample-Code-SDK/src/main/webapp.
Open index.htm and input your CLIENT_ID as shown below:

		var client_id = "INPUT YOUR CLIENT_ID"; 



Go to Moxtra-Java-Sample-Code-SDK/src/main/java/com/moxtra/webapp/api
Open APIServlet.java and locate the getAccessToken() method and input your CLIENT_SECRET as shown below:

		String client_secret = "INPUT YOUR CLIENT_SECRET";


Running your App:
Now you're all set to run your App:

Configure an application server like Tomcat or Weblogic etc. Please install maven if you haven't done it before.

The required dependencies have already been added in POM.xml

Go to the same folder as pom.xml and run the following command: "mvn clean install".

Go to the following folder Moxtra-Java-Sample-Code-SDK/target and copy the WAR file and place it in the webapps folder of tomcat 
(Tomcat/webapps)

Now change your directory to Tomcat/bin from a terminal or cmd and start your Tomcat server using the following command:
sh startup.sh

Now open a browser and visit the page on http://localhost:8080/apiutil/index.html. In case you have your own domain,
you can visit the page on http://YOUR_DOMIAN_NAME:8080/apiutil/index.html
 



Step by Step tutorial:


Here we are using a web page to drive server operations. The web page performs the following operations:
1. Authenticate the user by generating the access token.
2. Initialize the user.
3. Start a Moxtra meet.
4. Upload selected files to meet.

The upload file operations are to get files from server, not client. In other words, server codes are clients to Moxtra REST API Service. 

  + The Servlet is handled by /src/main/java/com/moxtra/webapp/api/APIServlet.java
  + The server operations are handled by /src/main/java/com/moxtra/util/MoxtraAPIUtil.java


Step 1: Authenticate the user by generating the access token.
		The Core API uses Simple Single Sign On (SSO), but the Java SDK will take care of most of it so you don't have to start from scratch. 

		You'll need to provide your CLIENT_ID inside the getToken function in index.htm

				getToken = function()
	            {
	               	var uniqueid = Math.floor((Math.random() * 10) + 1); // You can replace this with any Unique value of your own.
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
			Once the CLIENT_ID is input in the index.htm, we will construct the URL to fetch the data from the application server:
				var req_url = "http://localhost:8080/apiutil/api?action=getAccessToken&uniqueid=" + uniqueid;

			Now we will make an AJAX call to send this request to the JAVA API on the App server to autenticate the user:
				jQuery.ajax({
	                    type: "GET",
	                    url: req_url,
	                    dataType: 'jsonp',
	                    cache: false,
	                    jsonpCallback: "getdata",
	                    success: function(response, status, xhr) {
	                        access_token = response.access_token;

	        This would return the access_token on successful authentication. 


Step 2: Initilaize the user:
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


Step 3: Start a meet:
		The user is authenticated and initialized onload of the webpage.
		Now the user clicks on the "Start Moxtra Meet", the start_meet() function gets invoked.
		In the start_meet() function, the meet_options variable is set with the required parameters to start a moxtra meet.
		Now we make a call to Moxtra Javascript SDK to start a meet with the required parameters:
				Moxtra.meet(meet_options);

		On succesful start of the meet, the Javascript SDK returns the session_id and the session_key of the meet.
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
		                
		                Moxtra.meet(meet_options);
		            }



Step 4: Upload selected files to meet:
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



For the detailed documentation on Moxtra APIs please visit http://developer.moxtra.com.







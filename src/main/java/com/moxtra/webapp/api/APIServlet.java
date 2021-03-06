package com.moxtra.webapp.api;

import com.moxtra.util.MoxtraAPIUtil;
import com.moxtra.util.MoxtraAPIUtilException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * This servlet is to handle request to do MoxtraAPIUtil provided features
 */
public class APIServlet extends HttpServlet {
	
	  /**
	   * The doGet or doPost method handles
	   * 1. getAccessToken
	   * 2. uploadFileToMeet
	   * 
	   * based on action
	   * 
	   */
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  
		  doPost(request, response);
 	}	

	/**
	 * The doPost method handles 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
	
    	try {
    		
    		String action = request.getParameter("action");
    		
    		if (action == null) {
    			throw new Exception("no action input");
    		}

			if (action.equals("getAccessToken")) {
				getAccessToken(request, response);
				return;
			}

    		if (action.equals("uploadFileToMeet")) {
    			uploadFileToMeet(request, response);
    			return;
    		}
    		
    		throw new Exception("not correct action input: " + action);
    		
	  } catch (Exception e) {
		  	throw new ServletException (e.getMessage(), e);
	  }		
		
	}


	/**
	 * getAccessToken
	 *
	 * @param request
	 * @param response
	 * @throws MoxtraAPIUtilException
	 */

	protected void getAccessToken(HttpServletRequest request, HttpServletResponse response)
			throws MoxtraAPIUtilException, IOException {

		String client_id = "INPUT_YOUR_CLIENT_ID";
		String client_secret = "INPUT_YOUR_CLIENT_SECRET";
		String uniqueid = request.getParameter("uniqueid");
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");

		String jsonString = null;
		try {
			HashMap<String, Object> map = MoxtraAPIUtil.getAccessToken(client_id, client_secret, uniqueid, firstname, lastname);
			ObjectMapper mapper = new ObjectMapper();
			jsonString = mapper.writeValueAsString(map);

		} catch (Exception e) {
			jsonString = "{ \"code\" : \"RESPONSE_ERROR_FAILED\", \"message\" : \"" + e.getMessage() + "\" }";
		}

		jsonResponse(jsonString, request.getParameter("callback"), response);
	}
	
	/**
	 * uploadFileToMeet
	 * 
	 * @param request
	 * @param response
	 * @throws MoxtraAPIUtilException
	 * @throws IOException
	 */
	protected void uploadFileToMeet(HttpServletRequest request, HttpServletResponse response) 
		throws MoxtraAPIUtilException, IOException {
		
		//String access_token = request.getParameter("access_token");
		String session_id = request.getParameter("session_id");
		String session_key = request.getParameter("session_key");
		String file_path = request.getParameter("file_path");

		String jsonString = null;
		try {
			File uploadFile = new File(file_path);
		
			jsonString = MoxtraAPIUtil.uploadFileToMeet(session_id, session_key, uploadFile);
			
        } catch (Exception e) {
        	jsonString = "{ \"code\" : \"RESPONSE_ERROR_FAILED\", \"message\" : \"" + e.getMessage() + "\" }";
        }		        
		
		jsonResponse(jsonString, request.getParameter("callback"), response);	
	}
	
	/**
	 * return JSON string
	 * 
	 * @param jsonString
	 * @param callback
	 * @param response
	 * @throws IOException
	 */
	
	void jsonResponse(String jsonString, String callback, HttpServletResponse response)
		throws IOException {
	        
		StringBuilder out = new StringBuilder();
		if (callback != null) {
			out.append(callback).append("( ");
		}
		
		try {
			out.append(new String(jsonString.getBytes("ISO-8859-1"), "UTF-8"));
        } catch (Exception e) {
        	out.append(jsonString);
        }
		
		if (callback != null) {
			out.append(" )");
		}				
		
		response.setContentType("application/json; charset=UTF-8");
		
		PrintWriter writer = response.getWriter();
		writer.println(out.toString());
		writer.flush();
	        
	}	
	
}

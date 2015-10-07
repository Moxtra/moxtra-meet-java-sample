package com.moxtra.util;


import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moxtra.util.MoxtraAPIUtil;
import com.moxtra.util.MoxtraAPIUtilException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;


/**
 * @author Ryan Heaton
 * @author Dave Syer
 */
public class TestAPIRequest extends TestCase {

	private static Log logger = LogFactory.getLog(TestAPIRequest.class);			
	
	
    public TestAPIRequest(String testMethodName) {
        super(testMethodName);
    }	
	
	
    public static void main(String args[]) {
    	
    	junit.textui.TestRunner.run(suite());
    }    
    
    /**
     * suite()
     * @return a
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
    
        suite.addTest(new TestAPIRequest("testUtilGetAccessToken"));
       	suite.addTest(new TestAPIRequest("testUtilUploadFileToMeet"));
      
        
        return suite;
    }	
    
    public void testURI() {
    	String url = "https://api.moxtra.com/me/binders?access_token=GiozMgAAAUhhsvDEApMuAFU3Rk9CT0N2bnpaSlBNMlVCem9oSFBEIAAAAANUQk5GalNDdm56WkpQTTJVQnpvaEhQRHFkYk9wcWY2elNJ";
    	
    	try {
    	
    		URI uri = new URI(url);
    		
    		org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
    		
    		System.out.println("done");
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    
    public void testUtilGetAccessToken() {
		String client_id = "INPUT_YOUR_CLIENT_ID";
		String client_secret = "INPUT_YOUR_CLIENT_ID";
		String unique_id = "INPUT_YOUR_UNIQUE_ID";
		String firstname = "INPUT_YOUR_FIRST_NAME";
		String lastname = "INPUT_YOUR_LAST_NAME";

		try {
			HashMap<String, Object> map = MoxtraAPIUtil.getAccessToken(client_id, client_secret, unique_id, firstname, lastname);
			
			String access_token = (String) map.get(MoxtraAPIUtil.PARAM_ACCESS_TOKEN);
			int expires_in = (Integer) map.get(MoxtraAPIUtil.PARAM_EXPIRES_IN);
		
			System.out.println("access_token: " + access_token + " expires_in: " + expires_in);
			
		} catch (MoxtraAPIUtilException e) {
			e.printStackTrace();
		}
			
    }
    
    
    public void testUtilUploadFileToMeet() {
    	String access_token = "INPUT_YOUR_ACCESS_TOKEN";
    	String session_id = "INPUT_YOUR_SESSION_ID";
    	String session_key = "INPUT_YOUR_SESSION_KEY";
    	String file = "INPUT_YOUR_FILE_PATH";
    	
    	try {
    		
    		File uploadFile = new File(file);
    		
    		String result = MoxtraAPIUtil.uploadFileToMeet(session_id, session_key, uploadFile);
    		
    		System.out.println("response: " + result);
    		
	} catch (MoxtraAPIUtilException e) {
			e.printStackTrace();
	  }
    	
    }
    
}

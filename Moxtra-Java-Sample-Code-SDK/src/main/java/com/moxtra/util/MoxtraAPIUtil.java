package com.moxtra.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.xml.security.utils.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import com.moxtra.util.MultipartUtility;


public class MoxtraAPIUtil {
	public static String UNIQUEID_GRANT_TYPE = "http://www.moxtra.com/auth_uniqueid"; 
	public static String API_HOST_URL = "https://apisandbox.moxtra.com/";
	public static String WEB_HOST_URL = "https://sandbox.moxtra.com/";
	public static String PARAM_ACCESS_TOKEN = "access_token";
	public static String PARAM_EXPIRES_IN = "expires_in";
	private static final int SOCKET_TIMEOUT = 30000;
	private static final int MAX_TOTAL_CONNECTION = 100;
	private static final int MAX_CONNECTION_PER_ROUTE = 20;
	private static final int MAX_CONNECTION_MOXTRA = 50;
	private static PoolingHttpClientConnectionManager ccm = null;
	
	
	/**
	 * getHttpClient
	 * 
	 * @return
	 */
	
	private static CloseableHttpClient getHttpClient() {

		  try {

			  if (ccm == null) {
				  PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager();
				  
				  // Increase max total connection to 100
				  ccm.setMaxTotal(MAX_TOTAL_CONNECTION);
				  // Increase default max connection per route to 20
				  ccm.setDefaultMaxPerRoute(MAX_CONNECTION_PER_ROUTE);
				  // Increase max connections for api.moxtra.com to 50
				  HttpHost moxtrahost = new HttpHost(API_HOST_URL, 443);
				  ccm.setMaxPerRoute(new HttpRoute(moxtrahost), MAX_CONNECTION_MOXTRA);
				  
				  ccm.setSocketConfig(moxtrahost, SocketConfig.custom().setSoTimeout(SOCKET_TIMEOUT).build());
			  }
		   
			  return HttpClients.custom().setConnectionManager(ccm).build();
		   
		  } catch (Exception e) {
			  return HttpClients.createDefault();
		  }
	 }

	/**
	 * To get the Access Token via /oauth/token unique_id. The return in the following JSON format
	 *
	 *   {
	 *   	"access_token": ACCESS_TOKEN,
	 *   	"expires_in": EXPIRES_IN,
	 *   	...
	 *   }
	 *
	 * @param client_id
	 * @param client_secret
	 * @param unique_id
	 * @param firstname (optional)
	 * @param lastname (optional)
	 * @return HashMap
	 * @throws MoxtraAPIUtilException
	 */

	@SuppressWarnings("unchecked")
	public static HashMap<String, Object> getAccessToken(String client_id, String client_secret, String unique_id,
														 String firstname, String lastname) throws MoxtraAPIUtilException {

		if (client_id == null || client_secret == null || unique_id == null) {
			throw new MoxtraAPIUtilException("client_id, client_secret, and unique_id are required!");
		}

		String timestamp = Long.toString(System.currentTimeMillis());
		HashMap<String, Object> myMap = new HashMap<String, Object>();

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;

		try {

			// generate code
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

			SecretKeySpec secret_key = new SecretKeySpec(client_secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			StringBuffer total = new StringBuffer();
			total.append(client_id);
			total.append(unique_id);
			total.append(timestamp);

			String signature = encodeUrlSafe(sha256_HMAC.doFinal(total.toString().getBytes()));

			httpClient = getHttpClient();
			HttpPost httpPost = new HttpPost(API_HOST_URL + "oauth/token");
			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", client_id));
			params.add(new BasicNameValuePair("client_secret", client_secret));
			params.add(new BasicNameValuePair("grant_type", UNIQUEID_GRANT_TYPE));
			params.add(new BasicNameValuePair("uniqueid", unique_id));
			params.add(new BasicNameValuePair("timestamp", timestamp));
			params.add(new BasicNameValuePair("signature", signature));

			// optional
			if (firstname != null) {
				params.add(new BasicNameValuePair("firstname", firstname));
			}

			if (lastname != null) {
				params.add(new BasicNameValuePair("lastname", lastname));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("unable to get access_token");
			}
			if (responseEntity != null) {
				// EntityUtils to get the response content
				String content =  EntityUtils.toString(responseEntity);

				// get access token
				ObjectMapper objectMapper = new ObjectMapper();
				myMap = objectMapper.readValue(content, HashMap.class);

			} else {
				throw new Exception("unable to make request");
			}

			return myMap;

		} catch (Exception e) {
			throw new MoxtraAPIUtilException(e.getMessage(), e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException ex) {
					throw new MoxtraAPIUtilException(ex.getMessage(), ex);
				}
			}

			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException ex) {
					throw new MoxtraAPIUtilException(ex.getMessage(), ex);
				}
			}
		}

	}

	/**
	 * upload File into Meet based on session_id and session_key for host
	 * 
	 * @param session_id
	 * @param session_key
	 * @param uploadFile
	 * @param access_token
	 * @return response in JSON
	 * @throws MoxtraAPIUtilException
	 */
	
	//public static String uploadFileToMeet(String session_id, String session_key, File uploadFile, String access_token)
    public static String uploadFileToMeet(String session_id, String session_key, File uploadFile)
    throws MoxtraAPIUtilException {
		
		if (session_id == null || session_key == null || uploadFile == null) {
			throw new MoxtraAPIUtilException("session_id, session_key, uploadFile, and access_token are required!"); 
		}
		
		String json_result = null;
		InputStream inputStream = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;

		try {

			String filename = URLEncoder.encode(uploadFile.getName(), "UTF-8");		
			String requestURL = WEB_HOST_URL + "board/upload?type=original&sessionid=" + session_id + "&key=" + session_key + "&name=" + filename;
			
			inputStream = new FileInputStream(uploadFile);
			
			long length = uploadFile.length();
			
			httpClient = getHttpClient();
			HttpPost httppost = new HttpPost(requestURL);
			InputStreamEntity entity = new InputStreamEntity(inputStream, length, ContentType.APPLICATION_OCTET_STREAM);
			httppost.setEntity(entity);
			
			response = httpClient.execute(httppost);
			HttpEntity responseEntity = response.getEntity(); 
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Upload file failed");
			}
			if (responseEntity != null) {
				json_result = EntityUtils.toString(responseEntity);
			}
			
			return json_result;
		
  		} catch (Exception e) {
  			throw new MoxtraAPIUtilException(e.getMessage(), e);
  			
		} finally {
			
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ex) {
					throw new MoxtraAPIUtilException(ex.getMessage(), ex);
				}
			}
			
  			if (response != null) {
  				try {
  					response.close();
				} catch (IOException ex) {
					throw new MoxtraAPIUtilException(ex.getMessage(), ex);
				}
  			}
  			
  			if (httpClient != null) {
  				try {
  					httpClient.close();
				} catch (IOException ex) {
					throw new MoxtraAPIUtilException(ex.getMessage(), ex);
				}
  			}  						
		}
		
	}
	
	/**
	 * URLSafe Base64 encoding with space padding 
	 * 
	 * @param data
	 * @return
	 */
	public static String encodeUrlSafe(byte[] data) {
	    String strcode = Base64.encode(data);
	    byte[] encode = strcode.getBytes(); 
	    for (int i = 0; i < encode.length; i++) {
	        if (encode[i] == '+') {
	            encode[i] = '-';
	        } else if (encode[i] == '/') {
	            encode[i] = '_';
	        } else if (encode[i] == '=') {
	        	encode[i] = ' ';
	        }
	    }
	    return new String(encode).trim();
	}	

}

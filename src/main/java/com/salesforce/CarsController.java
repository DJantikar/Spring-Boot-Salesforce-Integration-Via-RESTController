package com.salesforce;


import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;





@RestController
public class CarsController {
    static final String USERNAME     = "<Your Salesforce Username>";
    static final String PASSWORD     = "<Your Salesforce Password>";
    static final String LOGINURL     = "<Your Salesforce Domain >";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "<Your Connected App OAuth Client ID>";
    static final String CLIENTSECRET = "<Your Connected App OAuth Client SECRET>";
    private static String accessToken;
    
    @RequestMapping("/authenticate")
	public String oAuthSessionProvider()
			throws HttpException, IOException, JSONException {

		 // Assemble the login request URL
        String baseUrl = LOGINURL +
                          GRANTSERVICE +
                          "&client_id=" + CLIENTID +
                          "&client_secret=" + CLIENTSECRET +
                          "&username=" + USERNAME +
                          "&password=" + PASSWORD;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(baseUrl);
        BasicResponseHandler handler = new BasicResponseHandler();
        HttpResponse response = client.execute(post);
        JSONObject json =new JSONObject(handler.handleResponse(response));
        accessToken=(String) json.get("access_token");
        System.out.println(accessToken);
        return accessToken;
	}
    @SuppressWarnings("deprecation")
	@RequestMapping("/write")
    public String exampleWrite() throws HttpException, IOException{
		//do a query
    	org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
		PostMethod post = new PostMethod(LOGINURL + "/services/data/v20.0/sobjects/Car__c/");
		post.setRequestHeader("Authorization", "OAuth " + accessToken);
		post.setRequestHeader("Content-Type", "application/json");
		post.setRequestBody(new JSONObject().put("Name", "TestMySpringBot").toString());
		httpclient.executeMethod(post);
		String jsonResponse = post.getResponseBodyAsString();
		System.out.println("HTTP " + String.valueOf(httpclient) + ": " + jsonResponse);
		return jsonResponse;
	}
    @RequestMapping("/read")
    public String exampleQuery() throws HttpException, IOException{
		//do a query
    	org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
		GetMethod get = new GetMethod(LOGINURL + "/services/data/v20.0/query");
		get.setRequestHeader("Authorization", "OAuth " + accessToken);
	
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];

		params[0] = new NameValuePair("q",
				"SELECT Name, Mileage__c from Car__c");
		get.setQueryString(params);

		httpclient.executeMethod(get);
		String jsonResponse = get.getResponseBodyAsString();
		System.out.println("HTTP " + String.valueOf(httpclient) + ": " + jsonResponse);
		return jsonResponse;
		//curl https://yourInstance.salesforce.com/services/data/v20.0/sobjects/Account/ -H "Authorization: Bearer token -H "Content-Type: application/json" -d "@newaccount.json"

	}
}

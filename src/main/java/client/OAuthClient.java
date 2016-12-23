/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package client;

import config.OtherOptionNumberRegistry;
import config.RDClientConfig;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.*;
import org.json.JSONException;
import org.json.JSONObject;

class OAuthClient extends CoapClient{

	private static final Log log = LogFactory.getLog(OAuthClient.class);

	private RDClientConfig clientConfig;

	OAuthClient(String rdEndpoint,RDClientConfig clientConfig)
	{
		super(rdEndpoint+TokenUtils.TOKEN_ENDPOINT);
		this.clientConfig=clientConfig;
	}

	RDClientConfig init()
	{
		initialOAuthToken();//get refresh token
		return this.clientConfig;
	}

	private RDClientConfig initialOAuthToken()
	{
		//payload
		String payload=TokenUtils.GRANT_TYPE + "=" + TokenUtils.PASSWORD + "&" + TokenUtils.USER_NAME + "=" + clientConfig
				.getOwner() + "&" + TokenUtils.PASSWORD + "=" + clientConfig.getPassword();

		sendTokenRequest(payload);
		return this.clientConfig;
	}


	RDClientConfig refreshAouthToken()
	{
		//payload
		String payload = TokenUtils.GRANT_TYPE + "=" + TokenUtils.REFRESH_TOKEN + "&" + TokenUtils.REFRESH_TOKEN + "="
				+ this.clientConfig.getRefreshToken();
		sendTokenRequest(payload);
		return this.clientConfig;
	}

	private void sendTokenRequest(String payload)
	{
		Request request= new Request(CoAP.Code.POST, CoAP.Type.CON);

		//endpoint
		String proxyUri=this.clientConfig.getApimGatewayEndpoint()+TokenUtils.TOKEN_ENDPOINT;

		//options
		OptionSet options=new OptionSet();
		options.addOption(new Option(OtherOptionNumberRegistry.CONTENT_TYPE, "application/x-www-form-urlencoded"));
		options.addOption(new Option(OtherOptionNumberRegistry.AUTHORIZATION, "Basic " + clientConfig.getApplicationKey()));
		options.setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		options.setProxyUri(proxyUri);

		//set request
		request.setPayload(payload);
		request.setOptions(options);

		log.info(this.getURI()+"\n"+Utils.prettyPrint(request));

		//send response
		CoapResponse response=this.advanced(request);
		if(response.isSuccess())
		{
			log.info(response.getCode().name()+"\n"+Utils.prettyPrint(response));
			if(response.getCode()== CoAP.ResponseCode.CONTENT)
			{
				if(!response.getResponseText().isEmpty())
					updateOAuthToken(response.getResponseText());
			}

		}
		else
		{
			log.info(response.getCode().name()+"\n"+Utils.prettyPrint(response));
		}

	}

	boolean isRegistered()
	{
		CoapResponse response= this.post("",MediaTypeRegistry.TEXT_PLAIN);
		return (response!=null);
	}

	//set config class and file with new tokens
	private void updateOAuthToken(String payload)
	{
		try {
			JSONObject jsonTokenObject = new JSONObject(payload);
			String newAccessToken = (String) jsonTokenObject.get(TokenUtils.ACCESS_TOKEN);
			String newRefreshToken = (String) jsonTokenObject.get(TokenUtils.REFRESH_TOKEN);
			if (newAccessToken == null || newRefreshToken == null) {
				log.error("Neither Access-Token nor Refresh-Token was found in the response [" + payload + "].");
			} else {
				this.clientConfig.setAuthToken(newAccessToken);
				this.clientConfig.setRefreshToken(newRefreshToken);

				String deviceConfigFilePath = this.clientConfig.getRootPath() + this.clientConfig.getConfigFileName();
				PropertiesConfiguration propertyFileConfiguration = new PropertiesConfiguration(deviceConfigFilePath);
				propertyFileConfiguration.setProperty("auth-token", newAccessToken);
				propertyFileConfiguration.setProperty("refresh-token", newRefreshToken);
				propertyFileConfiguration.save();
			}

		} catch (JSONException e) {
			log.info(e.getMessage() + ": payload- " + payload);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}



	/*getters*/

	public RDClientConfig getClientConfig() {
		return clientConfig;
	}

	/**
	 * Token util constants
	 */
	private static class TokenUtils {

		static final String TOKEN_ENDPOINT = "/oauth2/token";
		static final String GRANT_TYPE = "grant_type";
		static final String ACCESS_TOKEN = "access_token";
		static final String REFRESH_TOKEN = "refresh_token";
		static final String PASSWORD = "password";
		static final String USER_NAME = "username";

	}
}

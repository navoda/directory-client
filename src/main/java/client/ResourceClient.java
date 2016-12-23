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

import config.ExampleDeviceInfo;
import config.OtherOptionNumberRegistry;
import config.RDClientConfig;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * The end user client for the RD (Resource Directory)
 */
public class ResourceClient extends CoapClient {

	private static final Log log = LogFactory.getLog(ResourceClient.class);
	private OAuthClient oAuthClient;
	private VirtualFirealarmClient virtualFirealarmClient;

	private RDClientConfig clientConfig;
	private String endpoint;

	public ResourceClient() {
		super(CoAP.COAP_URI_SCHEME, ResourceClientConstants.LOCALHOST, CoAP.getDefaultPort(CoAP.COAP_URI_SCHEME),
				ResourceClientConstants.RESOURCE_DIRECTORY);
		this.endpoint = this.getURI();
		init();
	}

	private void init() {

		clientConfig=new RDClientConfig(ResourceClientConstants.CONFIG_FILENAME);
		//client config file
		readClientConfig();
		//oauth client
		this.oAuthClient = new OAuthClient(this.endpoint, this.clientConfig);
		//if(!oAuthClient.isRegistered())
			registerOauthTokenEndpoint();
		this.clientConfig = oAuthClient.init();

		//firealarm client
		virtualFirealarmClient= new VirtualFirealarmClient(this.endpoint);
	}

	public void exampleRequest(int deviceType,int requestEndpoint)
	{
		Request request=null;
		switch (deviceType)
		{
		case 1://virtual_firealarm
			switch (requestEndpoint)
			{
			case 1://buzz_on
				request=virtualFirealarmClient.virtualFirealarmBuzz(ExampleDeviceInfo.VIRTUAL_FIREALARM_DEVICE_ID,"on");
				break;
			case 2://buzz_off
				request=virtualFirealarmClient.virtualFirealarmBuzz(ExampleDeviceInfo.VIRTUAL_FIREALARM_DEVICE_ID,"off");
				break;
			default:log.info("Invalid Request Endpoint");
			}
			if(request!=null)
				sendRequest(virtualFirealarmClient,request,0);
			break;
		default:
			log.info("Invalid Device Type");
		}
	}

	/**
	 *
	 * @param client - device client
	 * @param request - coap API request
	 * @param turn - number of times the request has send.
	 */
	private void sendRequest(CoapClient client, Request request, int turn) {
		//set authorization option
		OptionSet options = request.getOptions();
		options.addOption(
				new Option(OtherOptionNumberRegistry.AUTHORIZATION, "Bearer " + this.clientConfig.getAuthToken()));
		request.setOptions(options);
		Utils.prettyPrint(request);
		CoapResponse response = client.advanced(request);
		if (response != null) {
			CoAP.ResponseCode code = response.getCode();
			log.info(code.toString());
			//success
			if (CoAP.ResponseCode.isSuccess(code)) {
				log.info(Utils.prettyPrint(response));
			}
			//failures
			else {
				log.error(Utils.prettyPrint(response));
				//authorization failures
				if (code.equals(CoAP.ResponseCode.UNAUTHORIZED) || code.equals(CoAP.ResponseCode.BAD_REQUEST)) {
					this.oAuthClient.refreshAouthToken();
					log.error(Utils.prettyPrint(response));
					//sending up to 3 times
					if (turn < 3) {
						log.info("Sending again with refresh token - turn" + (turn + 1));
						sendRequest(client, request, turn + 1);
					}
				}
			}
		} else
			log.error("Request Timeout");
	}

	/**
	 * register the oauth token endpoint in Coap Resource directory
	 */
	private void registerOauthTokenEndpoint() {
		String endpoint = ResourceClientConstants.OAUTH_ENDPOINT;
		String payload = "<" + ResourceClientConstants.TOKEN_PATH.substring(endpoint.length() + 1)
				+ ">;rt=\"service\";if=\"POST\"";

		Request request = new Request(CoAP.Code.POST, CoAP.Type.CON); //coap request to send the auth api to server

		request.setURI(this.getURI() + "?ep=" + endpoint + "&d=" + this.clientConfig.getTenantDomain() + "&con="
				+ this.clientConfig.getApimGatewayEndpoint()); //endpoint name, domain and context

		request.setPayload(payload);
		log.info(Utils.prettyPrint(request));

		this.advanced(new CoapHandler() {

			@Override public void onLoad(CoapResponse coapResponse) {
				if (log.isDebugEnabled()) {
					log.info(Utils.prettyPrint(coapResponse));
				}
			}

			@Override public void onError() {
				if (log.isDebugEnabled()) {
					log.error("Error adding API to the resource directory");
				}
			}
		}, request);

	}

	/**
	 * read from .properties file into ClientConfig
	 */
	private void readClientConfig() {

		Properties properties = new Properties();
		try {
			URL path = ResourceClient.class.getClassLoader().getResource(ResourceClientConstants.CONFIG_FILENAME);
			String rootPath;
			if (path != null) {
				rootPath = path.getPath().replace(ResourceClientConstants.CONFIG_FILENAME, "").replace("jar:", "")
						.replace("file:", "");
			} else
				throw new NullPointerException();

			rootPath = URLDecoder.decode(rootPath, CharEncoding.UTF_8);
			clientConfig.setRootPath(rootPath);

			String deviceConfigFilePath = rootPath + ResourceClientConstants.CONFIG_FILENAME;
			InputStream inputStream = new FileInputStream(deviceConfigFilePath);
			properties.load(inputStream);

			clientConfig.setTenantDomain(properties.getProperty("tenantDomain"));
			clientConfig.setOwner(properties.getProperty("owner"));
			clientConfig.setPassword(properties.getProperty("password"));
			clientConfig.setApplicationKey(properties.getProperty("application-key"));
			clientConfig.setApimGatewayEndpoint(properties.getProperty("apim-ep"));
			clientConfig.setHTTP_ServerEndpoint(properties.getProperty("http-ep"));
			clientConfig.setHTTPS_ServerEndpoint(properties.getProperty("https-ep"));
			clientConfig.setCOAP_DirectoryEndpoint(properties.getProperty("coap-ep"));
			clientConfig.setCOAPS_DirectoryEndpoint(properties.getProperty("coaps-ep"));
			clientConfig.setAuthMethod(properties.getProperty("auth-method"));
			clientConfig.setAuthToken(properties.getProperty("auth-token"));
			clientConfig.setRefreshToken(properties.getProperty("refresh-token"));

			inputStream.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	private static class ResourceClientConstants {

		private static final String OAUTH_ENDPOINT = "oauth2";
		private static final String TOKEN_PATH = "/oauth2/token";
		private static final String CONFIG_FILENAME = "clientConfig.properties";
		private static final String LOCALHOST = "localhost"; //host
		private static final String RESOURCE_DIRECTORY = "rd"; //root path

	}
}

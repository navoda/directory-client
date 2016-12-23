package config;/*
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

/**
 * This class stores the Directory Client config
 */
public class RDClientConfig {

	private String tenantDomain;
	private String owner;
	private String password;
	private String HTTPS_ServerEndpoint;
	private String HTTP_ServerEndpoint;
	private String apimGatewayEndpoint;
	private String COAP_DirectoryEndpoint;
	private String COAPS_DirectoryEndpoint;
	private String applicationKey;
	private String authMethod;
	private String authToken;
	private String refreshToken;
	private String rootPath;
	private String configFileName;

	public RDClientConfig(String configFileName) {
		this.configFileName = configFileName;
	}

	/*setters*/

	public void setTenantDomain(String tenantDomain) {
		this.tenantDomain = tenantDomain;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setHTTPS_ServerEndpoint(String HTTPS_ServerEndpoint) {
		this.HTTPS_ServerEndpoint = HTTPS_ServerEndpoint;
	}

	public void setHTTP_ServerEndpoint(String HTTP_ServerEndpoint) {
		this.HTTP_ServerEndpoint = HTTP_ServerEndpoint;
	}

	public void setApimGatewayEndpoint(String apimGatewayEndpoint) {
		this.apimGatewayEndpoint = apimGatewayEndpoint;
	}

	public void setCOAP_DirectoryEndpoint(String COAP_DirectoryEndpoint) {
		this.COAP_DirectoryEndpoint = COAP_DirectoryEndpoint;
	}

	public void setCOAPS_DirectoryEndpoint(String COAPS_DirectoryEndpoint) {
		this.COAPS_DirectoryEndpoint = COAPS_DirectoryEndpoint;
	}

	public void setApplicationKey(String applicationKey) {
		this.applicationKey = applicationKey;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	/*getters*/

	public String getTenantDomain() {
		return tenantDomain;
	}

	public String getOwner() {
		return owner;
	}

	public String getPassword() {
		return password;
	}

	public String getHTTPS_ServerEndpoint() {
		return HTTPS_ServerEndpoint;
	}

	public String getHTTP_ServerEndpoint() {
		return HTTP_ServerEndpoint;
	}

	public String getApimGatewayEndpoint() {
		return apimGatewayEndpoint;
	}

	public String getCOAP_DirectoryEndpoint() {
		return COAP_DirectoryEndpoint;
	}

	public String getCOAPS_DirectoryEndpoint() {
		return COAPS_DirectoryEndpoint;
	}

	public String getApplicationKey() {
		return applicationKey;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getRootPath() {
		return rootPath;
	}

	public String getConfigFileName() {
		return configFileName;
	}
}

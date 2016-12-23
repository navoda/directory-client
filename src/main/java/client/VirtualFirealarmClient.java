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
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.*;

class VirtualFirealarmClient extends CoapClient{

	private static final String VIRTUAL_FIREALARM = "/virtual_firealarm";
	private static final String DEVICE_STATS = "/device/stats/{deviceId}";
	private static final String DEVICE_DOWNLOAD = "/device/download";
	private static final String BUZZ="/device/{deviceId}/buzz";

	private String rdEndpoint;

	VirtualFirealarmClient(String rdEndpoint) {
		super(rdEndpoint+VIRTUAL_FIREALARM);
		this.rdEndpoint = rdEndpoint+VIRTUAL_FIREALARM;
	}

	public String getRDEndpoint() {
		return rdEndpoint;
	}

	Request buzz(String deviceId, String state) {

		Request request = new Request(CoAP.Code.POST, CoAP.Type.CON);

		//endpoint
		this.setURI(this.rdEndpoint+BUZZ.replace("{deviceId}",deviceId));

		//payload
		request.setPayload("state=" + state);
		//options
		OptionSet options = new OptionSet();
		options.addOption(new Option(OtherOptionNumberRegistry.CONTENT_TYPE,"application/x-www-form-urlencoded"));//content format for http
		options.setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		request.setOptions(options);

		return request;
	}

	Request stats(String deviceId,long to, long from)
	{
		Request request= new Request(CoAP.Code.GET, CoAP.Type.CON);

		String queryParam="?from="+from+"&to="+to;

		//endpoint
		this.setURI(this.rdEndpoint+DEVICE_STATS.replace("{deviceId}",deviceId)+queryParam);

		//options
		OptionSet options = new OptionSet();
		options.addOption(new Option(OtherOptionNumberRegistry.CONTENT_TYPE,"application/json"));//content format for http
		request.setOptions(options);

		return request;
	}


}

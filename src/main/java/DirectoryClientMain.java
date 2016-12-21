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

import client.ResourceClient;

import java.util.Scanner;

public class DirectoryClientMain {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		boolean run = true;
		int deviceType;
		int deviceEndpoint;
		ResourceClient client = new ResourceClient();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (run) {
			System.out.println("----------------------------");
			System.out.println("Select Device Type----------");
			System.out.println("0. Exit");
			System.out.println("1. Virtual-Firealarm");

			deviceType = scanner.nextInt();
			if (deviceType != 0) {
				System.out.println("----------------------------");
				System.out.println("Select Device Request-------");
				switch (deviceType) {
				case 1:
					System.out.println("----------------------------");
					System.out.println("virtual-firealarm endpoints");
					System.out.println("0. Return");
					System.out.println("1. Buzzer on");
					System.out.println("2. Buzzer off");
					break;
				default:
				}
				deviceEndpoint = scanner.nextInt();
				if (deviceEndpoint != 0)
					client.exampleRequest(deviceType, deviceEndpoint);
			} else
				run = false;
		}

		scanner.close();

	}
}

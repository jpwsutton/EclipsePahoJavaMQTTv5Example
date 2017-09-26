/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.paho;

import java.util.ArrayList;

import org.eclipse.paho.mqttv5.client.IMqttDeliveryToken;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptionsBuilder;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.MqttToken;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttReturnCode;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

/**
 * Sample MQTTv5 Eclipse Paho Java App.
 * https://github.com/jpwsutton/EclipsePahoJavaMQTTv5Example
 * 
 * @author James Sutton (2017) - Initial Contribution
 * 
 *         This example application shows how you can add your own custom
 *         properties to a disconnect message when disconnecting from a server.
 * 
 *         Example Properties you can now add in MQTTv5 are:
 * 
 *         <ul>
 *         <li>Return Code - There are a range of Return Codes you can send to
 *         the server to indicate the reason for your disconnection, or even to
 *         trigger a certain behaviour after the client has disconnected.</li>
 *         <li>Session Expiry Interval - If the Session Expiry interval in the
 *         connect packet was NOT 0, then this will override the value that was
 *         sent upon connection (or the default), this will cause the session to
 *         expire after the selected interval.</li>
 *         <li>Reason String - A human readable string containing a more
 *         'friendly' message explaining why the client disconnected, this is
 *         only meant to be used for diagnostics and should NOT be parsed by the
 *         server.</li>
 *         <li>User Properties - An Array of {@link UserProperty} that can be
 *         used to send further information to the server, dependent on the
 *         server being able to parse them.</li>
 *         </ul>
 * 
 */
public class DisconnectExample implements MqttCallback {

	// ------ Client Configuration ------ //
	String topic = "MQTTV5";
	String content = "This Message is being sent over MQTTv5!";
	String willContent = "I've Disconnected, sorry!";
	int qos = 2;
	String broker = "tcp://localhost:1883";
	String clientId = "PahoJavaV5Client";
	int messagesToSend = 5;
	private MqttAsyncClient asyncClient;

	/**
	 * Main App Class, nothing fancy here.
	 * 
	 * @throws InterruptedException
	 */
	public DisconnectExample() throws InterruptedException {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			this.asyncClient = new MqttAsyncClient(broker, clientId, persistence);

			// Lets build our Connection Options:
			MqttConnectionOptionsBuilder conOptsBuilder = new MqttConnectionOptionsBuilder();
			MqttConnectionOptions conOpts = conOptsBuilder.serverURI(broker).cleanSession(true)
					.sessionExpiryInterval(120).automaticReconnect(true)
					.will(topic, new MqttMessage(willContent.getBytes(), qos, false)).topicAliasMaximum(1000)
					.sessionExpiryInterval(10).build();
			asyncClient.setCallback(this);

			System.out.println("Connecting to broker: " + broker);

			IMqttToken connectToken = asyncClient.connect(conOpts, null, new MqttActionListener() {

				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					System.out.println("Connected");
					SampleUtilities.printConnectDetails((MqttToken) asyncActionToken);
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					System.out.println("Failed to Connect: " + exception.getLocalizedMessage());
				}
			});

			MqttActionListener disconnectListener = new MqttActionListener() {

				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					System.out.println("Disconnect Successful!");
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					System.out.println("Disconnect Failed! : " + exception.getMessage());
				}
			};
			connectToken.waitForCompletion();
			// We should now be connected to the MQTTv5 Broker, time to disconnect.

			// Create some User Properties.
			ArrayList<UserProperty> userProperties = new ArrayList<UserProperty>();
			userProperties.add(new UserProperty("dogFood", "cheeseSandwich"));
			userProperties.add(new UserProperty("meaningOfLife", "42"));
			userProperties.add(new UserProperty("sizeOfBrain", "planet"));

			// Build up the disconnect command
			IMqttToken disconnectToken = asyncClient.disconnect(5000, // The Quiesce Timeout
					null, // User context object
					disconnectListener, // The Disconnect Listener
					MqttReturnCode.RETURN_CODE_DISCONNECT_WITH_WILL_MESSAGE, // Return code - Send will message
					30, // Session Expiry Interval in seconds
					"This is an example reason string!", // Diagnostic Reason String
					userProperties // Arraylist of User Properties
			);

			disconnectToken.waitForCompletion();
			System.out.println("Disconnected");
			asyncClient.close();
			System.exit(0);

		} catch (MqttException e) {
			System.err.println("Exception Occured whilst connecting the client: ");
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 *            - No arguments to process
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SampleUtilities.printBanner("MQTTv5 Disconnect Example Java App");
		new DisconnectExample();

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String incomingMessage = new String(message.getPayload());
		System.out.println("Incoming Message: [" + incomingMessage + "], topic:[" + topic + "]");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Delivery Complete: Message ID: " + token.getMessageId());
	}

	@Override
	public void disconnected(MqttDisconnectResponse disconnectResponse) {
		System.out.println("Disconnection Complete! : " + disconnectResponse.toString());
	}

	@Override
	public void mqttErrorOccured(MqttException exception) {
		System.out.println("MQTT Error Occured: " + exception.getMessage());
	}
}

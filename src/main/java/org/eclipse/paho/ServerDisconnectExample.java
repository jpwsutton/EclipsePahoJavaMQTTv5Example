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
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

/**
 * Sample MQTTv5 Eclipse Paho Java App.
 * https://github.com/jpwsutton/EclipsePahoJavaMQTTv5Example
 * 
 * This example application shows some of the new features available in the
 * MQTTv5 Paho Java Client.
 * 
 * <ul>
 * <li>Connecting with the extended connection properties.</li>
 * <li>Connection acknowledgement properties</li>
 * <li>Subscription acknowledgement properties</li>
 * <li>The new mqttErrorOccured Callback for non-fatal protocol errors e.g. If
 * the server sent a message with an invalid topic alias.</li>
 * <li>The new disconnected Callback replacing connectionLost</li>
 * <li>More to come, watch this space!</li>
 * </ul>
 * 
 * - - - -
 * 
 * @author James Sutton (2017) - Initial Contribution
 */
public class ServerDisconnectExample implements MqttCallback {

	// ------ Client Configuration ------ //
	String topic = "cmd/disconnectWithRC";
	String content = "139";
	String willContent = "I've Disconnected, sorry!";
	int qos = 0;
	String broker = "tcp://localhost:1883";
	String clientId = "PahoJavaV5Client";
	int messagesToSend = 5;
	private MqttAsyncClient asyncClient;

	/**
	 * Main App Class, nothing fancy here.
	 * 
	 * @throws InterruptedException
	 */
	public ServerDisconnectExample() throws InterruptedException {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			this.asyncClient = new MqttAsyncClient(broker, clientId, persistence);
			ArrayList<UserProperty> userProps = new ArrayList<UserProperty>();
			userProps.add(new UserProperty("myKey", "myValue"));

			// Lets build our Connection Options:
			MqttConnectionOptionsBuilder conOptsBuilder = new MqttConnectionOptionsBuilder();
			MqttConnectionOptions conOpts = conOptsBuilder.serverURI(broker).cleanSession(true)
					.sessionExpiryInterval(120).automaticReconnect(false)
					.topicAliasMaximum(1000).userProperties(userProps).build();
			asyncClient.setCallback(this);

			System.out.println("Connecting to broker: " + broker);

			asyncClient.connect(conOpts, null, new MqttActionListener() {

				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					System.out.println("Connected");

					SampleUtilities.printConnectDetails((MqttToken) asyncActionToken);
					try {
						
						MqttMessage msg = new MqttMessage(content.getBytes());
						msg.setQos(qos);
						asyncClient.publish(topic, msg);
					} catch (MqttException e) {
						System.err.println("Exception Occured whilst Subscribing:");
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					System.out.println("Failed to Connect: " + exception.getLocalizedMessage());

				}
			});

			/**
			asyncClient.disconnect(5000);
			System.out.println("Disconnected");
			asyncClient.close();
			System.exit(0);
			*/

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
		SampleUtilities.printBanner("MQTTv5 Sample Java App");
		new ServerDisconnectExample();

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
		System.exit(0);

	}

	@Override
	public void mqttErrorOccured(MqttException exception) {
		System.out.println("MQTT Error Occured: " + exception.getMessage());
	}
}

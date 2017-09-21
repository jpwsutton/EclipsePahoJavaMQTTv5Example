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

/**
 * Sample MQTTv5 Eclipse Paho Java App.
 * https://github.com/jpwsutton/EclipsePahoJavaMQTTv5Example
 * @author James Sutton (2017) - Initial Contribution
 */
public class App implements MqttCallback {

	// ------ Client Configuration ------ //
	String topic = "MQTTV5";
	String content = "This Message is being sent over MQTTv5!";
	String willContent = "I've Disconnected, sorry!";
	int qos = 2;
	String broker = "tcp://localhost:1883";
	String clientId = "PahoJavaV5Client";
	int messagesToSend = 5;
	private int sentMessageCount = 0;

	/**
	 * Main App Class, nothing fancy here.
	 * @throws InterruptedException
	 */
	public App() throws InterruptedException {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			final MqttAsyncClient asyncClient = new MqttAsyncClient(broker, clientId, persistence);

			// Lets build our Connection Options:
			MqttConnectionOptionsBuilder conOptsBuilder = new MqttConnectionOptionsBuilder();
			MqttConnectionOptions conOpts = conOptsBuilder.serverURI(broker).cleanSession(true)
					.sessionExpiryInterval(120).automaticReconnect(true)
					.will(topic, new MqttMessage(willContent.getBytes(), qos, false)).topicAliasMaximum(1000).build();
			asyncClient.setCallback(this);

			System.out.println("Connecting to broker: " + broker);

			asyncClient.connect(conOpts, null, new MqttActionListener() {

				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					System.out.println("Connected");

					printConnectDetails((MqttToken) asyncActionToken);
					try {
						IMqttToken subToken = asyncClient.subscribe(topic, qos);
						subToken.waitForCompletion();
						printSubscriptionDetails((MqttToken) subToken);
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

			while (true) {

				// asyncClient.publish(topic, new MqttMessage(content.getBytes(), 2, false));
				Thread.sleep(1000);
				System.out.println("Sending Message: " + sentMessageCount);
				String message = content + " " + sentMessageCount;
				asyncClient.publish(topic, new MqttMessage(message.getBytes(), 2, false));
				if (sentMessageCount == messagesToSend) {
					System.out.println("Have sent " + messagesToSend + ", stopping client.");
					break;
				}
				sentMessageCount++;

			}
			asyncClient.disconnect(5000);
			System.out.println("Disconnected");
			asyncClient.close();
			System.exit(0);

		} catch (MqttException e) {
			System.err.println("Exception Occured whilst connecting the client: ");
			e.printStackTrace();
		}
	}

	/**
	 * @param args - No arguments to process
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		printBanner();
		new App();

	}

	private static void printBanner() {
		System.out.println("------------------------------");
		System.out.println("    MQTTv5 Sample Java App    ");
		System.out.println("------------------------------");
	}

	/**
	 * Prints the details of a Subscription Acknowledgement sent by the server.
	 * @param token the {@link MqttToken} containing the MQTTv5 SUBACK
	 */
	public static void printSubscriptionDetails(MqttToken token) {
		System.out.println("Subscription Response: [reasonString=" + token.getReasonString() + ", user"
				+ ", userDefinedProperties=" + token.getUserDefinedProperties());

	}

	/**
	 * Prints the details of a Connection Acknowledgement sent by the server.
	 * @param token the {@link MqttToken} containing the MQTTv5 CONNACK
	 */
	public static void printConnectDetails(MqttToken token) {
		System.out.println("Connection Response: [ sessionPresent=" + token.getSessionPresent() + ", responseInfo="
				+ token.getResponseInformation() + ", assignedClientIdentifier=" + token.getAssignedClientIdentifier()
				+ ", serverKeepAlive=" + token.getServerKeepAlive() + ", authMethod=" + token.getAuthMethod()
				+ ", authData=" + token.getAuthData() + ", serverReference=" + token.getServerReference()
				+ ", reasonString=" + token.getReasonString() + ", recieveMaximum=" + token.getRecieveMaximum()
				+ ", topicAliasMaximum=" + token.getTopicAliasMaximum() + ", maximumQoS=" + token.getMaximumQoS()
				+ ", retainAvailable=" + token.isRetainAvailable() + ", userDefinedProperties="
				+ token.getUserDefinedProperties() + ", maxPacketSize=" + token.getMaximumPacketSize()
				+ ", wildcardSubscriptionAvailable=" + token.isWildcardSubscriptionAvailable()
				+ ", subscriptionIdentifiersAvailable=" + token.isSubscriptionIdentifiersAvailable()
				+ ", sharedSubscriptionAvailable=" + token.isSharedSubscriptionAvailable() + "]");
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
		System.out.println("Disconnection Complete!");

	}
}

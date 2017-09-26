package org.eclipse.paho;

import java.util.Collections;

import org.eclipse.paho.mqttv5.client.MqttToken;

public class SampleUtilities {

	/**
	 * Prints the details of a Subscription Acknowledgement sent by the server.
	 * 
	 * @param token
	 *            the {@link MqttToken} containing the MQTTv5 SUBACK
	 */
	public static void printSubscriptionDetails(MqttToken token) {
		System.out.println("Subscription Response: [reasonString=" + token.getReasonString() + ", user"
				+ ", userDefinedProperties=" + token.getUserDefinedProperties());

	}

	/**
	 * Prints the details of a Connection Acknowledgement sent by the server.
	 * 
	 * @param token
	 *            the {@link MqttToken} containing the MQTTv5 CONNACK
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
	
	/**
	 * Prints a simple Banner
	 * @param bannerText the text to display
	 */
	public static void printBanner(String bannerText) {
		int bannerWidth = bannerText.length() + 10;
		String border = String.join("",  Collections.nCopies(bannerWidth, "-"));
		System.out.println(border);
		System.out.println("     " + bannerText + "     ");
		System.out.println(border);
	}

}

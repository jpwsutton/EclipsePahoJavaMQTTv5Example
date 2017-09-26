# EclipsePahoJavaMQTTv5Example
An Example MQTTv5 Application using the new Eclipse Paho Java Client

![A screen recording of the client in action.](MQTTv5-1.gif)

## Build and Run

 * To build: ```mvn package```

### Examples

 There are a number of example classes in this project, allowing you to test out various parts of the new MQTTv5 functionality.

#### Main Example
 This example shows off some of the new features available in the MQTTv5 Paho Java Client:

 - Connecting with the extended connection properties.
 - Connection acknowledgement properties.
 - Subscription acknowledgement properties.
 - The new mqttErrorOccured Callback for non-fatal protocol errors e.g. If the server sent a message with an invalid topic alias.
 - The new disconnected Callback replacing connectionLost.
 - More to come, watch this space!

To run it, build the project and then execute: ```java -cp target/mqttv5-sample-app-1.0-SNAPSHOT.jar org.eclipse.paho.App```


#### Client Disconnect Example
This example application shows how you can add your own custom properties to a disconnect message when disconnecting from a server.

 - Return Code - There are a range of Return Codes you can send to the server to indicate the reason for your disconnection, or even to trigger a certain behaviour after the client has disconnected.
 - Session Expiry Interval - If the Session Expiry interval in the  connect packet was NOT 0, then this will override the value that was sent upon connection (or the default), this will cause the session to expire after the selected interval.
 - Reason String - A human readable string containing a more 'friendly' message explaining why the client disconnected, this is only meant to be used for diagnostics and should NOT be parsed by the server.
 - User Properties - An Array of {@link UserProperty} that can be used to send further information to the server, dependent on the server being able to parse them.

To run it, build the project and then execute: ```java -cp target/mqttv5-sample-app-1.0-SNAPSHOT.jar org.eclipse.paho.DisconnectExample```


## Finding an MQTTv5 Broker to test against

This client has so far been tested agains the Eclipse Paho Interoperability v5 Broker written by [Ian Craggs](https://github.com/icraggs) and can be found in the following repository: https://github.com/eclipse/paho.mqtt.testing.

To run, simply enter the interoperability directory and run ```python3 startbroker5.py```.


## Features so far:

 - Packet Serialisation / Deserialisation for all MQTTv5 Packets as of the last Working Draft Spec with test suite
 - Connect Options have been expanded to include new properties
 - CONNACK properties can be returned and inspected.
 - SUBACK properties can be returned and inspected.
 - Topic Aliases "Should" be implemented, but have not fully tested yet.
 - New disconnected callback replacing connectionLost. However not fully implemented.
 - Numerous other small changes to the API here and there.


## Help, something doesn't work! / This looks terrible! / What about x!

The [Paho Java Client](https://github.com/eclipse/paho.mqtt.java/tree/mqttv5-new) is under active development and as such may be incomplete / broken a lot of the time right now. However, the more feedback and help we get on it, the better it will get! If you have any issues, please raise a bug against the client [here](https://github.com/eclipse/paho.mqtt.java/issues), but **please** prefix it with 'MQTTv5' so we know that it's not an issue with the current v3.1.1 client.

If you have any ideas about how the API should be designed going forward, then please chip in on [this](https://github.com/eclipse/paho.mqtt.java/issues/389) issue.

And of course, if you think of an amazing new feature for the v5 client, have a go at implementing it and submit a pr against the mqttv5-new branch!

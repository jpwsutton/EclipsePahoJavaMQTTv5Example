# EclipsePahoJavaMQTTv5Example
An Example MQTTv5 Application using the new Eclipse Paho Java Client

## Build and Run

 * To build: ```mvn package```
 * Then to run it: ```java -cp target/mqttv5-sample-app-1.0-SNAPSHOT.jar org.eclipse.paho.App```


## Finding an MQTTv5 Broker to test against

This client has so far been tested agains the Eclipse Paho Interoperability v5 Broker written by [Ian Craggs](https://github.com/icraggs) and can be found in the following repository: https://github.com/eclipse/paho.mqtt.testing.

To run, simply enter the interoperability directory and run ```python3 startbroker5.py```.

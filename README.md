# AVCN Simulator
Simulator that fetches changes of configuration from kafka and sends them to VES client.

## What does it do?
The simulator processes notifications from NETCONF server. It does this by being a subscriber of a Kafka topic that is fed 
with NETCONF notifications. The topic name is "config". Incoming notifications are then processed and output of this
 processing is sent to VES client.

### Processing details
1. When last element contain value (equal sign) then cut it together with `'/'` from path and fill attributeList object with key equal to name of element before `'='` and value equal to element after `'='` keeping key and value quoted (`""`),
2. Convert remaining `'/'` into `' , '` ,
3. Convert all container types (without `[key='value']`) to `<container name>=<container name>`
4. Convert all list types (with `[key='value']`) to `<list name>=<value>`
````
{containerA}/{listB}[{keyB}={valueB}]/{containerC}/{listD}[{keyD}={valueD}]/{[leaf, leaf-list]} = {value of leaf}

{
  "variables": {
    "dn": " {containerA}={containerA} , {listB}={valueB} , {containerC}={containerC} , {listD}={valued}",
    "attributesList": {
      "{leaf, leaf-list}": "{value of leaf}"
    }
  }
} 
````

### VES client Request
The resulting request to VES client looks like the following (the "dn" and "attributesList"
 parameters are dependent on the actual NETCONF notification's value):
````
{
  "simulatorParams": {
    "repeatCount": 1,
    "repeatInterval": 1,
    "vesServerUrl": ""
  },
  "templateName": "notification.json",
  "patch": {},
  "variables": {
    "dn": "",
    "attributesList": {}
  }
}
````

### Examples
````
/example-sports:sports/person[name='name 1'] (list instance)

{
  "simulatorParams": { },
  "templateName": "cmNotify.json",
  "patch": { },
  "variables": {
    "dn":" example-sports:sports= example-sports:sports , person=name 1",
    "attributesList": { }
  }        
}
````

````
/example-sports:sports/person[name='name 1']/name = name 1

{
  "simulatorParams": { },
  "templateName": "cmNotify.json",
  "patch": { },
  "variables": {
    "dn":" example-sports:sports= example-sports:sports , person=name 1",
    "attributesList": { "name": "name 2" }
  }        
}
````

````
/example-sports:sports/person[name='name 2'] (list instance)

{
  "simulatorParams": { },
  "templateName": "cmNotify.json",
  "patch": { },
  "variables": {
    "dn":" example-sports:sports= example-sports:sports , person=name 2",
    "attributesList": { }
  }        
}
````

````
/example-sports:sports/team[name='team 1']/player[name='player 1'] (list instance)

{
  "simulatorParams": { },
  "templateName": "cmNotify.json",
  "patch": { },
  "variables": {
    "dn":" example-sports:sports= example-sports:sports , player=player 1",
    "attributesList": { }
  }        
}
````

````
/example-sports:sports/team[name='team 1']/player[name='player 1']/name = player 1
{
  "simulatorParams": { },
  "templateName": "cmNotify.json",
  "patch": { },
   "variables": {
     "dn":" example-sports:sports= example-sports:sports , player=player 1",
     "attributesList": { "name": "player 1"”" }
   }        
}
````
## Simulator configuration
It's possible to override default configuration. Following environment variables can be set 

    KAFKA_BOOTSTRAP_SERVERS - Kafka host, by default kafka1:9092 
    
    KAFKA_APPLICATION_ID - An identifier for the stream processing application. Must be unique within the Kafka cluster. 
                            By default avcn-simulator  
    
    KAFKA_SOURCE_TOPIC - Kafka topic, where Netconf simulator pushes notification, by default config
        
    REST_CLIENT_PNF_SIMULATOR_ENDPOINT -  VES client's URL, by default pnf-simulator:5000/simulator/start
    
    REST_CLIENT_VES_ENDPOINT - VES URL, AVNC events will be send via VES client to this address 
    
## Start avcn-manager
To run avcn manager, config ves and pnf-simulator ip in docker-compose.yaml
REST_CLIENT_VES_ENDPOINT: http://<ves>:8080/eventListener/v7
REST_CLIENT_VESCLIENT_ENDPOINT: http://<ves-cilent>:5000/simulator/start

and start docker-compose
    
    docker-compose up

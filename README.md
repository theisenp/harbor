Harbor
======

Harbor is an [LCM][1] based peer discovery library for Java. Clients specify the protocols that they support (e.g. IPC socket for local connections, ZMQ for remote ones) and are notified when peers are discovered. Clients can optionally specify the LCM address, port, time to live, publish rate, and timeout length. It is up to clients to implement the secondary connections once Harbor has provided the set of available peers.

Usage
-----

### Peer ###

A `Peer` is a description of a Harbor client and the protocols it supports. Each peer has:

- *ID*: A unique identifier. Multiple instances of the same client should have different IDs.
- *Type*: A not necessarily unique identifier. Multiple instances of the same client may have the same type.
- *Status*: One of { `CONNECTED`, `ACTIVE`, `INACTIVE`, `DISCONNECTED` }
- *Description*: An optional explanation of the peer, mainly for logging or monitoring.
- *Protocols*: A map of protocols and their corresponding addresses.

### Filter ###

`Filter` is an interface for objects that pass or fail peers based on some internal logic. Harbor provides some common filters right out of the box:

- `Filter.PASS` - Passes all peers
- `Filter.FAIL` - Fails all peers
- `TypeFilter` - Passes peers of a particular type
- `StatusFilter` - Passes peers with a particular status
- `ProtocolFilter` - Passes peers that support a particular protocol
- `ConjunctiveFilter` - Passes peers that pass all of a set of multiple filters
- `DisjunctiveFilter` - Passes peers that pass any of a set of multiple filters


### Example ###

```java
// Describe your client and the protocols it supports
Peer self = new Peer.Builder()
		.id(UUID.randomUUID().toString())
		.type("example")
		.status(Peer.Status.ACTIVE)
		.description("An example harbor client")
		.protocol("IPC", "/tmp/example")
		.protocol("TCP", "192.168.0.1:5555")
		.build();

// Configure a harbor instance for your client
Harbor harbor = new Harbor.Builder()
		.address("239.255.76.67")
		.port(7667)
		.ttl(1)
		.period(Duration.millis(500))
		.timeout(Duration.standardSeconds(2))
		.self(self)
		.build();

// Register a listener to log peer events		
harbor.addListener(new Harbor.Listener() {
	@Override
	public void onConnected(Peer peer) {
		System.out.println("Peer connected: " + peer.getId());
	}
	
	@Override
	public void onActive(Peer peer) {
		System.out.println("Peer active: " + peer.getId());
	}
	
	@Override
	public void onInactive(Peer peer) {
		System.out.println("Peer inactive: " + peer.getId());
	}
	
	@Override
	public void onDisconnected(Peer peer) {
		System.out.println("Peer disconnected: " + peer.getId());
	}
});

// Register an asynchronous callback for a particular sort of peer
ListenableFuture<Peer> future = harbor.find(new TypeFilter("example"));
Futures.transform(future, new Function<Peer, Void>() {
	@Override
	public Void apply(Peer peer) {
		// TODO: Connect to the peer using a mutually supported protocol
		return null;
	}
});

// Start the peer discovery
harbor.open();
```

Build
-----

Harbor is built with [Maven][2] and depends on the `lcm-gen` tool, which can be built from the [LCM source code][1]. To build the project and install it in your local Maven repositiory, type:

	mvn clean install
	
If `lcm-gen` is not on the the path, you will need to specify its location:

	mvn clean install -Dlcm-gen=/path/to/lcm-gen

Download
--------

To include Harbor in another Maven project, add the following dependency to the project's `pom.xml`:

```xml
<dependency>
  <groupId>com.theisenp.harbor</groupId>
  <artifactId>harbor</artifactId>
  <version>(latest version)</version>
</dependency>
```

Developed By
------------

* Patrick Theisen - <theisenp@gmail.com>

License
-------

    Copyright 2014 Patrick Theisen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[1]: lcm.googlecode.com
[2]: http://maven.apache.org/
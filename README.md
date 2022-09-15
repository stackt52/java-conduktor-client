## HIE Conduktor Java Client

The app is written to demonstrate how to reliably set up a gRPC Connection in java.
Since Streams in HTTP/2 enable multiple concurrent conversations on a single connection; 
channels extend this concept by enabling multiple streams over multiple concurrent connections.

We will configure our gRPC connection to support HTTP/2’s long-lived connections so that we'll
keep our connections alive, healthy, and utilized.
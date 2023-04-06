# Load Balancer

Asynchronous round robin load balancer created using functional concurrency primitives. 

## Configuration
Your `application.conf` file should like as follows: 

```
http {
  port=<PORT>
  host=<HOST>
}

servers = ["<SERVER_1>", "<SERVER_2>", ..., "<SERVER_N>"]
```

see current config for an example. 

This configuration creates `zio-http` application running on `<http.host>:<http.port>` and redirects
all the requests into some server specified in the `servers` list.  
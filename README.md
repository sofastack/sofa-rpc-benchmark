# SOFARPC Benchmark
This project focuses on benchmarking and profiling sofarpc with the combination of different serialization and transporter options. The code and the idea behinds it is inspired by [Dubbo Benchmark](https://github.com/apache/dubbo-benchmark).

## How To Run Benchmark
Clone this project onto your desktop, then
* Start the target server first, for example:

```bash
./benchmark.sh sofa-rpc-bolt-hessian-server
```

* Start the corresponding client, for example: 

```bash
./benchmark.sh sofa-rpc-bolt-hessian-client
```

## How to Run Profiling
* Start the target server first, for example:

```bash
./benchmark.sh -m profiling sofa-rpc-bolt-hessian-server
```

* Start the corresponding client, for example:

```bash
./benchmark.sh -m profiling sofa-rpc-bolt-hessian-client
```

## Specify parameters 

### Specify hostname, port , output file and client thread num for service

* Start the target server and specify 12201 first, for example:

```bash
./benchmark.sh -p 12201 sofa-rpc-bolt-hessian-server
```

* Start the corresponding client, and specify port, specify output file, specify number of client threads, for example:

```bash
./benchmark.sh -s 127.0.0.1 -p 12201 -f result.json -t 1000 sofa-rpc-bolt-hessian-client
```

### Specify warmupIterations, warmupTime , warmupTime and measurementTime for test

* Start the corresponding client, and specify warmupIterations, specify warmupTime, specify measurementIterations, specify measurementTime, for example:

```bash
./benchmark.sh -a "--warmupIterations=3 --warmupTime=10 --measurementIterations=3 --measurementTime=150" sofa-rpc-bolt-hessian-client
```

### Specify request size for request call and result format for result

```bash
./benchmark.sh -e "-Drequest.size=10240 -Dresult.format=TEXT" sofa-rpc-bolt-hessian-client
```
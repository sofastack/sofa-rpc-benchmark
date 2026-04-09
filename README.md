# SOFARPC Benchmark

This project focuses on benchmarking and profiling SOFARPC with different protocol and serialization options. The code and the idea behind it is inspired by [Dubbo Benchmark](https://github.com/apache/dubbo-benchmark).

## Modules

| Module | Protocol | Description |
|--------|----------|-------------|
| `sofa-rpc-bolt-hessian-server` / `client` | Bolt + Hessian | JMH benchmark |
| `sofa-rpc-triple-pojo-server` / `client` | Triple (HTTP/2) | JMH benchmark, tests `UserPojoService` (POJO, unary + streaming) |
| `sofa-rpc-triple-proto-server` / `client` | Triple (HTTP/2) | JMH benchmark, tests `IUserService` (Protobuf, unary + streaming) |

---

## Bolt + Hessian Benchmark

### Run Benchmark

```bash
# Terminal 1: start server (default port 12200)
./benchmark.sh sofa-rpc-bolt-hessian-server

# Terminal 2: start client
./benchmark.sh sofa-rpc-bolt-hessian-client
```

### Run Profiling

```bash
./benchmark.sh -m profiling sofa-rpc-bolt-hessian-server
./benchmark.sh -m profiling sofa-rpc-bolt-hessian-client
```

---

## Triple Benchmark

`TripleClient` benchmarks all four call modes of `UserPojoService`:

| Benchmark | Call Mode |
|-----------|-----------|
| `unary` | Synchronous unary (`getUser`) |
| `serverStream` | Server streaming (`listUserServerStream`) |
| `clientStream` | Client streaming (`batchCreateUserClientStream`) |
| `biStream` | Bidirectional streaming (`verifyUserBiStream`) |

### Run Benchmark

```bash
# Terminal 1: start server (default port 50051)
./benchmark.sh -p 50051 sofa-rpc-triple-pojo-server

# Terminal 2: start client
# Note: use 127.0.0.1 instead of localhost to avoid IPv6 proxy issues
./benchmark.sh -s 127.0.0.1 -p 50051 sofa-rpc-triple-pojo-client
```

---

## Triple Proto Benchmark

`TripleProtoClient` benchmarks all four call modes of `IUserService` using **Protobuf** wire format:

| Benchmark | Call Mode |
|-----------|-----------|
| `unary` | Synchronous unary (`getUser`) |
| `serverStream` | Server streaming (`listUserServerStream`) |
| `clientStream` | Client streaming (`batchCreateUser`) |
| `biStream` | Bidirectional streaming (`verifyUserBiStream`) |

### Run Benchmark

```bash
# Terminal 1: start server (default port 50052)
./benchmark.sh -p 50052 sofa-rpc-triple-proto-server

# Terminal 2: start client
./benchmark.sh -s 127.0.0.1 -p 50052 sofa-rpc-triple-proto-client
```

---

## Common Parameters

### Specify port, host, output file, thread count

```bash
./benchmark.sh -p 12201 sofa-rpc-bolt-hessian-server
./benchmark.sh -s 127.0.0.1 -p 12201 -f result.json -t 64 sofa-rpc-bolt-hessian-client
```

### Specify JMH warmup and measurement iterations

```bash
./benchmark.sh -a "--warmupIterations=3 --warmupTime=10 --measurementIterations=3 --measurementTime=150" sofa-rpc-bolt-hessian-client
```

### Specify request size and result format

```bash
./benchmark.sh -e "-Drequest.size=10240 -Dresult.format=TEXT" sofa-rpc-bolt-hessian-client
```

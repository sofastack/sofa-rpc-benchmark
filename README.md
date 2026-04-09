# SOFARPC Benchmark

This project focuses on benchmarking and profiling SOFARPC with different protocol and serialization options. The code and the idea behind it is inspired by [Dubbo Benchmark](https://github.com/apache/dubbo-benchmark).

## Modules

| Module | Protocol | Description |
|--------|----------|-------------|
| `sofa-rpc-bolt-hessian-server` / `client` | Bolt + Hessian | JMH benchmark |
| `sofa-rpc-triple-server` / `client` | Triple (HTTP/2) | JMH benchmark, tests `StreamingUserService` (unary + streaming) |

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

`TripleClient` benchmarks all four call modes of `StreamingUserService`:

| Benchmark | Call Mode |
|-----------|-----------|
| `unary` | Synchronous unary (`getUser`) |
| `serverStream` | Server streaming (`listUserServerStream`) |
| `clientStream` | Client streaming (`batchCreateUserClientStream`) |
| `biStream` | Bidirectional streaming (`verifyUserBiStream`) |

### Run Benchmark

```bash
# Terminal 1: start server (default port 50051)
./benchmark.sh -p 50051 sofa-rpc-triple-server

# Terminal 2: start client
# Note: use 127.0.0.1 instead of localhost to avoid IPv6 proxy issues
./benchmark.sh -s 127.0.0.1 -p 50051 sofa-rpc-triple-client
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

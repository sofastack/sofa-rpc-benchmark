/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.benchmark;

import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.rpc.benchmark.proto.BatchCreateResponse;
import com.alipay.sofa.rpc.benchmark.proto.CreateUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.GetUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.GetUserResponse;
import com.alipay.sofa.rpc.benchmark.proto.ListUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.SofaUserServiceTriple;
import com.alipay.sofa.rpc.benchmark.proto.VerifyUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.VerifyUserResponse;
import com.alipay.sofa.rpc.benchmark.utils.JMHHelper;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import io.grpc.stub.StreamObserver;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
public class TripleProtoClient {

    private static final Logger                                      LOGGER      = LoggerFactory
                                                                                     .getLogger(TripleProtoClient.class);

    private static int                                               CONCURRENCY = 32;

    private final SofaUserServiceTriple.IUserService                 userService;

    private final ConsumerConfig<SofaUserServiceTriple.IUserService> consumerConfig;

    private final AtomicInteger                                      counter     = new AtomicInteger(0);

    public TripleProtoClient() {
        String host = System.getProperty("server.host", "127.0.0.1");
        String port = System.getProperty("server.port", "50052");
        String threadNum = System.getProperty("thread.num");
        if (StringUtil.isNotBlank(threadNum)) {
            CONCURRENCY = Integer.parseInt(threadNum);
        }
        String nonProxyHosts = System.getProperty("http.nonProxyHosts", "");
        if (!nonProxyHosts.contains("127.0.0.1")) {
            System.setProperty("http.nonProxyHosts",
                nonProxyHosts.isEmpty() ? "localhost|127.0.0.1" : nonProxyHosts + "|localhost|127.0.0.1");
        }
        consumerConfig = new ConsumerConfig<SofaUserServiceTriple.IUserService>()
            .setRepeatedReferLimit(10)
            .setInterfaceId(SofaUserServiceTriple.IUserService.class.getName())
            .setProtocol("tri")
            .setDirectUrl("tri://" + host + ":" + port)
            .setTimeout(4000);
        userService = consumerConfig.refer();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @TearDown
    public void close() {
        consumerConfig.unRefer();
    }

    /**
     * Unary benchmark: single synchronous request-response.
     */
    @Benchmark
    @BenchmarkMode({ Mode.Throughput })
    @OutputTimeUnit(TimeUnit.SECONDS)
    public GetUserResponse unary() {
        long id = counter.getAndIncrement();
        return userService.getUser(GetUserRequest.newBuilder().setId(id).build());
    }

    /**
     * Server streaming benchmark: one request, server pushes back a page of users.
     */
    @Benchmark
    @BenchmarkMode({ Mode.Throughput })
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int serverStream() throws InterruptedException {
        int pageNo = counter.getAndIncrement();
        CountDownLatch latch = new CountDownLatch(1);
        final int[] receivedCount = { 0 };
        userService.listUserServerStream(
            ListUserRequest.newBuilder().setPageNo(pageNo).setPageSize(10).build(),
            new StreamObserver<GetUserResponse>() {
                @Override
                public void onNext(GetUserResponse user) {
                    receivedCount[0]++;
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }

                @Override
                public void onError(Throwable t) {
                    LOGGER.error("serverStream error", t);
                    latch.countDown();
                }
            });
        latch.await(4, TimeUnit.SECONDS);
        return receivedCount[0];
    }

    /**
     * Client streaming benchmark: send a batch of users, wait for server summary.
     */
    @Benchmark
    @BenchmarkMode({ Mode.Throughput })
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int clientStream() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final int[] result = { 0 };
        StreamObserver<CreateUserRequest> requestObserver = userService
            .batchCreateUser(new StreamObserver<BatchCreateResponse>() {
                @Override
                public void onNext(BatchCreateResponse response) {
                    result[0] = response.getCount();
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }

                @Override
                public void onError(Throwable t) {
                    LOGGER.error("clientStream error", t);
                    latch.countDown();
                }
            });
        for (int i = 0; i < 5; i++) {
            long id = counter.getAndIncrement();
            requestObserver.onNext(CreateUserRequest.newBuilder()
                .setId(id)
                .setName("User-" + id)
                .setEmail("user" + id + "@example.com")
                .setMobile("1861234567" + (id % 10))
                .setAddress("Address-" + id)
                .build());
        }
        requestObserver.onCompleted();
        latch.await(4, TimeUnit.SECONDS);
        return result[0];
    }

    /**
     * Bidirectional streaming benchmark: send users one by one, receive each verified response.
     */
    @Benchmark
    @BenchmarkMode({ Mode.Throughput })
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int biStream() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final int[] receivedCount = { 0 };
        StreamObserver<VerifyUserRequest> requestObserver = userService
            .verifyUserBiStream(new StreamObserver<VerifyUserResponse>() {
                @Override
                public void onNext(VerifyUserResponse response) {
                    receivedCount[0]++;
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }

                @Override
                public void onError(Throwable t) {
                    LOGGER.error("biStream error", t);
                    latch.countDown();
                }
            });
        for (int i = 0; i < 3; i++) {
            long id = counter.getAndIncrement();
            requestObserver.onNext(VerifyUserRequest.newBuilder()
                .setId(id)
                .setName("User-" + id)
                .setEmail("user" + id + "@example.com")
                .build());
        }
        requestObserver.onCompleted();
        latch.await(4, TimeUnit.SECONDS);
        return receivedCount[0];
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info(Arrays.toString(args));
        ChainedOptionsBuilder optBuilder = JMHHelper.newBaseChainedOptionsBuilder(args)
            .include(TripleProtoClient.class.getSimpleName())
            .threads(CONCURRENCY)
            .forks(1);

        Options opt = optBuilder.build();
        new Runner(opt).run();
    }
}

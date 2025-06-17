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
package com.alipay.sofa.benchmark;

import com.alipay.sofa.benchmark.bean.Page;
import com.alipay.sofa.benchmark.bean.User;
import com.alipay.sofa.benchmark.rpc.AbstractClient;
import com.alipay.sofa.benchmark.service.UserService;
import com.alipay.sofa.benchmark.utils.JMHHelper;
import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.rpc.config.ConsumerConfig;
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
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class BoltClient extends AbstractClient {

    private static final Logger               LOGGER      = LoggerFactory.getLogger(BoltClient.class);

    private static int                        CONCURRENCY = 32;

    private final UserService                 userService;

    private final ConsumerConfig<UserService> consumerConfig;

    public BoltClient() {
        String port = System.getProperty("server.port", "12200");
        String threadNum = System.getProperty("thread.num");
        if (StringUtil.isNotBlank(threadNum)) {
            CONCURRENCY = Integer.parseInt(threadNum);
        }
        consumerConfig = new ConsumerConfig<UserService>()
            .setRepeatedReferLimit(10)
            .setInterfaceId(UserService.class.getName()) // 指定接口
            .setProtocol("bolt") // 指定协议
            .setDirectUrl("bolt://127.0.0.1:" + port) // 指定直连地址
            .setTimeout(4000);
        // 生成代理类
        userService = consumerConfig.refer();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }

    @TearDown
    public void close() {
        consumerConfig.unRefer();
    }

    //@Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Override
    public boolean existUser() throws Exception {
        return super.existUser();
    }

    //@Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Override
    public boolean createUser() throws Exception {
        return super.createUser();
    }

    //@Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Override
    public User getUser() throws Exception {
        return super.getUser();
    }

    //@Benchmark
    @BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Override
    public Page<User> listUser() throws Exception {
        return super.listUser();
    }

    @Benchmark
    @BenchmarkMode({ Mode.Throughput })
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Override
    public User verifyUser() throws Exception {
        return super.verifyUser();
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info(Arrays.toString(args));
        ChainedOptionsBuilder optBuilder = JMHHelper.newBaseChainedOptionsBuilder(args)
            .include(BoltClient.class.getSimpleName())
            .threads(CONCURRENCY)
            .forks(1);

        Options opt = optBuilder.build();
        new Runner(opt).run();
    }
}

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
package com.alipay.sofa.rpc.benchmark.demo;

import com.alipay.sofa.rpc.benchmark.TripleProtoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Triple Protobuf single-shot demo client.
 *
 * Start sofa-rpc-triple-proto-server first (default port 50052), then run main().
 *
 * Override host/port via JVM properties:
 *   -Dserver.host=127.0.0.1 -Dserver.port=50052
 */
public class TripleProtoClientDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripleProtoClientDemo.class);

    public static void main(String[] args) throws Exception {
        TripleProtoClient client = new TripleProtoClient();
        try {
            LOGGER.info("[unary]        result={}", client.unary());
            LOGGER.info("[serverStream] received={}", client.serverStream());
            LOGGER.info("[clientStream] received={}", client.clientStream());
            LOGGER.info("[biStream]     received={}", client.biStream());
        } finally {
            client.close();
        }
    }
}

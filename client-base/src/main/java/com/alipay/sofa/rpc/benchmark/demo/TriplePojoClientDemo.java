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

import com.alipay.sofa.rpc.benchmark.TriplePojoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Triple POJO single-shot demo client.
 *
 * Start sofa-rpc-triple-pojo-server first (default port 50051), then run main().
 *
 * Override host/port via JVM properties:
 *   -Dserver.host=127.0.0.1 -Dserver.port=50051
 */
public class TriplePojoClientDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriplePojoClientDemo.class);

    public static void main(String[] args) throws Exception {
        TriplePojoClient client = new TriplePojoClient();
        try {
            LOGGER.info("[unary]        result={}", client.unary());
            LOGGER.info("[serverStream] received={}", client.serverStream());
            LOGGER.info("[clientStream] result={}", client.clientStream());
            LOGGER.info("[biStream]     received={}", client.biStream());
        } finally {
            client.close();
        }
    }
}

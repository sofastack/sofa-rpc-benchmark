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

import com.alipay.sofa.rpc.benchmark.proto.SofaUserServiceTriple;
import com.alipay.sofa.rpc.benchmark.service.UserServiceProtoImpl;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Triple proto-based server demo.
 *
 * The service interface and stub are generated from user_service.proto by
 * sofa-rpc-compiler (SofaTripleGenerator). The impl extends the generated
 * {@code SofaUserServiceTriple.UserServiceImplBase} which implements
 * {@code BindableService}, so SOFARPC Triple registers it as a native gRPC service.
 */
public class TripleProtoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripleProtoServer.class);

    public static void main(String[] args) {
        String port = System.getProperty("server.port", "50052");

        ServerConfig serverConfig = new ServerConfig()
            .setProtocol("tri")
            .setPort(Integer.parseInt(port))
            .setDaemon(false);

        ProviderConfig<SofaUserServiceTriple.IUserService> providerConfig = new ProviderConfig<SofaUserServiceTriple.IUserService>()
            .setInterfaceId(SofaUserServiceTriple.IUserService.class.getName())
            .setRef(new UserServiceProtoImpl())
            .setServer(serverConfig);

        providerConfig.export();

        LOGGER.info("Triple proto server started on port {}", port);
        LOGGER.info("Service: {}", SofaUserServiceTriple.getServiceName());
    }
}

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

import com.alipay.sofa.rpc.benchmark.service.UserPojoService;
import com.alipay.sofa.rpc.benchmark.service.UserPojoServiceImpl;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.caucho.hessian.io.Hessian2Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriplePojoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriplePojoServer.class);

    public static void main(String[] args) {
        String port = System.getProperty("server.port", "50051");

        ServerConfig serverConfig = new ServerConfig()
            .setProtocol("tri")
            .setPort(Integer.parseInt(port))
            .setDaemon(false);

        ProviderConfig<UserPojoService> providerConfig = new ProviderConfig<UserPojoService>()
            .setInterfaceId(UserPojoService.class.getName())
            .setRef(new UserPojoServiceImpl())
            .setServer(serverConfig);

        providerConfig.export();

        LOGGER.info("Triple pojo server started on port {}", port);
        LOGGER.info("Service: {}", UserPojoService.class.getName());
    }
}

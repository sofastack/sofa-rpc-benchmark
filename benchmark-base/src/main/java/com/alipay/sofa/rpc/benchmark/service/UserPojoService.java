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
package com.alipay.sofa.rpc.benchmark.service;

import com.alipay.sofa.rpc.benchmark.bean.User;
import com.alipay.sofa.rpc.transport.SofaStreamObserver;

/**
 * Triple streaming service interface.
 *
 * Demonstrates three streaming modes supported by Triple protocol:
 * - Server Streaming: client sends one request, server responds with a stream
 * - Client Streaming: client sends a stream, server responds with one response
 * - Bidirectional Streaming: both sides stream messages
 */
public interface UserPojoService {

    /**
     * Server Streaming: query users by page, server pushes each user one by one.
     *
     * @param pageNo           page number to query
     * @param responseObserver observer to receive each User response
     */
    void listUserServerStream(int pageNo, SofaStreamObserver<User> responseObserver);

    /**
     * Client Streaming: client uploads a batch of users, server returns a summary count.
     *
     * @param responseObserver observer to receive the final summary response
     * @return observer for the client to send User objects
     */
    SofaStreamObserver<User> batchCreateUserClientStream(SofaStreamObserver<String> responseObserver);

    /**
     * Bidirectional Streaming: client sends users one by one, server echoes each back with
     * verification result appended to the name.
     *
     * @param responseObserver observer to receive each verified User
     * @return observer for the client to send User objects
     */
    SofaStreamObserver<User> verifyUserBiStream(SofaStreamObserver<User> responseObserver);

    /**
     * Unary (synchronous): get a single user by id.
     *
     * @param id user id
     * @return the User
     */
    User getUser(long id);
}

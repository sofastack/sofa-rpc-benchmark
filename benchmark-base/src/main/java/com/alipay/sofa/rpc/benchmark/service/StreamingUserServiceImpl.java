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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class StreamingUserServiceImpl implements StreamingUserService {

    private static final Logger         LOGGER      = LoggerFactory
                                                        .getLogger(StreamingUserServiceImpl.class);

    private final UserServiceServerImpl userService = new UserServiceServerImpl();

    private static final int            PAGE_SIZE   = 10;

    @Override
    public void listUserServerStream(int pageNo, SofaStreamObserver<User> responseObserver) {
        LOGGER.info("Server streaming: listUserServerStream, pageNo={}", pageNo);
        try {
            for (int i = 0; i < PAGE_SIZE; i++) {
                long userId = (long) pageNo * PAGE_SIZE + i;
                User user = userService.getUserById(userId, 1);
                responseObserver.onNext(user);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public SofaStreamObserver<User> batchCreateUserClientStream(
                                                                final SofaStreamObserver<String> responseObserver) {
        LOGGER.info("Client streaming: batchCreateUserClientStream");
        return new SofaStreamObserver<User>() {
            private final AtomicInteger count = new AtomicInteger(0);

            @Override
            public void onNext(User user) {
                if (user != null) {
                    count.incrementAndGet();
                    LOGGER.debug("Received user: id={}, name={}", user.getId(), user.getName());
                }
            }

            @Override
            public void onCompleted() {
                String summary = "Batch create completed, total users received: " + count.get();
                LOGGER.info(summary);
                responseObserver.onNext(summary);
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("Client stream error", throwable);
                responseObserver.onError(throwable);
            }
        };
    }

    @Override
    public SofaStreamObserver<User> verifyUserBiStream(
                                                       final SofaStreamObserver<User> responseObserver) {
        LOGGER.info("Bidirectional streaming: verifyUserBiStream");
        return new SofaStreamObserver<User>() {
            @Override
            public void onNext(User user) {
                if (user == null) {
                    return;
                }
                // Echo back user with verified mark appended to name
                user.setName(user.getName() + "[verified]");
                LOGGER.debug("Verified user: id={}, name={}", user.getId(), user.getName());
                responseObserver.onNext(user);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("BiStream: client completed sending");
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("BiStream error", throwable);
                responseObserver.onError(throwable);
            }
        };
    }

    @Override
    public User getUser(long id) {
        LOGGER.info("Unary: getUser, id={}", id);
        return userService.getUserById(id, 1);
    }
}

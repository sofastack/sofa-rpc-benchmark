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

import com.alipay.sofa.rpc.benchmark.proto.BatchCreateResponse;
import com.alipay.sofa.rpc.benchmark.proto.CreateUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.GetUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.GetUserResponse;
import com.alipay.sofa.rpc.benchmark.proto.ListUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.SofaUserServiceTriple;
import com.alipay.sofa.rpc.benchmark.proto.VerifyUserRequest;
import com.alipay.sofa.rpc.benchmark.proto.VerifyUserResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server-side implementation of the proto-defined UserService.
 *
 * Extends the SOFARPC-generated {@code SofaUserServiceTriple.UserServiceImplBase},
 * which implements {@code BindableService} so SOFARPC Triple registers it as a
 * native gRPC service automatically.
 */
public class UserServiceProtoImpl extends SofaUserServiceTriple.UserServiceImplBase {

    private static final Logger LOGGER    = LoggerFactory.getLogger(UserServiceProtoImpl.class);

    private static final int    PAGE_SIZE = 10;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        LOGGER.info("Unary: getUser id={}", request.getId());
        responseObserver.onNext(buildUserResponse(request.getId()));
        responseObserver.onCompleted();
    }

    @Override
    public void listUserServerStream(ListUserRequest request,
                                     StreamObserver<GetUserResponse> responseObserver) {
        LOGGER.info("Server streaming: listUserServerStream pageNo={}", request.getPageNo());
        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : PAGE_SIZE;
        try {
            for (int i = 0; i < pageSize; i++) {
                long id = (long) request.getPageNo() * pageSize + i;
                responseObserver.onNext(buildUserResponse(id));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public StreamObserver<CreateUserRequest> batchCreateUser(
                                                             StreamObserver<BatchCreateResponse> responseObserver) {
        LOGGER.info("Client streaming: batchCreateUser");
        return new StreamObserver<CreateUserRequest>() {
            private final AtomicInteger count = new AtomicInteger(0);

            @Override
            public void onNext(CreateUserRequest request) {
                LOGGER.debug("Received user: id={}, name={}", request.getId(), request.getName());
                count.incrementAndGet();
            }

            @Override
            public void onCompleted() {
                int total = count.get();
                LOGGER.info("Batch create completed, total={}", total);
                responseObserver.onNext(BatchCreateResponse.newBuilder()
                    .setCount(total)
                    .setMessage("Batch create completed, total users received: " + total)
                    .build());
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                LOGGER.error("Client stream error", t);
                responseObserver.onError(t);
            }
        };
    }

    @Override
    public StreamObserver<VerifyUserRequest> verifyUserBiStream(
                                                                StreamObserver<VerifyUserResponse> responseObserver) {
        LOGGER.info("Bidirectional streaming: verifyUserBiStream");
        return new StreamObserver<VerifyUserRequest>() {
            @Override
            public void onNext(VerifyUserRequest request) {
                LOGGER.debug("Verifying user: id={}, name={}", request.getId(), request.getName());
                responseObserver.onNext(VerifyUserResponse.newBuilder()
                    .setId(request.getId())
                    .setName(request.getName() + "[verified]")
                    .setVerified(true)
                    .build());
            }

            @Override
            public void onCompleted() {
                LOGGER.info("BiStream: client completed");
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                LOGGER.error("BiStream error", t);
                responseObserver.onError(t);
            }
        };
    }

    private GetUserResponse buildUserResponse(long id) {
        return GetUserResponse.newBuilder()
            .setId(id)
            .setName("Doug Lea")
            .setEmail("dong.lea@gmail.com")
            .setMobile("18612345678")
            .setAddress("北京市 中关村 中关村大街1号 鼎好大厦 1605")
            .setStatus(1)
            .build();
    }
}

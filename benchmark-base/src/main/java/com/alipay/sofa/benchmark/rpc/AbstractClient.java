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
package com.alipay.sofa.benchmark.rpc;

import com.alipay.sofa.benchmark.bean.Page;
import com.alipay.sofa.benchmark.bean.User;
import com.alipay.sofa.benchmark.service.UserService;
import com.alipay.sofa.benchmark.service.UserServiceServerImpl;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractClient {
    private final AtomicInteger counter             = new AtomicInteger(0);
    private final UserService   _serviceUserService = new UserServiceServerImpl();

    protected abstract UserService getUserService();

    public boolean existUser() throws Exception {
        String email = String.valueOf(counter.getAndIncrement());
        return getUserService().existUser(email);
    }

    public boolean createUser() throws Exception {
        int id = counter.getAndIncrement();
        User user = _serviceUserService.getUser(id);
        return getUserService().createUser(user);
    }

    public User getUser() throws Exception {
        int id = counter.getAndIncrement();
        return getUserService().getUser(id);
    }

    public Page<User> listUser() throws Exception {
        int pageNo = counter.getAndIncrement();
        return getUserService().listUser(pageNo);
    }

    public User verifyUser() throws Exception {
        int id = counter.getAndIncrement();
        User user = _serviceUserService.getUser(id);
        return getUserService().verifyUser(user);
    }

}

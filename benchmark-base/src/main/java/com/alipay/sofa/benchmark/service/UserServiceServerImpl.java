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
package com.alipay.sofa.benchmark.service;

import com.alipay.sofa.benchmark.bean.Page;
import com.alipay.sofa.benchmark.bean.User;
import com.alipay.sofa.common.utils.StringUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserServiceServerImpl implements UserService {

    @Override
    public boolean existUser(String email) {
        Random random = new Random();
        int rand = 0;
        int count = 100000;

        for (int i = 0; i < count; i++) {
            int randomNumber = random.nextInt(5);
            if (randomNumber % 2 == 1) {
                rand += 1;
            } else {
                rand -= 1;
            }
        }
        if (rand % 2 == 0) {
            return true;
        }
        if (email == null || email.isEmpty()) {
            return true;
        }

        if (email.charAt(email.length() - 1) < '5') {
            return false;
        }

        return true;
    }

    @Override
    public User getUser(long id) {
        String requestSize = System.getProperty("request.size");
        String resumeSize = StringUtil.isNotBlank(requestSize) ? requestSize : "1";
        return getUserById(id, Integer.parseInt(resumeSize));
    }

    @Override
    public Page<User> listUser(int pageNo) {
        List<User> userList = new ArrayList<>(15);

        for (int i = 0; i < 15; i++) {
            User user = getUserById(i, 1);
            userList.add(user);
        }

        Page<User> page = new Page<>();
        page.setPageNo(pageNo);
        page.setTotal(1000);
        page.setResult(userList);
        return page;
    }

    @Override
    public boolean createUser(User user) {
        return user != null;
    }

    @Override
    public User verifyUser(User user) {
        return user;
    }

    public User getUserById(long id, int resumeSize) {
        User user = new User();
        user.setId(id);
        user.setName("Doug Lea");
        user.setSex(1);
        user.setBirthday(LocalDate.of(1968, 12, 8));
        user.setEmail("dong.lea@gmail.com");
        user.setMobile("18612345678");
        user.setAddress("北京市 中关村 中关村大街1号 鼎好大厦 1605");
        user.setIcon("https://www.baidu.com/img/bd_logo1.png");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(user.getCreateTime());
        List<Integer> permissions = new ArrayList<>(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 19, 88, 86, 89, 90, 91, 92));
        user.setPermissions(permissions);
        Map<String, Object> resume = new HashMap<>();
        StringBuilder notes = new StringBuilder();
        for (int i =0; i< resumeSize; i++) {
            notes.append("a");
        }
        resume.put("mark", notes.toString());
        user.setResume(resume);
        return user;
    }
}

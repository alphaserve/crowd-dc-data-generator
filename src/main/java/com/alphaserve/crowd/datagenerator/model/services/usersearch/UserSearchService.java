package com.alphaserve.crowd.datagenerator.model.services.usersearch;

import com.atlassian.crowd.model.user.User;

import java.util.List;

public interface UserSearchService {

    List<User> findUsers(String nameOrEmail);

    List<User> findUsers();
}

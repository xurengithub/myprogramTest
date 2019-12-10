package com.xuren.service;

import com.xuren.pojo.Users;

public interface UserService {

    public boolean queryUsernameIsExist(String userName);
    public void saveUser(Users user);
    public Users queryUserForLogin(String userName, String password);

}

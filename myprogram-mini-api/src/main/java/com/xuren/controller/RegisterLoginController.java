package com.xuren.controller;

import com.xuren.service.UserService;
import com.xuren.pojo.Users;
import com.xuren.utils.IMoocJSONResult;
import com.xuren.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "用户注册登录接口", tags = {"注册和登录的controller"})
public class RegisterLoginController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("regist")
    public IMoocJSONResult regist(@RequestBody Users user) throws Exception {

        if(StringUtils.isBlank(user.getUsername()) && StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }

        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        if(usernameIsExist) {
            return IMoocJSONResult.errorMsg("用户名已存在");
        }

        user.setNickname(user.getUsername());
        user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        user.setFansCounts(0);
        user.setFollowCounts(0);
        user.setReceiveLikeCounts(0);
        userService.saveUser(user);

        return IMoocJSONResult.ok();
    }
}

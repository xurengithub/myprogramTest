package com.xuren.controller;

import com.xuren.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    public static final String FILE_SPACE = "C:/myprogram_dev/video";

    public static final String FFMPEG_EXE = "";
}

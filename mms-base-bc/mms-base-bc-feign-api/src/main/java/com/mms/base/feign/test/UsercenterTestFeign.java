package com.mms.base.feign.test;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "usercenter", path = "/usercenter/test")
public interface UsercenterTestFeign {

    @GetMapping("/1")
    String test1();
}



package com.mms.test.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "base", path = "/base/test")
public interface BaseTestFeign {

    @GetMapping("/1")
    String test1();
}



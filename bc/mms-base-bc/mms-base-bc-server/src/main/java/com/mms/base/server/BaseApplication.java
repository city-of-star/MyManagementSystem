package com.mms.base.server;

import com.mms.common.core.constants.scan.FeignScanConstant;
import com.mms.common.core.constants.scan.MapperScanConstant;
import com.mms.common.core.constants.scan.PackageScanConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@MapperScan(MapperScanConstant.BASE_MAPPER_SCAN)
@EnableFeignClients(basePackages = {FeignScanConstant.BASE_FEIGN_SCAN})
@SpringBootApplication(scanBasePackages = {PackageScanConstant.BASE_PACKAGE_SCAN, PackageScanConstant.COMMON_PACKAGE_SCAN})
public class BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
    }
}
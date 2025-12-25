package com.mms.gateway;

import com.mms.common.core.constants.scan.PackageScanConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {PackageScanConstant.GATEWAY_PACKAGE_SCAN, PackageScanConstant.COMMON_PACKAGE_SCAN})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.mms.gateway.GatewayApplication.class, args);
    }
}


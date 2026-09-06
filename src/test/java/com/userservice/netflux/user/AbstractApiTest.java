package com.userservice.netflux.user;

import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
public abstract class AbstractApiTest {

    @DynamicPropertySource
    static void registerRsaPrivateKey(DynamicPropertyRegistry registry) {
        RsaTestKeySupport.registerRsaPrivateKey(registry);
    }
}

package com.userservice.netflux.user.config;

import java.security.interfaces.RSAPrivateKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(RSAPrivateKey privateKey) {
}

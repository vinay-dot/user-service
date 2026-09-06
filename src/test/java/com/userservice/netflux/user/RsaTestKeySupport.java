package com.userservice.netflux.user;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.util.Base64;

import org.springframework.test.context.DynamicPropertyRegistry;

final class RsaTestKeySupport {

    private RsaTestKeySupport() {
    }

    static void registerRsaPrivateKey(DynamicPropertyRegistry registry) {
        var keyLocation = generateAndWriteTestKey();
        registry.add("rsa.private-key", () -> keyLocation);
    }

    static void registerRsaPrivateKeySystemProperty() {
        System.setProperty("rsa.private-key", generateAndWriteTestKey());
    }

    private static String generateAndWriteTestKey() {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            var keyPair = keyPairGenerator.generateKeyPair();
            var pem = "-----BEGIN PRIVATE KEY-----\n"
                    + Base64.getMimeEncoder(64, "\n".getBytes())
                            .encodeToString(keyPair.getPrivate().getEncoded())
                    + "\n-----END PRIVATE KEY-----\n";
            var keyPath = Path.of("target", "test-rsa-private-key.pem");
            Files.writeString(keyPath, pem);
            return keyPath.toUri().toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate test RSA key", ex);
        }
    }
}

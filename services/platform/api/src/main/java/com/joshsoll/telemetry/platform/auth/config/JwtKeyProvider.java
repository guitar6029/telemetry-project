package com.joshsoll.telemetry.platform.auth.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class JwtKeyProvider {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @PostConstruct
    public void init() {
        // load the PEM files here
        privateKey = loadPrivateKey(privateKeyPath);
        publicKey = loadPublicKey(publicKeyPath);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private PrivateKey loadPrivateKey(String path) {
        try {
            String pem = Files.readString(Path.of(path));
            // remove the PEM wrapper
            pem = cleanPem(pem, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

            // decode base64
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            // generate private key from bytes
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePrivate(spec);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key from path: " + path, e);
        }
    }

    private PublicKey loadPublicKey(String path) {
        try {

            String pem = Files.readString(Path.of(path));
            pem = cleanPem(pem, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");

            byte[] keyBytes = Base64.getDecoder().decode(pem);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key from path: " + path, e);
        }
    }

    private String cleanPem(
            String pem,
            String begin,
            String end) {

        return pem.replace(begin, "")
                .replace(end, "")
                .replaceAll("\\s+", "");

    }
}

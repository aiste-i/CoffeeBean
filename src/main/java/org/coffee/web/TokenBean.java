package org.coffee.web;

import lombok.NoArgsConstructor;

import javax.enterprise.context.RequestScoped;
import java.security.SecureRandom;
import java.util.Base64;

@RequestScoped
@NoArgsConstructor
public class TokenBean {

    private final SecureRandom secureRandom = new SecureRandom();

    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}

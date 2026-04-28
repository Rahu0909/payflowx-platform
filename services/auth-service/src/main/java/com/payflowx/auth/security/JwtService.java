package com.payflowx.auth.security;

import com.payflowx.auth.config.JwtProperties;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.security.provider.TokenProvider;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final TokenProvider activeProvider;

    public JwtService(
            JwtProperties properties,
            java.util.List<TokenProvider> providers
    ) {
        Map<String, TokenProvider> providerMap =
                providers.stream()
                        .collect(Collectors.toMap(
                                p -> p.algorithm().toUpperCase(),
                                Function.identity()
                        ));

        this.activeProvider =
                providerMap.get(
                        properties.algorithm().toUpperCase()
                );

        if (this.activeProvider == null) {
            throw new IllegalStateException("Unsupported JWT algorithm");
        }
    }

    public String generateToken(User user) {
        return activeProvider.generateToken(user);
    }
}
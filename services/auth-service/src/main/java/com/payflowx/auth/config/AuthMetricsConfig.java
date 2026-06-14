package com.payflowx.auth.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthMetricsConfig {

    @Bean
    public Counter authRegisterCounter(MeterRegistry registry) {
        return Counter.builder("auth_register_total")
                .description("Total user registrations")
                .register(registry);
    }

    @Bean
    public Counter authLoginSuccessCounter(MeterRegistry registry) {
        return Counter.builder("auth_login_success_total")
                .description("Successful logins")
                .register(registry);
    }

    @Bean
    public Counter authLoginFailedCounter(MeterRegistry registry) {
        return Counter.builder("auth_login_failed_total")
                .description("Failed logins")
                .register(registry);
    }

    @Bean
    public Counter authTokenRefreshCounter(MeterRegistry registry) {
        return Counter.builder("auth_token_refresh_total")
                .description("Refresh token requests")
                .register(registry);
    }

    @Bean
    public Counter authLogoutCounter(MeterRegistry registry) {
        return Counter.builder("auth_logout_total")
                .description("Logout requests")
                .register(registry);
    }
}
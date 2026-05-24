package com.payflowx.auth.config;

public final class AuthNotificationRabbitMqConstants {

    private AuthNotificationRabbitMqConstants() {
    }

    public static final String NOTIFICATION_EXCHANGE = "payflowx.notification.exchange";

    public static final String SOURCE_SERVICE = "auth-service";

    /*
     * USER EVENTS
     */

    public static final String USER_REGISTERED_ROUTING_KEY = "auth.user.registered";

    public static final String LOGIN_SUCCESS_ROUTING_KEY = "auth.login.success";

    public static final String USER_LOGOUT_ROUTING_KEY = "auth.user.logout";

    /*
     * TOKEN EVENTS
     */

    public static final String REFRESH_TOKEN_ROTATED_ROUTING_KEY = "auth.refresh.rotated";

    /*
     * PASSWORD EVENTS
     */

    public static final String PASSWORD_CHANGED_ROUTING_KEY = "auth.password.changed";

    public static final String PASSWORD_RESET_REQUESTED_ROUTING_KEY = "auth.password.reset.requested";

    public static final String PASSWORD_RESET_SUCCESS_ROUTING_KEY = "auth.password.reset.success";

    /*
     * EMAIL EVENTS
     */

    public static final String EMAIL_VERIFICATION_REQUESTED_ROUTING_KEY = "auth.email.verification.requested";

    public static final String EMAIL_VERIFIED_ROUTING_KEY = "auth.email.verified";

    /*
     * SECURITY EVENTS
     */

    public static final String ACCOUNT_LOCKED_ROUTING_KEY = "auth.account.locked";
}
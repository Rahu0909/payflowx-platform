package com.payflowx.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public RabbitMqConfig() {
    }

    /*
     EXCHANGE
     */
    public static final String NOTIFICATION_EXCHANGE = "payflowx.notification.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "payflowx.notification.dlx";

    /*
     ROUTING KEYS
     */
    public static final String MERCHANT_KYC_APPROVED_ROUTING_KEY = "merchant.kyc.approved";
    public static final String MERCHANT_KYC_REJECTED_ROUTING_KEY = "merchant.kyc.rejected";

    /*
     APPROVED QUEUES
     */
    public static final String MERCHANT_KYC_APPROVED_QUEUE = "merchant.kyc.approved.queue";
    public static final String MERCHANT_KYC_APPROVED_RETRY_QUEUE = "merchant.kyc.approved.retry.queue";
    public static final String MERCHANT_KYC_APPROVED_DLQ = "merchant.kyc.approved.dlq";

    /*
     REJECTED QUEUES
     */
    public static final String MERCHANT_KYC_REJECTED_QUEUE = "merchant.kyc.rejected.queue";
    public static final String MERCHANT_KYC_REJECTED_RETRY_QUEUE = "merchant.kyc.rejected.retry.queue";
    public static final String MERCHANT_KYC_REJECTED_DLQ = "merchant.kyc.rejected.dlq";


    public static final String PAYMENT_NOTIFICATION_QUEUE = "payment.notification.queue";

    public static final String PAYMENT_CREATED_ROUTING_KEY = "payment.created";
    public static final String PAYMENT_PROCESSING_ROUTING_KEY = "payment.processing";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";
    public static final String REFUND_CREATED_ROUTING_KEY = "refund.created";
    public static final String REFUND_SUCCESS_ROUTING_KEY = "refund.success";
    public static final String REFUND_FAILED_ROUTING_KEY = "refund.failed";
    public static final String SETTLEMENT_CREATED_ROUTING_KEY = "settlement.created";
    public static final String SETTLEMENT_COMPLETED_ROUTING_KEY = "settlement.completed";
    public static final String SETTLEMENT_FAILED_ROUTING_KEY = "settlement.failed";
    public static final String PAYOUT_CREATED_ROUTING_KEY = "payout.created";
    public static final String PAYOUT_SUCCESS_ROUTING_KEY = "payout.success";
    public static final String PAYOUT_FAILED_ROUTING_KEY = "payout.failed";
    public static final String PAYOUT_REVERSED_ROUTING_KEY = "payout.reversed";
    public static final String TREASURY_NOTIFICATION_QUEUE = "treasury.notification.queue";

    /*
 AUTH ROUTING KEYS
 */
    public static final String USER_REGISTERED_ROUTING_KEY = "auth.user.registered";
    public static final String LOGIN_SUCCESS_ROUTING_KEY = "auth.login.success";
    public static final String PASSWORD_RESET_REQUESTED_ROUTING_KEY = "auth.password.reset.requested";
    public static final String PASSWORD_RESET_SUCCESS_ROUTING_KEY = "auth.password.reset.success";
    public static final String USER_LOGOUT_ROUTING_KEY = "auth.user.logout";
    public static final String REFRESH_TOKEN_ROTATED_ROUTING_KEY = "auth.refresh.token.rotated";

    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_KYC_SUBMITTED_ROUTING_KEY = "user.kyc.submitted";
    public static final String USER_KYC_APPROVED_ROUTING_KEY = "user.kyc.approved";
    public static final String USER_KYC_REJECTED_ROUTING_KEY = "user.kyc.rejected";
    public static final String USER_ACCOUNT_BLOCKED_ROUTING_KEY = "user.account.blocked";
    public static final String USER_ACCOUNT_SUSPENDED_ROUTING_KEY = "user.account.suspended";

    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_CANCELLED_ROUTING_KEY = "order.cancelled";
    public static final String ORDER_PAID_ROUTING_KEY = "order.paid";
    public static final String ORDER_EXPIRED_ROUTING_KEY = "order.expired";
    /*
     AUTH QUEUE
     */
    public static final String AUTH_NOTIFICATION_QUEUE = "auth.notification.queue";
    public static final String USER_NOTIFICATION_QUEUE = "user.notification.queue";
    public static final String ORDER_NOTIFICATION_QUEUE = "order.notification.queue";

    @Bean
    public Queue userNotificationQueue() {
        return QueueBuilder.durable(USER_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue orderNotificationQueue() {
        return QueueBuilder.durable(ORDER_NOTIFICATION_QUEUE).build();
    }

    /*
     EXCHANGES
     */
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    /*
     APPROVED MAIN QUEUE
     */
    @Bean
    public Queue merchantKycApprovedQueue() {
        return QueueBuilder.durable(MERCHANT_KYC_APPROVED_QUEUE).withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE).withArgument("x-dead-letter-routing-key", MERCHANT_KYC_APPROVED_DLQ).build();
    }

    /*
     APPROVED RETRY QUEUE
     */
    @Bean
    public Queue merchantKycApprovedRetryQueue() {
        return QueueBuilder.durable(MERCHANT_KYC_APPROVED_RETRY_QUEUE).withArgument("x-message-ttl", 30000).withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE).withArgument("x-dead-letter-routing-key", MERCHANT_KYC_APPROVED_ROUTING_KEY).build();
    }

    /*
     APPROVED DLQ
     */
    @Bean
    public Queue merchantKycApprovedDlq() {
        return QueueBuilder.durable(MERCHANT_KYC_APPROVED_DLQ).build();
    }

    /*
     REJECTED MAIN QUEUE
     */
    @Bean
    public Queue merchantKycRejectedQueue() {
        return QueueBuilder.durable(MERCHANT_KYC_REJECTED_QUEUE).withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE).withArgument("x-dead-letter-routing-key", MERCHANT_KYC_REJECTED_DLQ).build();
    }

    /*
     REJECTED RETRY QUEUE
     */
    @Bean
    public Queue merchantKycRejectedRetryQueue() {
        return QueueBuilder.durable(MERCHANT_KYC_REJECTED_RETRY_QUEUE).withArgument("x-message-ttl", 30000).withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE).withArgument("x-dead-letter-routing-key", MERCHANT_KYC_REJECTED_ROUTING_KEY).build();
    }

    /*
     REJECTED DLQ
     */
    @Bean
    public Queue merchantKycRejectedDlq() {
        return QueueBuilder.durable(MERCHANT_KYC_REJECTED_DLQ).build();
    }

    /*
     BINDINGS
     */
    @Bean
    public Binding merchantKycApprovedBinding() {
        return BindingBuilder.bind(merchantKycApprovedQueue()).to(notificationExchange()).with(MERCHANT_KYC_APPROVED_ROUTING_KEY);
    }

    @Bean
    public Binding merchantKycRejectedBinding() {
        return BindingBuilder.bind(merchantKycRejectedQueue()).to(notificationExchange()).with(MERCHANT_KYC_REJECTED_ROUTING_KEY);
    }

    /*
     DLQ BINDINGS
     */
    @Bean
    public Binding merchantKycApprovedDlqBinding() {
        return BindingBuilder.bind(merchantKycApprovedDlq()).to(deadLetterExchange()).with(MERCHANT_KYC_APPROVED_DLQ);
    }

    @Bean
    public Binding merchantKycRejectedDlqBinding() {
        return BindingBuilder.bind(merchantKycRejectedDlq()).to(deadLetterExchange()).with(MERCHANT_KYC_REJECTED_DLQ);
    }

    @Bean
    public Queue paymentNotificationQueue() {
        return new Queue(PAYMENT_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding paymentCreatedBinding() {
        return BindingBuilder.bind(paymentNotificationQueue()).to(notificationExchange()).with(PAYMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentProcessingBinding() {
        return BindingBuilder.bind(paymentNotificationQueue()).to(notificationExchange()).with(PAYMENT_PROCESSING_ROUTING_KEY);
    }

    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder.bind(paymentNotificationQueue()).to(notificationExchange()).with(PAYMENT_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(paymentNotificationQueue()).to(notificationExchange()).with(PAYMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding refundCreatedBinding() {
        return BindingBuilder.bind(paymentNotificationQueue()).to(notificationExchange()).with(REFUND_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding refundSuccessBinding() {
        return BindingBuilder.bind(paymentNotificationQueue()).to(notificationExchange()).with(REFUND_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding refundFailedBinding() {
        return BindingBuilder.bind(paymentNotificationQueue()).to(notificationExchange()).with(REFUND_FAILED_ROUTING_KEY);
    }

    @Bean
    public Queue treasuryNotificationQueue() {
        return QueueBuilder.durable(TREASURY_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding settlementCreatedBinding() {
        return BindingBuilder.bind(treasuryNotificationQueue()).to(notificationExchange()).with(SETTLEMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding settlementCompletedBinding() {
        return BindingBuilder.bind(treasuryNotificationQueue()).to(notificationExchange()).with(SETTLEMENT_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding settlementFailedBinding() {
        return BindingBuilder.bind(treasuryNotificationQueue()).to(notificationExchange()).with(SETTLEMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding payoutCreatedBinding() {
        return BindingBuilder.bind(treasuryNotificationQueue()).to(notificationExchange()).with(PAYOUT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding payoutSuccessBinding() {
        return BindingBuilder.bind(treasuryNotificationQueue()).to(notificationExchange()).with(PAYOUT_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding payoutFailedBinding() {
        return BindingBuilder.bind(treasuryNotificationQueue()).to(notificationExchange()).with(PAYOUT_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding payoutReversedBinding() {
        return BindingBuilder.bind(treasuryNotificationQueue()).to(notificationExchange()).with(PAYOUT_REVERSED_ROUTING_KEY);
    }

    @Bean
    public Queue authNotificationQueue() {
        return QueueBuilder.durable(AUTH_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder.bind(authNotificationQueue()).to(notificationExchange()).with(USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Binding loginSuccessBinding() {
        return BindingBuilder.bind(authNotificationQueue()).to(notificationExchange()).with(LOGIN_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetRequestedBinding() {
        return BindingBuilder.bind(authNotificationQueue()).to(notificationExchange()).with(PASSWORD_RESET_REQUESTED_ROUTING_KEY);
    }

    @Bean
    public Binding passwordResetSuccessBinding() {
        return BindingBuilder.bind(authNotificationQueue()).to(notificationExchange()).with(PASSWORD_RESET_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding userLogoutBinding() {
        return BindingBuilder.bind(authNotificationQueue()).to(notificationExchange()).with(USER_LOGOUT_ROUTING_KEY);
    }

    @Bean
    public Binding refreshTokenRotatedBinding() {
        return BindingBuilder.bind(authNotificationQueue()).to(notificationExchange()).with(REFRESH_TOKEN_ROTATED_ROUTING_KEY);
    }

    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userNotificationQueue()).to(notificationExchange()).with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding userKycSubmittedBinding() {
        return BindingBuilder.bind(userNotificationQueue()).to(notificationExchange()).with(USER_KYC_SUBMITTED_ROUTING_KEY);
    }

    @Bean
    public Binding userKycApprovedBinding() {
        return BindingBuilder.bind(userNotificationQueue()).to(notificationExchange()).with(USER_KYC_APPROVED_ROUTING_KEY);
    }

    @Bean
    public Binding userKycRejectedBinding() {
        return BindingBuilder.bind(userNotificationQueue()).to(notificationExchange()).with(USER_KYC_REJECTED_ROUTING_KEY);
    }

    @Bean
    public Binding userAccountBlockedBinding() {
        return BindingBuilder.bind(userNotificationQueue()).to(notificationExchange()).with(USER_ACCOUNT_BLOCKED_ROUTING_KEY);
    }

    @Bean
    public Binding userAccountSuspendedBinding() {
        return BindingBuilder.bind(userNotificationQueue()).to(notificationExchange()).with(USER_ACCOUNT_SUSPENDED_ROUTING_KEY);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderNotificationQueue()).to(notificationExchange()).with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderCancelledBinding() {
        return BindingBuilder.bind(orderNotificationQueue()).to(notificationExchange()).with(ORDER_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public Binding orderPaidBinding() {
        return BindingBuilder.bind(orderNotificationQueue()).to(notificationExchange()).with(ORDER_PAID_ROUTING_KEY);
    }

    @Bean
    public Binding orderExpiredBinding() {
        return BindingBuilder.bind(orderNotificationQueue()).to(notificationExchange()).with(ORDER_EXPIRED_ROUTING_KEY);
    }
}
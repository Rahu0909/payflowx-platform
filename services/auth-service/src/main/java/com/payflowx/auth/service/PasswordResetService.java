package com.payflowx.auth.service;

import com.payflowx.auth.dto.ForgotPasswordRequest;
import com.payflowx.auth.dto.ResetPasswordRequest;

public interface PasswordResetService {

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
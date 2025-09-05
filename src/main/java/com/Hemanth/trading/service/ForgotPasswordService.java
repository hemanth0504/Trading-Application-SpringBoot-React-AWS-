package com.Hemanth.trading.service;

import com.Hemanth.trading.domain.VerificationType;
import com.Hemanth.trading.model.ForgotPasswordToken;
import com.Hemanth.trading.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp,
                                    VerificationType verificationType,String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);

    boolean verifyToken(ForgotPasswordToken token,String otp);
}

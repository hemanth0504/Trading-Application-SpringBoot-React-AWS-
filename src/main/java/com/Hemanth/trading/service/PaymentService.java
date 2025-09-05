package com.Hemanth.trading.service;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.Hemanth.trading.domain.PaymentMethod;
import com.Hemanth.trading.model.PaymentOrder;
import com.Hemanth.trading.model.User;
import com.Hemanth.trading.response.PaymentResponse;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean ProceedPaymentOrder (PaymentOrder paymentOrder,
                                 String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLink(User user,
                                              Long Amount,
                                              Long orderId) throws RazorpayException;

    PaymentResponse createStripePaymentLink(User user, Long Amount,
                                            Long orderId) throws StripeException;
}

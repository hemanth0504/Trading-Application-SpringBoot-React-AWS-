package com.Hemanth.trading.service;

import com.Hemanth.trading.model.PaymentDetails;
import com.Hemanth.trading.model.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails( String accountNumber,
                                             String accountHolderName,
                                             String ifsc,
                                             String bankName,
                                             User user
    );

    public PaymentDetails getUsersPaymentDetails(User user);


}

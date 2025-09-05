package com.Hemanth.trading.service;

import com.Hemanth.trading.domain.WalletTransactionType;
import com.Hemanth.trading.model.Wallet;
import com.Hemanth.trading.model.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {
    WalletTransaction createTransaction(Wallet wallet,
                                        WalletTransactionType type,
                                        String transferId,
                                        String purpose,
                                        Long amount
    );

    List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type);

}

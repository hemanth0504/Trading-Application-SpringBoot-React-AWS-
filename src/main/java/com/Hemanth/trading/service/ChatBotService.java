package com.Hemanth.trading.service;

import com.Hemanth.trading.model.CoinDTO;
import com.Hemanth.trading.response.ApiResponse;

public interface ChatBotService {
    ApiResponse getCoinDetails(String coinName);

    CoinDTO getCoinByName(String coinName);

    String simpleChat(String prompt);
}

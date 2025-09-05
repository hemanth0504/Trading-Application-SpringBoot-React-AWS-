package com.Hemanth.trading.service;

import com.Hemanth.trading.model.Coin;
import com.Hemanth.trading.model.User;
import com.Hemanth.trading.model.Watchlist;

public interface WatchlistService {

    Watchlist findUserWatchlist(Long userId) throws Exception;

    Watchlist createWatchList(User user);

    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin,User user) throws Exception;
}

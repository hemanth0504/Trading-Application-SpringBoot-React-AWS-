package com.Hemanth.trading.repository;


import com.Hemanth.trading.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Make User Database changes using this repository

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

}

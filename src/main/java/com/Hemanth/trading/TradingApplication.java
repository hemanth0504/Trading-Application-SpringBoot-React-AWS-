package com.Hemanth.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingApplication.class, args);
	}
		public void run(){
			System.out.println("CI/CD Github Actions are set up successfully  !!");
		}
}

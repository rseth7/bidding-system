package com.cars24.biddingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class BiddingSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(BiddingSystemApplication.class, args);
	}
}

package com.test.resize_image;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	private final AtomicLong counter = new AtomicLong();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// @Bean
	// CommandLineRunner init(ImageRepository imageRepository) {
	// return args -> {
	// BigInteger bigInteger = new BigInteger("28477493201717843167638773405");
	// Image i1 = imageRepository.findById(bigInteger);
	// System.out.println(i1);
	// };
	// }
}

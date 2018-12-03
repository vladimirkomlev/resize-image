package com.test.resize_image;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.test.resize_image.property.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
public class Application {
    private final AtomicLong counter = new AtomicLong();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // @Bean
    // CommandLineRunner init(ImageRepository imageRepository) {
    // return args -> {
    // Image i1 = new Image("stuff", "SMALL", 8, ".jpg");
    // System.out.println(i1);
    // i1 = imageRepository.save(i1);
    // System.out.println(i1);
    // };
    // }
}

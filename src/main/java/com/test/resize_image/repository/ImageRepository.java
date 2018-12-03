package com.test.resize_image.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.test.resize_image.model.Image;

public interface ImageRepository extends MongoRepository<Image, String> {
    Image findById(String id);

    void deleteById(String id);
}

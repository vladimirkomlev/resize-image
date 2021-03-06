package com.test.resize_image.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.test.resize_image.model.Image;

public interface ImageRepository extends MongoRepository<Image, String> {
    Image findById(String id);

    void deleteById(String id);

    Image findByIndexAndSize(long index, String size);

    List<Image> findByIndex(long index);
}

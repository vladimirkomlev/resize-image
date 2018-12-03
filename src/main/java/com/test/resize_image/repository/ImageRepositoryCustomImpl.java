package com.test.resize_image.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.test.resize_image.model.Image;

@Repository
public class ImageRepositoryCustomImpl implements ImageRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long findBiggestIndexOfImage() {
        Query query = new Query();
        query.limit(5);
        query.with(new Sort(Sort.Direction.DESC, "index"));

        List<Image> imageList = mongoTemplate.find(query, Image.class);
        System.out.println();

        return 0;
    }

}

package com.test.resize_image.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.test.resize_image.model.Image;
import com.test.resize_image.repository.ImageRepository;
import com.test.resize_image.repository.ImageRepositoryCustom;
import com.test.resize_image.service.FileStorageService;

@RestController
@RequestMapping("/image-storage")
public class ImageHandlingController {

    private static final String JPG = "jpg";
    private static final String JPEG = "jpeg";
    private static final String PNG = "png";
    private static final long MAX_BYTES_SIZE_OF_FILE = 5000000L;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageRepositoryCustom imageRepositoryCustom;

    @Autowired
    private FileStorageService fileStorageService;

    @RequestMapping(value = "/resize", method = RequestMethod.POST)
    public String resizeImage() {
        imageRepositoryCustom.findBiggestIndexOfImage();
        return "Everything OK!";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Image> getImageById(@PathVariable("id") String id) {
        Image image = imageRepository.findById(id);
        if (image == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(image);
        } else {
            return ResponseEntity.ok().body(image);
        }

    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleImageUpload(MultipartFile[] files, String callbackUrl) {
        for (MultipartFile file : files) {
            int index = file.getOriginalFilename().lastIndexOf(".") + 1;
            String fileExpansion = file.getOriginalFilename().substring(index, file.getOriginalFilename().length());
            if (!fileExpansion.equals(JPEG) && !fileExpansion.equals(JPG) && !fileExpansion.equals(PNG)) {
                return "Improper extension of image " + file.getOriginalFilename();
            }
            if (file.getSize() > MAX_BYTES_SIZE_OF_FILE) {
                return String.format("Size of %s more than 5MB", file.getOriginalFilename());
            }
        }
        Arrays.asList(files).stream().forEach(file -> {
            System.out.println(String.format("name: %s", file.getName()));
            System.out.println(String.format("OriginalFilename: %s", file.getOriginalFilename()));
            System.out.println(String.format("ContentType: %s", file.getContentType()));
            System.out.println(String.format("size: %d", file.getSize()));
            int index = file.getOriginalFilename().lastIndexOf(".") + 1;
            String fileExpansion = file.getOriginalFilename().substring(index, file.getOriginalFilename().length());
            System.out.println(String.format("callbackUrl: %s", callbackUrl));
            fileStorageService.storeFile(file);
        });

        return "";
    }
}

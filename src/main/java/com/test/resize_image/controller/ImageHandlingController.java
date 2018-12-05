package com.test.resize_image.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.test.resize_image.exception.FileExpansionException;
import com.test.resize_image.exception.FileSizeException;
import com.test.resize_image.exception.MyFileNotFoundException;
import com.test.resize_image.model.Image;
import com.test.resize_image.model.UploadImageResponse;
import com.test.resize_image.repository.ImageRepository;
import com.test.resize_image.repository.ImageRepositoryCustom;
import com.test.resize_image.service.ImageStorageService;
import com.test.resize_image.service.ResizeImageService;

@RestController
@RequestMapping("/image-storage")
public class ImageHandlingController {

    private static final String JPG = "jpg";
    private static final String JPEG = "jpeg";
    private static final String PNG = "png";
    private static final long MAX_BYTES_SIZE_OF_FILE = 5000000L;
    private static final String SMALL = "small";
    private static final String MEDIUM = "medium";
    private static final String BIG = "big";
    private static final String POINT_OF_PATH = ".";
    private static final int SMALL_WIDTH = 400;
    private static final int SMALL_HEIGHT = 300;
    private static final int MEDIUM_WIDTH = 900;
    private static final int MEDIUM_HEIGHT = 600;
    private static final int BIG_WIDTH = 1024;
    private static final int BIG_HEIGHT = 800;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageRepositoryCustom imageRepositoryCustom;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private ResizeImageService resizeImageService;

    @RequestMapping(value = "/download/{index}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadImage(@PathVariable(name = "index") long index,
            @RequestParam(name = "size") String size, HttpServletRequest request) {
        Image image = imageRepository.findByIndexAndSize(index, size.toLowerCase());

        Resource resource = null;
        try {
            resource = imageStorageService.loadImageAsResource(image.getName() + POINT_OF_PATH + image.getExpansion());
        } catch (Exception e) {
            throw new MyFileNotFoundException("File not found", e);
        }

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            throw new MyFileNotFoundException("Could not determine file type.");
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseStatus(org.springframework.http.HttpStatus.ACCEPTED)
    public List<UploadImageResponse> handleImageUpload(MultipartFile[] files, String callbackUrl,
            HttpServletRequest request) {
        List<UploadImageResponse> imageResponses = new ArrayList<>();
        for (MultipartFile file : files) {
            int index = file.getOriginalFilename().lastIndexOf(".") + 1;
            String fileExpansion = file.getOriginalFilename().substring(index, file.getOriginalFilename().length());
            if (!fileExpansion.equals(JPEG) && !fileExpansion.equals(JPG) && !fileExpansion.equals(PNG)) {
                throw new FileExpansionException(
                        String.format("Forbidden extension of image %s", file.getOriginalFilename()));
            }
            if (file.getSize() > MAX_BYTES_SIZE_OF_FILE) {
                throw new FileSizeException(String.format("Size of %s more than 5MB", file.getOriginalFilename()));
            }
        }
        StringBuilder firstPartOfUri = new StringBuilder();
        firstPartOfUri.append(request.getScheme()).append("://").append(request.getServerName()).append(":")
                .append(request.getServerPort()).append("/download/");
        Arrays.asList(files).stream().forEach(file -> {
            int index = file.getOriginalFilename().lastIndexOf(".") + 1;
            String fileExpansion = file.getOriginalFilename().substring(index, file.getOriginalFilename().length());
            String fileName = file.getOriginalFilename().substring(0, index - 1);
            resizeAndSaveImage(file, fileName, fileExpansion, firstPartOfUri, imageResponses);
        });

        return imageResponses;
    }

    @RequestMapping(value = "/delete/{index}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteImage(@PathVariable(name = "index") long index) {
        List<Image> imageList = imageRepository.findByIndex(index);
        for (Image image : imageList) {
            imageStorageService.deleteImage(image.getName() + POINT_OF_PATH + image.getExpansion());
            imageRepository.deleteById(image.getId());
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Images deleted");
    }

    private void resizeAndSaveImage(MultipartFile file, String fileName, String fileExpansion,
            StringBuilder firstPartOfUri, List<UploadImageResponse> imageResponses) {
        long indexOfImage = imageRepositoryCustom.findBiggestIndexOfImage();
        ++indexOfImage;
        resizeAndSaveImageWithParams(file, SMALL, fileName + SMALL, fileExpansion, firstPartOfUri, imageResponses,
                indexOfImage, SMALL_WIDTH, SMALL_HEIGHT);
        resizeAndSaveImageWithParams(file, MEDIUM, fileName + MEDIUM, fileExpansion, firstPartOfUri, imageResponses,
                indexOfImage, MEDIUM_WIDTH, MEDIUM_HEIGHT);
        resizeAndSaveImageWithParams(file, BIG, fileName + BIG, fileExpansion, firstPartOfUri, imageResponses,
                indexOfImage, BIG_WIDTH, BIG_HEIGHT);
    }

    private void resizeAndSaveImageWithParams(MultipartFile file, String typeSizeOfImage, String fileNameWithTypeSize,
            String fileExpansion, StringBuilder firstPartOfUri, List<UploadImageResponse> imageResponses,
            long indexOfImage, int width, int height) {
        BufferedImage bufferedImage = resizeImageService.resizeImage(file, width, height);
        imageStorageService.storeImage(fileNameWithTypeSize, fileExpansion, bufferedImage);
        Image image = new Image(fileNameWithTypeSize, typeSizeOfImage, indexOfImage, fileExpansion);
        image = imageRepository.save(image);
        StringBuilder secondPartOfUri = new StringBuilder().append(firstPartOfUri);
        secondPartOfUri.append(image.getIndex()).append("?size=").append(image.getSize());
        imageResponses.add(new UploadImageResponse(image.getName(), secondPartOfUri.toString(), image.getExpansion(),
                image.getSize()));
    }
}

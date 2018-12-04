package com.test.resize_image.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.test.resize_image.exception.FileExistsException;
import com.test.resize_image.exception.FileStorageException;
import com.test.resize_image.exception.MyFileNotFoundException;
import com.test.resize_image.property.FileStorageProperties;

@Service
public class ImageStorageService {

    private final Path fileStorageLocation;

    public ImageStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
                    e);
        }
    }

    public String storeImage(String imageName, String expansionImage, BufferedImage bufferedImage) {
        String fileName = StringUtils.cleanPath(imageName + "." + expansionImage);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            if (Files.exists(targetLocation))
                throw new FileExistsException(String.format("File %s already exists", fileName));

            ImageIO.write(bufferedImage, expansionImage, targetLocation.toFile());
            return fileName;
        } catch (IOException e) {
            throw new FileStorageException("Could not store image " + fileName, e);
        }
    }

    public Resource loadImageAsResource(String fileNameWithExpansion) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileNameWithExpansion).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("Image not found " + fileNameWithExpansion);
            }
        } catch (MalformedURLException e) {
            throw new MyFileNotFoundException("Image not found " + fileNameWithExpansion, e);
        }
    }
}

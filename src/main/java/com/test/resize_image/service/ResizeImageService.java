package com.test.resize_image.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.test.resize_image.exception.MyFileNotFoundException;

@Service
public class ResizeImageService {

    public BufferedImage resizeImage(MultipartFile file, int width, int height) {
        BufferedImage originalBufferedImage = null;
        try {
            originalBufferedImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new MyFileNotFoundException("Image not found " + file.getOriginalFilename(), e);
        }
        BufferedImage resizeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizeImage.createGraphics();
        graphics.drawImage(originalBufferedImage, 0, 0, width, height, null);
        graphics.dispose();

        return resizeImage;
    }
}

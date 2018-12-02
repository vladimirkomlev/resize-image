package com.test.resize_image.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.test.resize_image.model.Image;
import com.test.resize_image.repository.ImageRepository;

@RestController
public class ImageHandlingController {

	@Autowired
	private ImageRepository imageRepository;

	@RequestMapping(value = "/resize", method = RequestMethod.POST)
	public String resizeImage() {
		return "Everything OK!";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Image getImageById(@PathVariable("id") String id) {
		Image image = imageRepository.findById(id);
		return image;
	}
}

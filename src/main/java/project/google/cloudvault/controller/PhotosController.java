package project.google.cloudvault.controller;

import org.springframework.web.bind.annotation.RestController;

import project.google.cloudvault.service.PhotosService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class PhotosController {
    @Autowired
    private PhotosService photos;

    @GetMapping("/api/photos")
    public String getPhotos(@AuthenticationPrincipal OAuth2User principal) {
        String photo = photos.getPhotos(principal);
        return photo;
    }

}

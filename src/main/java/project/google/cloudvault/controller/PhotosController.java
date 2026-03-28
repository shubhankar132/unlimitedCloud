package project.google.cloudvault.controller;

import org.springframework.web.bind.annotation.RestController;

import project.google.cloudvault.model.PhotoItem;
import project.google.cloudvault.service.PhotosService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class PhotosController {
    @Autowired
    private PhotosService photos;

    @GetMapping("/api/photos")
    public List<PhotoItem> getPhotos(@AuthenticationPrincipal OAuth2User principal) {
        List<PhotoItem> photo = photos.getPhotos(principal);
        return photo;
    }

}

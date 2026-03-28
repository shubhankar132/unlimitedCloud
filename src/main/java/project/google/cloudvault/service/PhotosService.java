package project.google.cloudvault.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import project.google.cloudvault.model.DriveFileResponse;
import project.google.cloudvault.model.PhotoItem;

@Service
public class PhotosService {
    @Autowired
    private OAuth2AuthorizedClientService clientService;
    @Autowired
    private RestTemplate template;

    public List<PhotoItem> getPhotos(OAuth2User principal) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                "google",
                principal.getName());// this method gets all the fields; think of it as "google+principle.getName" as
                                     // primary key
        String accessToken = client.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        // Creating the request header
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity <DriveFileResponse> response = template.exchange(
                "https://www.googleapis.com/drive/v3/files?q=mimeType+contains+'image/'&fields=files(id,name,mimeType,thumbnailLink,webViewLink,createdTime,imageMediaMetadata)",
                HttpMethod.GET,
                entity,
                DriveFileResponse.class);
                DriveFileResponse drivefileresponse = response.getBody();
        return drivefileresponse.getFiles();
    }
}

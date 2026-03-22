package project.google.cloudvault.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PhotosService {
    @Autowired
    private OAuth2AuthorizedClientService clientService;
    @Autowired
    private RestTemplate template;

    public String getPhotos(OAuth2User principal) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                "google",
                principal.getName());// this method gets all the fields; think of it as "google+principle.getName" as
                                     // primary key
        String accessToken = client.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        // Creating the request header
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = template.exchange(
                "https://photoslibrary.googleapis.com/v1/mediaItems", HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}

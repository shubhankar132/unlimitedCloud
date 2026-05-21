package project.google.cloudvault.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import project.google.cloudvault.model.ConnectedAccount;
import project.google.cloudvault.model.DriveFileResponse;
import project.google.cloudvault.model.PhotoItem;
import project.google.cloudvault.repository.ConnectedAccountRepository;

@Service
public class PhotosService {
    @Autowired
    private ConnectedAccountRepository connectedAccountRepository;
    @Autowired
    private OAuth2AuthorizedClientService clientService;
    @Autowired
    private RestTemplate template;
    @org.springframework.beans.factory.annotation.Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @org.springframework.beans.factory.annotation.Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

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
        ResponseEntity<DriveFileResponse> response = template.exchange(
                "https://www.googleapis.com/drive/v3/files?q=mimeType+contains+'image/'&fields=files(id,name,mimeType,thumbnailLink,webViewLink,createdTime,imageMediaMetadata)",
                HttpMethod.GET,
                entity,
                DriveFileResponse.class);
        DriveFileResponse drivefileresponse = response.getBody();
        List<PhotoItem> masterAccountFile = drivefileresponse.getFiles();

        List<ConnectedAccount> connected_account_list = new ArrayList<>();
        connected_account_list = connectedAccountRepository.findByMasterUserID_GoogleSubjectId(principal.getName());
        List<PhotoItem> all_photos = new ArrayList<>();
        all_photos.addAll(masterAccountFile);
        for (ConnectedAccount i : connected_account_list) {
            String current_Access_token = i.getAccessToken();
            if (i.getTokenExpiry().isBefore(Instant.now())) // checking if the token is expired for a connected account
                                                            // -- if not we lazy refresh it
            {
                current_Access_token = refreshAcessToken(i);
            }

            HttpHeaders connected_headers = new HttpHeaders();
            connected_headers.setBearerAuth(current_Access_token);
            HttpEntity<String> connetctedEntity = new HttpEntity<>(connected_headers);
            ResponseEntity<DriveFileResponse> connected_response = template.exchange(
                    "https://www.googleapis.com/drive/v3/files?q=mimeType+contains+'image/'&fields=files(id,name,mimeType,thumbnailLink,webViewLink,createdTime,imageMediaMetadata)",
                    HttpMethod.GET,
                    connetctedEntity,
                    DriveFileResponse.class);
            DriveFileResponse connecteddrivefileresponse = connected_response.getBody();
            List<PhotoItem> connectedaccountfiles = connecteddrivefileresponse.getFiles();
            all_photos.addAll(connectedaccountfiles);
        }
        return all_photos;
    }

    private String refreshAcessToken(ConnectedAccount connnected_user) {
        String url = "https://oauth2.googleapis.com/token";
        String refreshToken = connnected_user.getRefreshToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();//
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", refreshToken);
        map.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map<String, Object>> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {
                });
        String renewed_token = (String) response.getBody().get("access_token");
        Instant new_expiry = Instant.now().plusSeconds((Integer) response.getBody().get("expires_in"));
        connnected_user.setAccessToken(renewed_token);
        connnected_user.setTokenExpiry(new_expiry);
        connectedAccountRepository.save(connnected_user);
        return renewed_token;
    }
}

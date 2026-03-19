package project.google.cloudvault.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
public class DashboardController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(@AuthenticationPrincipal OAuth2User principal) {
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        String profile_picture_id = principal.getAttribute("picture");
        HashMap<String, Object> json_response = new HashMap<>();
        json_response.put("email", email);
        json_response.put("name", name);
        json_response.put("profile", profile_picture_id);

        return json_response;
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to UnlimitedCloud! <a href='/dashboard'>Login</a>";
    }

}

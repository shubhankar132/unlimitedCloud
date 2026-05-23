package project.google.cloudvault.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.google.cloudvault.model.ConnectedAccount;
import project.google.cloudvault.model.MasterUser;
import project.google.cloudvault.repository.ConnectedAccountRepository;
import project.google.cloudvault.repository.MasterUserRepository;

import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
public class DashboardController {
    @Autowired
    private MasterUserRepository masterUserRepository;
    @Autowired
    private ConnectedAccountRepository connectedAccountRepository;
    // DashboardController(MasterUserRepository masterUserRepository) {
    // this.masterUserRepository = masterUserRepository;
    // }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(@AuthenticationPrincipal OAuth2User principal) {
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        String profile_picture_id = principal.getAttribute("picture");
        HashMap<String, Object> json_response = new HashMap<>();
        json_response.put("email", email);
        json_response.put("name", name);
        json_response.put("profile", profile_picture_id);
        json_response.put("id", principal.getName());
        return json_response;
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to UnlimitedCloud! <a href='/photos.html'>Dashboard</a>";
    }

    @GetMapping("/add-connected-account")
    public void add_connected_account(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        String sessionId = authentication.getName();
        request.getSession().setAttribute("masterGoogleSubjectId", sessionId);
        response.sendRedirect("/oauth2/authorization/google");
    }

    // FOR TESTING DB ENTRIES

    // @GetMapping("/dbEntries")
    // public List<MasterUser> dbEntries() {
    //     return masterUserRepository.findAll();
    // }

    // @GetMapping("/dbEntries/connected")
    // public List<ConnectedAccount> connectedEntries() {
    //     return connectedAccountRepository.findAll();
    // }

}

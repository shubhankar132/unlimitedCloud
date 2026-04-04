package project.google.cloudvault.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.google.cloudvault.model.MasterUser;
import project.google.cloudvault.repository.MasterUserRepository;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private MasterUserRepository masterUserRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String googleSubjectID = principal.getName();// the user id -- not the email id
        if (!masterUserRepository.existsById(googleSubjectID)) 
            {
                MasterUser newUser = new MasterUser();
                newUser.setGoogleSubjectId(googleSubjectID);
                masterUserRepository.save(newUser);
            }
        response.sendRedirect("/photos.html");
    }
}

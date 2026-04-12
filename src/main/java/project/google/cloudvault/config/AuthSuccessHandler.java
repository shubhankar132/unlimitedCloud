package project.google.cloudvault.config;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.google.cloudvault.model.ConnectedAccount;
import project.google.cloudvault.model.MasterUser;
import project.google.cloudvault.repository.ConnectedAccountRepository;
import project.google.cloudvault.repository.MasterUserRepository;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private MasterUserRepository masterUserRepository;
    @Autowired
    private ConnectedAccountRepository connectedAccountRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException 
            {
                OAuth2User principal = (OAuth2User) authentication.getPrincipal();
                String googleSubjectID = principal.getName();// the googleSubject id -- not the email id
                String masterSubjectId = (String) request.getSession().getAttribute("masterGoogleSubjectId");
                String checkUserType = isTheUserInOurDatabases(googleSubjectID);

                if(masterSubjectId!=null)//we are in a session that has a masterUser mapped
                {
                    if(checkUserType.equals("MasterUser"))
                    {
                        request.getSession().invalidate();
                        response.sendRedirect("/error?reason=master-account exists");
                    }
                    else if(checkUserType.equals("ConnectedUser"))
                    {
                        request.getSession().invalidate();
                        response.sendRedirect("/error?reason=account-already-mapped-to-a-master-account");
                    }
                    else
                    {
                        ConnectedAccount connectedAccount=new ConnectedAccount();
                        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                            "google",           // the registration ID from your application.yml
                            authentication.getName()
                        );
                        String accessToken = authorizedClient.getAccessToken().getTokenValue();
                        String refreshToken = authorizedClient.getRefreshToken() != null
                                ? authorizedClient.getRefreshToken().getTokenValue()
                                : null;
                        Instant tokenExpiry = authorizedClient.getAccessToken().getExpiresAt();
                        MasterUser masterUser = masterUserRepository.findById(masterSubjectId).orElseThrow();
                        connectedAccount.setGoogleSubjectId(googleSubjectID);
                        connectedAccount.setAccessToken(accessToken);
                        connectedAccount.setRefreshToken(refreshToken);
                        connectedAccount.setTokenExpiry(tokenExpiry);
                        connectedAccount.setMasterUserID(masterUser);
                        connectedAccountRepository.save(connectedAccount);
                        request.getSession().removeAttribute("masterGoogleSubjectId");
                        response.sendRedirect("/photos.html");
                    }
                }
                else // masterUser is not mapped
                {
                    if (checkUserType.equals("MasterUser")) 
                    {
                        response.sendRedirect("/photos.html");
                    } 
                    else if (checkUserType.equals("ConnectedUser"))
                    {
                        request.getSession().invalidate();
                        response.sendRedirect("/error?reason=account-already-mapped-to-a-master-account");
                    }
                    else 
                    {
                        MasterUser newUser = new MasterUser();
                        newUser.setGoogleSubjectId(googleSubjectID);
                        masterUserRepository.save(newUser);
                        response.sendRedirect("/photos.html");
                    }
                }
            }
    private String isTheUserInOurDatabases(String googleSubjectID)
            {
                if(masterUserRepository.existsById(googleSubjectID))
                    return "MasterUser";
                else if(connectedAccountRepository.existsByGoogleSubjectId(googleSubjectID))
                    return "ConnectedUser";
                else
                    return "NewUser";
            }
}

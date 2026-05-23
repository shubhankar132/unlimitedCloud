package project.google.cloudvault.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Autowired
        private AuthSuccessHandler authSuccessHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/error").permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionFixation()
                                                .migrateSession())
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(authSuccessHandler))
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/")
                                                .deleteCookies("JSESSIONID")
                                                .invalidateHttpSession(true));

                return http.build();
        }
}
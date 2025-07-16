package com.fitness_application.service;

import com.fitness_application.model.User;
import com.fitness_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = extractEmail(oauth2User, registrationId);
        String firstName = extractFirstName(oauth2User, registrationId);
        String lastName = extractLastName(oauth2User, registrationId);
        
        User user = processOAuth2User(email, firstName, lastName, registrationId);
        
        return new CustomOAuth2User(oauth2User, user);
    }
    
    private User processOAuth2User(String email, String firstName, String lastName, String provider) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            return createNewUser(email, firstName, lastName, provider);
        }
    }
    
    private User createNewUser(String email, String firstName, String lastName, String provider) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(User.Role.USER);
        user.setEnabled(true);
        user.setPassword(""); // OAuth2 users don't need password
        
        return userRepository.save(user);
    }
    
    private String extractEmail(OAuth2User oauth2User, String registrationId) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        switch (registrationId) {
            case "google":
                return (String) attributes.get("email");
            case "github":
                String email = (String) attributes.get("email");
                if (email == null) {
                    // GitHub might not provide public email, use login + @github.local
                    String login = (String) attributes.get("login");
                    return login + "@github.local";
                }
                return email;
            default:
                throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }
    }
    
    private String extractFirstName(OAuth2User oauth2User, String registrationId) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        switch (registrationId) {
            case "google":
                return (String) attributes.get("given_name");
            case "github":
                String name = (String) attributes.get("name");
                if (name != null && name.contains(" ")) {
                    return name.split(" ")[0];
                }
                return (String) attributes.get("login");
            default:
                return "Unknown";
        }
    }
    
    private String extractLastName(OAuth2User oauth2User, String registrationId) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        switch (registrationId) {
            case "google":
                return (String) attributes.get("family_name");
            case "github":
                String name = (String) attributes.get("name");
                if (name != null && name.contains(" ")) {
                    String[] parts = name.split(" ");
                    return parts[parts.length - 1];
                }
                return "";
            default:
                return "";
        }
    }
}

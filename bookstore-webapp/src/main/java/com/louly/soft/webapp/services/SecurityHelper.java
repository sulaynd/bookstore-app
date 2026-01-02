package com.louly.soft.webapp.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SecurityHelper {
    // when we add Oauth to security config spring automatically creates OAuth2AuthorizedClientService bean
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityHelper(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // The implementation of Authentication for OAuth2 is OAuth2AuthenticationToken
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            return null;
        }
        // load the authorized client (which contains the access token) using the authorized client service
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        // append the current loggin user access token
        return client.getAccessToken().getTokenValue();
    }
}

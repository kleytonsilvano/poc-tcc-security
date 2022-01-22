package tcc.poc.oauth;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;

import javax.inject.Inject;

public class CustomTokenEnhancer extends TokenEnhancerChain {

    @Inject
    private ClientDetailsService service;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        ClientDetails client = this.service.loadClientByClientId(authentication.getOAuth2Request().getClientId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(client.getAdditionalInformation());
        return accessToken;
    }
}

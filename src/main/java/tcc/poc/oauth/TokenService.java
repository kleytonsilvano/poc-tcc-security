package tcc.poc.oauth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TokenService extends DefaultTokenServices {

    private TokenStore tokenStore;
    private TokenEnhancer accessTokenEnhancer;

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {

        OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
        if(refreshToken == null) {
            throw new InvalidGrantException("Invalid refresh token " + refreshTokenValue);
        }

        OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
        String clientId = authentication.getOAuth2Request().getClientId();
        if(clientId == null || !clientId.equals(tokenRequest.getClientId())) {
            throw new InvalidGrantException("Wrong client for this refresh token " + refreshTokenValue);
        }

        Set<String> scopes = this.reloadScopes(clientId);
        return this.createAccessToken(authentication, refreshToken, scopes);

    }

    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken, Set<String> scopes) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if(validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }
        token.setRefreshToken(refreshToken);
        token.setScope(scopes);
        return this.accessTokenEnhancer != null ? this.accessTokenEnhancer.enhance(token, authentication) : token;
    }

    @Override
    public void setTokenStore(TokenStore tokenStore) {
        super.setTokenStore(tokenStore);
        this.tokenStore = tokenStore;
    }

    @Override
    public void setTokenEnhancer(TokenEnhancer tokenEnhancer) {
        super.setTokenEnhancer(tokenEnhancer);
        this.accessTokenEnhancer = tokenEnhancer;
    }

    private Set<String> reloadScopes(String clientId) {
        Set<String> scopes = new HashSet<>();
        scopes.add("");
        return scopes;
    }


}

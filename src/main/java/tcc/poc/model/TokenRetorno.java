package tcc.poc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tcc.poc.utils.JWTUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRetorno {
    @JsonProperty
    private String access_token;
    @JsonProperty
    private int expires_in;

    public TokenRetorno(Integer expiration, Set<String> scope, String clientId) {
        this.expires_in = expiration;
        this.access_token = JWTUtils.createJWT(new AccessToken(expiration, scope, clientId));
    }

    public TokenRetorno(Integer expiration, Set<String> scope, String clientId, String username, String typeUser) {
        this.expires_in = expiration;
        this.access_token = JWTUtils.createJWT(new AccessToken(expiration, scope, clientId, username, typeUser));
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }


    @org.codehaus.jackson.map.annotate.JsonSerialize(include = org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_DEFAULT)
    @org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public class AccessToken implements Serializable {

        @JsonProperty
        private Set<String> scope;
        @JsonProperty
        private String client_id;
        @JsonProperty
        private Long expiration;
        @JsonProperty
        private String username;
        @JsonProperty
        private String typeUser;

        public AccessToken(Integer expiresIn, Set<String> scope, String client_id) {
            this.scope = scope;
            this.client_id = client_id;
            setExpiration(new Date(System.currentTimeMillis() + (expiresIn * 1000L)).getTime());
        }

        public AccessToken(Integer expiresIn, Set<String> scope, String client_id, String username, String typeUser) {
            this.scope = scope;
            this.client_id = client_id;
            setExpiration(new Date(System.currentTimeMillis() + (expiresIn * 1000L)).getTime());
            this.username = username;
            this.typeUser = typeUser;
        }

        public Set<String> getScope() {
            return scope;
        }

        public void setScope(Set<String> scope) {
            this.scope = scope;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getTypeUser() {
            return typeUser;
        }

        public void setTypeUser(String typeUser) {
            this.typeUser = typeUser;
        }
    }
}

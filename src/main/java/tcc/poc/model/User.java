package tcc.poc.model;

import java.io.Serializable;

@org.codehaus.jackson.map.annotate.JsonSerialize(include = org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_DEFAULT)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @org.codehaus.jackson.annotate.JsonProperty("username")
    @com.fasterxml.jackson.annotation.JsonProperty("username")
    private String username;

    @org.codehaus.jackson.annotate.JsonProperty("password")
    @com.fasterxml.jackson.annotation.JsonProperty("password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
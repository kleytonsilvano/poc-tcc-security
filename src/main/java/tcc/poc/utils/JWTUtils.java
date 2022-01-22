package tcc.poc.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

public class JWTUtils {

    public static String createJWT(Object obj) {

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .claim("token" , obj);

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
}

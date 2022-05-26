package com.humga.cloudservice.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

import static com.humga.cloudservice.util.Constants.*;

public class JwtTokenUtil implements Serializable {
    protected static final Log logger = LogFactory.getLog("JwtTokenUtil");

    public static String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public static String generateToken(String subject, Collection<? extends GrantedAuthority> authorities) {

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scopes", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(TOKEN_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }

    public static void validateToken(
            String token, UserDetails userDetails, AutoExpiringBlackList autoExpiringBlackList) throws JwtException {

        final String username = getUsernameFromToken(token);

        if (!username.equals(userDetails.getUsername())) {
            throw new JwtException("Token username not found: " + username);
        }

        if (isTokenExpired(token)) {
            throw new JwtException("Token for username expired: " + username);
        }

        if (autoExpiringBlackList.contains(token)) {
            throw new JwtException("Blacklisted token received for: " + username);
        }
    }
}

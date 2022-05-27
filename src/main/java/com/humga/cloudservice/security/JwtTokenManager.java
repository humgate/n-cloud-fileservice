package com.humga.cloudservice.security;


import com.humga.cloudservice.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenManager implements Serializable {
    protected static final Log logger = LogFactory.getLog("JwtTokenUtil");

    private final AppProperties properties;
    private final AutoExpiringBlackList tokenBlackList;

    public JwtTokenManager(AppProperties properties) {
        this.properties = properties;
        this.tokenBlackList = new TokenBlackList(properties.getBlacklistMaxSize());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(properties.getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String subject, Collection<? extends GrantedAuthority> authorities) {

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scopes", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(properties.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + properties.getValiditySeconds()*1000))
                .signWith(SignatureAlgorithm.HS256, properties.getSigningKey())
                .compact();
    }

    public void validateToken(String token, UserDetails userDetails) throws JwtException {

        final String username = getUsernameFromToken(token);

        if (!username.equals(userDetails.getUsername())) {
            throw new JwtException("Token username not found: " + username);
        }

        if (isTokenExpired(token)) {
            throw new JwtException("Token for username expired: " + username);
        }

        if (tokenBlackList.contains(token)) {
            throw new JwtException("Blacklisted token received for: " + username);
        }
    }

    public void invalidateToken (String token) {
        tokenBlackList.add(
                token,
                getUsernameFromToken(token),
                convertToLocalDateTime(getExpirationDateFromToken(token)));
    }

    private LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public String getTokenFromHeader(String header) {
        return header.replace(properties.getPrefix(), "");
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        return getTokenFromHeader(request.getHeader(properties.getHeader()));
    }
}


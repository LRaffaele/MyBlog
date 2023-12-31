package it.cgmconsulting.myblog.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.cgmconsulting.myblog.config.SetValuesFromApplicationYaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {

	 
    public static String generateToken(Authentication authentication) {
    	
    	UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    	Map<String, Object> payloadClaims = new HashMap<String, Object>();
    	payloadClaims.put("roles", userPrincipal.getAuthorities());
    	payloadClaims.put("isEnabled", userPrincipal.isEnabled());
    	payloadClaims.put("id", userPrincipal.getId());
    	
        JWTCreator.Builder builder = JWT.create()
        		.withSubject(userPrincipal.getUsername());
        final Instant now = Instant.now();
        builder
        	.withIssuedAt(Date.from(now))
        	.withExpiresAt(Date.from(now.plus(SetValuesFromApplicationYaml.JWT_EXPIRATION_IN_SECONDS, ChronoUnit.SECONDS)));

        if (payloadClaims.isEmpty()) {
            log.warn("You are building a JWT without header claims");
        }
        for (Map.Entry<String, Object> entry : payloadClaims.entrySet()) {
            builder.withClaim(entry.getKey(), entry.getValue().toString());
        }
        return builder.sign(Algorithm.HMAC512(SetValuesFromApplicationYaml.JWT_SECRET));
    }
    
    
    public static DecodedJWT verifyJwt(String jwt) throws TokenExpiredException{
    	DecodedJWT decodedJwt = null;
    	try {
    		decodedJwt = JWT.require(Algorithm.HMAC512(SetValuesFromApplicationYaml.JWT_SECRET)).build().verify(jwt);
    		return decodedJwt;
    	} catch (TokenExpiredException ex){
    		return null;    		
    	} catch (Exception e){
    		log.error("++++++++++++++++ "+e.getMessage());
    		return null;  
    	}
	}
    
    public Long getUserIdFromJWT(String jwt) {
    	DecodedJWT decoded =  verifyJwt(jwt);
    	if(decoded == null)
    		return 0L;    
    	return Long.parseLong(decoded.getClaim("id").asString());    	
    }
}

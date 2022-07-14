package com.yanmastra.quarkusTraining;

import io.smallrye.jwt.auth.principal.*;
import org.jboss.logmanager.Logger;
import org.jose4j.jwt.JwtClaims;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.util.Base64;

@ApplicationScoped
@Alternative
@Priority(1)
public class MyJWTCallerPrincipalFactory extends JWTCallerPrincipalFactory {
    public final static Logger LOGGER = Logger.getLogger(TokenService.class.getSimpleName());

    @Override
    public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) throws ParseException {
        try {
            // Token has already been verified, parse the token claims only
            LOGGER.info(token);
            String payload = /*TokenUtils.decodeJwt(token);*/
            new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), "UTF-8");
            LOGGER.info(payload);
//            String[] params = payload.split(";");
//            JwtClaims claims = new JwtClaims();
//            claims.setSubject(params[0]);
//            claims.setClaim(Claims.preferred_username.name(), params[1]);
//            claims.setClaim(Claims.groups.name(), Arrays.asList(params[2],"CUSTOMER"));
            return new DefaultJWTCallerPrincipal(JwtClaims.parse(payload));
        } catch (Exception ex) {
            throw new ParseException(ex.getMessage());
        }
    }
}
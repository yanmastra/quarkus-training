package com.yanmastra.quarkusTraining;

import com.yanmastra.quarkusTraining.scheme.Roles;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logmanager.Logger;
import org.jose4j.jwt.JwtClaims;

import javax.enterprise.context.RequestScoped;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@RequestScoped
public class TokenService {

    public final static Logger LOGGER = Logger.getLogger(TokenService.class.getSimpleName());

    public String generateUserToken(String username, String name) throws Exception {
//        String payload = username+";"+name+";"+Roles.USER;
//        return TokenUtils.encodeJwt(payload, getExpiryMinuteInFuture());
        return generateToken(name, username, Roles.USER);
    }

    public Date getExpiryMinuteInFuture() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        return calendar.getTime();
    }

    public String generateServiceToken(String serviceId, String serviceName) {
        return generateToken(serviceId,serviceName,Roles.SERVICE);
    }

    public String generateToken(String subject, String name, String... roles) {
        try {
            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setIssuer("DonauTech"); // change to your company
            jwtClaims.setJwtId(UUID.randomUUID().toString());
            jwtClaims.setSubject(subject);
            jwtClaims.setClaim(Claims.amr.name(), subject);
            jwtClaims.setClaim(Claims.preferred_username.name(), name); //add more
            jwtClaims.setClaim(Claims.groups.name(), Arrays.asList(roles));
            jwtClaims.setAudience("using-jwt");
            jwtClaims.setExpirationTimeMinutesInTheFuture(60); // TODO specify how long do you need

            String token = TokenUtils.generateTokenString(jwtClaims);
//            LOGGER.info("TOKEN generated: " + token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

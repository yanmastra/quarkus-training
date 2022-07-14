package com.yanmastra.quarkusTraining;

import com.yanmastra.quarkusTraining.scheme.Roles;
import com.yanmastra.quarkusTraining.scheme.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("training")
public class TrainingListResource {
    public static List<String> training = Arrays.asList(
            "Learning Quarkus",
            "Learning QA testing",
            "API Call and Message Processing",
            "Learning Kogito",
            "Learning GraalVM"
    );

    @GET
//    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrainingList() {
        return Response.ok(training).build();
    }

    @GET
    @Path("halo")
    @RolesAllowed({Roles.USER, Roles.SERVICE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHalo(@Context SecurityContext context) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("method", "GET");
        obj.put("is_secure", context.isSecure());
        obj.put("principal name", context.getUserPrincipal() != null ? context.getUserPrincipal().getName(): "Unknown");
        obj.put("jwt name", jwt.getName());
        return Response.ok(obj).build();
    }

    @POST
    @Path("register")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(User user) {
        try {
            user.persist();
            return Response.ok(user).build();
        }catch (Throwable e){
            return Response.status(500, "{\"message\":\""+e.getMessage()+"\"}").build();
        }
    }

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("username") String username, @QueryParam("password") String password) throws Exception {
        User existingUser = User.find("username", username).firstResult();
        if(existingUser == null || !existingUser.password.equals(password)) {
            throw new WebApplicationException(Response.status(404).entity("No user found or password is incorrect").build());
        }
        String token = service.generateUserToken(existingUser.username, existingUser.name);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("data", existingUser);
        return Response.ok(data).build();
    }

    @DELETE
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMyUser(@Context SecurityContext context) {
        String username = context.getUserPrincipal().getName();
        User user = User.find("username", username).firstResult();
        if (user != null) {
            user.delete();

        }
        return Response.ok("{\"success\":true,\"message\":\"Your account has been deleted successfully\"}").build();
    }

    @Inject
    JsonWebToken jwt;
    @Inject TokenService service;

    @GET
    @Path("permit-all")
    @DenyAll
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@Context SecurityContext ctx) {
        return getResponseString(ctx);
    }

    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello + %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }
}

package us.livebythecode.rest.services.resources;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import us.livebythecode.security.auth.SuperSimpleUserAuthService;
import us.livebythecode.security.jwt.TokenService;

import java.util.ArrayList;
import java.util.List;

@Path("/squid-configuration/authenticate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticateResource {

    @Inject
    TokenService tokenService;

    @Inject
    SuperSimpleUserAuthService authService;

    private static final Logger LOG = Logger.getLogger(AuthenticateResource.class);

    @POST
    public String authenticate(@QueryParam("user") String user, @QueryParam("password") String password) {
        if(authService.validateCredentials(user, password)){
            LOG.info("Login attempt succeeded for: "+user);
            return tokenService.generateToken(user, new ArrayList<>(List.of("Admin")));
        }
        LOG.warn("Login attempt failed for: "+user);
        throw new NotAuthorizedException("Invalid username or password");
    }
}

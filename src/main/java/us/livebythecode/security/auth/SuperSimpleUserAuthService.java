package us.livebythecode.security.auth;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SuperSimpleUserAuthService {

    @ConfigProperty(name = "squid.configuration.user")
    private String user;

    @ConfigProperty(name = "squid.configuration.password")
    private String password;


    public boolean validateCredentials(String userName, String password){
        return this.user.equals(userName) && this.password.equals(password);
    }

}
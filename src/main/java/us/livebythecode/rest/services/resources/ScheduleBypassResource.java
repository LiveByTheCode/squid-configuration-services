package us.livebythecode.rest.services.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import us.livebythecode.rest.model.ScheduledBypassReset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/squid-configuration/schedule-bypass")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleBypassResource {

    @ConfigProperty(name = "squid.enablebypasscommand")
    String enableBypassCommand;

    @ConfigProperty(name = "squid.disablebypasscommand")
    String disableBypassCommand;

    @ConfigProperty(name = "squid.atcommand")
    String atCommand;

    @ConfigProperty(name = "squid.atqcommand")
    String atqCommand;

    @ConfigProperty(name = "squid.atrmcommand")
    String atrmCommand;

    @ConfigProperty(name = "squid.reloadcommand")
    String squidReloadCommand;

    private static final Logger LOG = Logger.getLogger(ScheduleBypassResource.class);

    //String testProxyCommand = "curl --proxy http://localhost:3128 ifconfig.me";

    @GET
    public List<ScheduledBypassReset> list() {
        return executeSystemCommand(atqCommand).stream().map(ScheduledBypassReset::new).collect(Collectors.toList());
    }

    @POST
    @RolesAllowed({"Admin"})
    public boolean add(@QueryParam("minutes") String minutes) {
        LOG.info("Adding "+minutes+" minute whitelist bypass.");
        delete(); //clean up any existing bypasses  
        executeSystemCommand(atCommand+" -f "+disableBypassCommand+" now + "+minutes+" minutes");
        executeSystemCommand(enableBypassCommand);
        return true;
    }

    @DELETE
    @RolesAllowed({"Admin"})
    public boolean delete() {
        LOG.info("Deleting whitelist bypass.");
        executeSystemCommand(disableBypassCommand);
        list().stream().forEach(bypass -> executeSystemCommand(atrmCommand + " " + bypass.getId()));
        return true;
    }

    
    private List<String> executeSystemCommand(String command) {
        List<String> outputStrings = new ArrayList<>();
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", command);
        try {
            outputStrings = readOutput(builder.start().getInputStream());
        } catch (IOException e) {
            LOG.error("System command execution failed for: "+command, e);
        }
        return outputStrings;
    }

    private List<String> readOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines().collect(Collectors.toList());
        }
    }

}

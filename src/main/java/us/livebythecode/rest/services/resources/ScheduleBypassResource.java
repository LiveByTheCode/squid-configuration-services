package us.livebythecode.rest.services.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    //String testProxyCommand = "curl --proxy http://localhost:3128 ifconfig.me";

    @GET
    public List<ScheduledBypassReset> list() {
        return executeSystemCommand(atqCommand).stream().map(ScheduledBypassReset::new).collect(Collectors.toList());
    }

    @POST
    public boolean add(@QueryParam("minutes") String minutes) {
        delete(); //clean up any existing bypasses  
        executeSystemCommand(atCommand+" -f "+disableBypassCommand+" now + "+minutes+" minutes");
        executeSystemCommand(enableBypassCommand);
        return true;
    }

    @DELETE
    public boolean delete() {
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
            e.printStackTrace();
        }
        return outputStrings;
    }

    private List<String> readOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines().collect(Collectors.toList());
        }
    }

}

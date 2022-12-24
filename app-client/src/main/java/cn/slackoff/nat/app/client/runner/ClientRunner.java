package cn.slackoff.nat.app.client.runner;

import cn.slackoff.nat.app.client.components.context.ClientContextHolder;
import org.apache.commons.cli.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yang
 */
@Component
public class ClientRunner implements CommandLineRunner {

    private static void logCommonLineError(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar nat-app-client.jar [...args]", options);
    }

    @Override
    public void run(String[] args) throws Exception {
        Options options = new Options();
        DefaultParser parser = new DefaultParser();
        options.addOption(Option.builder("c").longOpt("client")
                                .desc("Client specified.")
                                .required().hasArg().build());
        options.addOption(Option.builder("t").longOpt("tunnel")
                                .desc("The tunnel(s) to be enabled.")
                                .required().hasArgs().build());
        options.addOption(Option.builder("s").longOpt("server")
                                .desc("The server endpoint to be connected.")
                                .required().hasArg().build());
        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (MissingOptionException ignored) {
            logCommonLineError(options);
            System.exit(-1);
            return;
        }
        String client = commandLine.getOptionValue("client");
        String[] tunnels = commandLine.getOptionValues("tunnel");
        String server = commandLine.getOptionValue("server");

        ClientContextHolder.initialize(client, List.of(tunnels));
        TunnelClient tunnelClient = new TunnelClient();
        tunnelClient.setRetryCount(3);
        tunnelClient.onClosed(e -> System.exit(0));
        tunnelClient.connect(server);
    }
}

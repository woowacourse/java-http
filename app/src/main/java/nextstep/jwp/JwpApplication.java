package nextstep.jwp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import nextstep.jwp.server.WebServer;

public class JwpApplication {

    public static void main(String[] args) throws IOException {
        int port = WebServer.defaultPortIfNull(args);
        if (!isTest(args)) {
            final WebServer webServer = new WebServer(new ServerSocket(port));
            webServer.run();
        }
    }

    private static boolean isTest(String[] args) {
        return Arrays.stream(args)
            .anyMatch(arg -> arg.equalsIgnoreCase("test"));
    }
}

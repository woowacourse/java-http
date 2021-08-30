package nextstep.joanne;

public class JoanneApplication {

    public static void main(String[] args) {
        final int port = WebServer.defaultPortIfNull(args);
        final WebServer webServer = new WebServer(port);
        webServer.run();
    }
}

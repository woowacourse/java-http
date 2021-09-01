package nextstep.jwp;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final WebServer webServer = new WebServer(port, ApplicationContextFactory.create());
        webServer.run();
    }
}

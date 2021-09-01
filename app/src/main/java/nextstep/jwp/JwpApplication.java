package nextstep.jwp;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final Assembler container = new Assembler();
        final WebServer webServer = new WebServer(port, container);
        webServer.run();
    }
}

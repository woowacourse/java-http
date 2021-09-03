package nextstep.jwp;

public class TestFixture {

    static final RequestMapping requestMapping = new RequestMapping();

    static String runRequestHandler(String httpRequest) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, requestMapping);
        requestHandler.run();
        return socket.output();
    }
}

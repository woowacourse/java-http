package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import org.junit.jupiter.api.BeforeEach;

abstract class ControllerTest {

    protected MockSocket socket;
    protected RequestHandler requestHandler;
//
//    @BeforeEach
//    void setUp() {
//        // given
//        socket = new MockSocket();
//        requestHandler = new RequestHandler(socket);
//
//        // when
//        requestHandler.run();
//    }

    protected void sendRequest() {
        // given
        socket = new MockSocket();
        requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();
    }

    protected void sendRequest(String httpRequest) {
        // given
        socket = new MockSocket(httpRequest);
        requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();
    }

}

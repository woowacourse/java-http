package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.FrontController;

abstract class ControllerTest {

    protected MockSocket socket;
    protected FrontController frontController;

    protected void sendRequest() {
        // given
        socket = new MockSocket();
        frontController = new FrontController(socket);

        // when
        frontController.run();
    }

    protected void sendRequest(String httpRequest) {
        // given
        socket = new MockSocket(httpRequest);
        frontController = new FrontController(socket);

        // when
        frontController.run();
    }
}

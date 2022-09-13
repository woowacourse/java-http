package nextstep.org.apache.coyote.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import support.StubSocket;

class ControllerTest {

    protected StubSocket stubSocket;
    protected Controller controller;
    protected Request request;
    protected Response response;

    protected void setRequestAndResponse(final String requestString) throws IOException {
        stubSocket = new StubSocket(requestString);
        request = Request.of(new BufferedReader(new InputStreamReader(stubSocket.getInputStream())));
        response = Response.of(stubSocket.getOutputStream());
    }
}

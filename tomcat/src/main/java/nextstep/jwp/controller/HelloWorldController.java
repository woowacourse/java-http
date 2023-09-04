package nextstep.jwp.controller;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StringResponse;

import java.net.HttpURLConnection;

public class HelloWorldController implements Controller{

    @Override
    public Response handle(Request request) {
        return new StringResponse("Hello world!", HttpURLConnection.HTTP_OK, "OK");
    }
}

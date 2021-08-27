package nextstep.jwp.model;

import java.io.IOException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.HandlerFactory;

public class Response {

    private String message;

    public Response(Request request) throws IOException {
        Handler handler = HandlerFactory.handler(request.getRequestPath().path());
        this.message = handler.message(request);
    }

    public byte[] getBytes() {
        return message.getBytes();
    }
}

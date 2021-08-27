package nextstep.jwp.model;

import java.io.IOException;
import nextstep.jwp.utils.MessageBuilder;

public class Response {

    private String message;

    public Response(Request request) throws IOException {
        this.message = MessageBuilder.build(request);
    }

    public byte[] getBytes() {
        return message.getBytes();
    }
}

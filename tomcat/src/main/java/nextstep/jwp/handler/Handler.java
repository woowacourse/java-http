package nextstep.jwp.handler;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseParser;

public class Handler {


    private static final RequestHandler requestHandler = new RequestHandler();

    public static byte[] handle(HttpRequest request) {
        try {
            HttpResponse response = requestHandler.handle(request);
            return HttpResponseParser.parse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package nextstep.jwp;

import java.io.IOException;

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

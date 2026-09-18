package org.apache.coyote.http11.hadler;

import static org.apache.coyote.http11.config.TomcatServerConfiguration.DEFAULT_CHARSET_NAME;

import java.util.Map;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;

public class RootRequestHandler implements RequestHandler {

    @Override
    public Response handle(Request request) {
        return Response.ok(
                Map.of(
                        "Content-Type", "text/html;charset=" + DEFAULT_CHARSET_NAME
                ),
                "Hello world!"
        );
    }

    @Override
    public boolean canHandle(Request request) {
            return request.requestPoint().path().equals("/");
        }
}
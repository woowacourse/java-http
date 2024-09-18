package org.apache.coyote.http11;

import com.techcourse.RequestMapping;
import java.io.IOException;
import org.apache.catalina.Controller;
import org.apache.catalina.session.InvalidSessionRemover;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.data.MediaType;

public class HttpRequestHandler {
    private static final HttpRequestHandler INSTANCE = new HttpRequestHandler();

    private HttpRequestHandler() {
    }

    public static HttpRequestHandler getInstance() {
        return INSTANCE;
    }


    public HttpResponse handleRequest(HttpRequest request) throws IOException {
        HttpResponse response = InvalidSessionRemover.remove(request);
        if (response != null) {
            return response;
        }

        Controller controller = RequestMapping.getController(request);
        if (controller == null) {
            return new HttpResponse(HttpVersion.HTTP_1_1)
                    .setHttpStatusCode(HttpStatusCode.NOT_FOUND);
        }

        response = new HttpResponse(HttpVersion.HTTP_1_1);
        controller.service(request, response);
        return response;
    }

    public HttpResponse handleBadRequest(IllegalArgumentException e) {
        return new HttpResponse(HttpVersion.HTTP_1_1)
                .setHttpStatusCode(HttpStatusCode.BAD_REQUEST)
                .setResponseBody(e.getMessage())
                .setContentType(new ContentType(MediaType.HTML, null));
    }
}

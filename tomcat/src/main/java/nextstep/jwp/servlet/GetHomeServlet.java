package nextstep.jwp.servlet;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Servlet;

public class GetHomeServlet implements Servlet {

    private static final String WELCOME_MESSAGE = "Hello world!";

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setResponseBody(WELCOME_MESSAGE);
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        return httpRequest.getPath().equals("/");
    }
}

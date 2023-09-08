package nextstep.jwp.handler.get;

import nextstep.jwp.handler.Handler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;

public class RegisterGetHandler implements Handler {

    private static final String STATIC = "static";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final var resource = getClass().getClassLoader().getResource(STATIC + "/register.html");
        setResponse(response, resource);
    }

    private void setResponse(final HttpResponse response, final URL resource) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.HTML);
        response.setResponseBodyByUrl(resource);
    }
}

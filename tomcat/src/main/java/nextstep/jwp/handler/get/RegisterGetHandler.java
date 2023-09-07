package nextstep.jwp.handler.get;

import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public class RegisterGetHandler implements Handler {

    private static final String STATIC = "static";

    @Override
    public HttpResponse resolve(final HttpRequest request) throws IOException {
        final var resource = getClass().getClassLoader().getResource(STATIC + "/register.html");
        return HttpResponse.createBy(request.getVersion(), resource, StatusCode.OK);
    }
}

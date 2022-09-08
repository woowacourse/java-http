package nextstep.jwp.handler;

import org.apache.coyote.http11.handler.RequestServlet;
import org.apache.coyote.http11.handler.ServletResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class HomeServlet implements RequestServlet {

    private static final String HOME_BODY = "Hello world!";

    @Override
    public ServletResponseEntity doGet(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        return ServletResponseEntity.createWithResource(HOME_BODY);
    }

    @Override
    public ServletResponseEntity doPost(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        throw new IllegalStateException("Invalid Uri");
    }
}

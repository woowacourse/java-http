package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;

public class RootController extends AbstractController {

    private static final String RESOURCE = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusLine(new StatusLine(HttpStatus.OK))
                .setHeaders(ResponseHeaders.create(request, "Hello world!"))
                .setResource(RESOURCE);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("POST에 해당하는 hadnler가 없습니다.");
    }
}

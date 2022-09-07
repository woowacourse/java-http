package nextstep.jwp.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatusCode;

public class FileController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest, HttpStatusCode.OK, httpRequest.getUrl());
    }
}

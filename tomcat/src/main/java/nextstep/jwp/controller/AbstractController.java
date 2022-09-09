package nextstep.jwp.controller;

import nextstep.jwp.view.View;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected abstract HttpResponse doPost(HttpRequest request);

    protected abstract HttpResponse doGet(HttpRequest request);

    protected HttpResponse doNotFoundRequest() {
        return HttpResponse.notFound()
                .addResponseBody(View.NOT_FOUND.getContents(), ContentType.TEXT_HTML_CHARSET_UTF_8)
                .build();
    }
}

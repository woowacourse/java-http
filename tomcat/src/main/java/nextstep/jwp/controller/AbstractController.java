package nextstep.jwp.controller;

import nextstep.jwp.view.View;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected abstract HttpResponse doPost(HttpRequest request);

    protected abstract HttpResponse doGet(HttpRequest request);

    @Override
    public HttpResponse service(final HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        Method method = requestLine.getMethod();

        if (method.isPost()) {
            return doPost(request);
        }
        if (method.isGet()) {
            return doGet(request);
        }
        return doNotFoundRequest();
    }

    protected HttpResponse doNotFoundRequest() {
        return HttpResponse.notFound()
                .addResponseBody(View.NOT_FOUND.getContents(), ContentType.TEXT_HTML_CHARSET_UTF_8)
                .build();
    }
}

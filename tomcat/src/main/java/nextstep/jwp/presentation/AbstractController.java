package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.common.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String httpMethod = httpRequest.getRequestMethod();

        try {
            if (HttpMethod.GET.isSameMethod(httpMethod)) {
                doGet(httpRequest, httpResponse);
            }
        } catch (final UncheckedServletException | IOException e) {

        }
    }

    abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}

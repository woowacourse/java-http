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

            if (HttpMethod.POST.isSameMethod(httpMethod)) {
                doPost(httpRequest, httpResponse);
            }
        } catch (final UncheckedServletException | IOException e) {

        }
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        throw new UnsupportedOperationException();
    }
}

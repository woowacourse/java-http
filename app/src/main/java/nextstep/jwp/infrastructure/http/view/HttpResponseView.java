package nextstep.jwp.infrastructure.http.view;

import java.util.Optional;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public class HttpResponseView implements View {

    private final HttpResponse httpResponse;

    public HttpResponseView(final HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public Optional<HttpResponse> httpResponse(final String defaultPath) {
        return Optional.of(httpResponse);
    }
}

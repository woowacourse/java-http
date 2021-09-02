package nextstep.jwp.infrastructure.http.handler;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public interface Handler {

    void handle(final HttpRequest request, final HttpResponse response) throws Exception;
}

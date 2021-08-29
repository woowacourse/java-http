package nextstep.jwp.mapping;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Handler {

    void handle(HttpRequest request, HttpResponse httpResponse) throws Exception;
}

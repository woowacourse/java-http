package nextstep.jwp.dispatcher.handler;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface Handler {

    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}

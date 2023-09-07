package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponseBuilder;

import java.io.IOException;

public interface Controller {
    String process(HttpRequest httpRequest, HttpResponseBuilder httpResponseBuilder) throws IOException;
}

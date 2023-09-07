package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponseBuilder;

import java.io.IOException;

public interface Controller {
    String process(HttpRequest httpRequest, HttpResponseBuilder httpResponseBuilder) throws IOException;
}

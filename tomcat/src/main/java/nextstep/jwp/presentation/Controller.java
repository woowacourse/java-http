package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

import java.io.IOException;

public interface Controller {
    String process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}

package nextstep.jwp.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public interface Handler {

    void service(HttpRequest request, HttpResponse response) throws IOException;
}

package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;

import java.io.IOException;

import static org.apache.coyote.http.HttpMethod.GET;

public class RootController implements Controller {
    @Override
    public String process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.getMethod() == GET) {
            return doGet(httpRequest, httpResponse);
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }

    private String doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        return HttpResponseBuilder.buildCustomResponse(httpRequest, httpResponse, "Hello world!");
    }
}

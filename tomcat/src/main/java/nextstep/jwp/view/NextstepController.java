package nextstep.jwp.view;

import org.apache.http.BasicHttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.info.ContentType;
import org.apache.http.info.HttpMethod;
import org.apache.http.info.HttpVersion;
import org.apache.http.info.StatusCode;
import org.springframework.annotation.Controller;
import org.springframework.annotation.RequestMapping;

@Controller
public class NextstepController {

    private static final String WELCOME_MESSAGE = "Hello world!";

    @RequestMapping(method = HttpMethod.GET, uri = "/")
    public HttpResponse hello(final HttpRequest httpRequest) {
        return BasicHttpResponse.builder()
                .httpVersion(HttpVersion.HTTP_1_1)
                .statusCode(StatusCode.OK_200)
                .contentType(ContentType.TEXT_HTML.getName())
                .body(WELCOME_MESSAGE)
                .build();
    }
}

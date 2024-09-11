package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.handler.NotFoundHandler;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.request.Http11RequestHeaders;
import org.apache.coyote.http11.request.Http11RequestLine;
import org.apache.coyote.http11.response.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NotFoundHandlerTest {

    private RequestHandler handler = new NotFoundHandler();

    @DisplayName("존재하지 않는 리소스를 요청하면 404 페이지를 응답한다.")
    @Test
    void handle1() throws Exception {
        Http11RequestLine requestLine = new Http11RequestLine("GET /nothing HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(List.of());
        Http11RequestBody requestBody = new Http11RequestBody("");
        HttpRequest request = new Http11Request(requestLine, headers, requestBody);
        HttpResponse response = new Http11Response();

        handler.handle(request, response);

        String actual = response.getResponseMessage();
        String expectedResponseLine = "HTTP/1.1 404 Not Found";
        URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expectedContentLength =
                "Content-Length: " + Files.readAllBytes(new File(resource.getFile()).toPath()).length;

        assertThat(actual).contains(expectedResponseLine);
        assertThat(actual).contains(expectedContentLength);
    }
}

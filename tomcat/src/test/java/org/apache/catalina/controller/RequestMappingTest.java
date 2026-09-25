package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http.request.ContentType;
import org.apache.coyote.http.response.EmptyBody;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestLine;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final Controller controller = request -> HttpResponse.ok(EmptyBody.INSTANCE);
    private final RequestMapping requestMapping = new RequestMapping(Map.of("/login", controller));

    @Test
    void 등록된_경로의_컨트롤러를_찾는다() {
        assertThat(requestMapping.getController(get("/login"))).contains(controller);
    }

    @Test
    void 등록되지_않은_경로면_비어_있다() {
        assertThat(requestMapping.getController(get("/unknown"))).isEmpty();
    }

    private HttpRequest get(String path) {
        return new HttpRequest(
                RequestLine.from("GET " + path + " HTTP/1.1 "),
                HttpHeaders.from(List.of()),
                RequestBody.of(ContentType.PLAIN, ""));
    }
}

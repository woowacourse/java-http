package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http.request.ContentType;
import org.apache.coyote.http.response.EmptyBody;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestLine;
import org.junit.jupiter.api.Test;

class DispatcherTest {

    private final Controller mapped = request -> HttpResponse.of(HttpStatus.OK, EmptyBody.INSTANCE);
    private final Controller fallback = request -> HttpResponse.of(HttpStatus.NOT_FOUND, EmptyBody.INSTANCE);
    private final Dispatcher dispatcher =
            new Dispatcher(new RequestMapping(Map.of("/login", mapped)), fallback);

    @Test
    void 매핑된_경로는_해당_컨트롤러가_처리한다() throws IOException {
        HttpResponse response = dispatcher.service(get("/login"));

        assertThat(response.statusLine().getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void 매핑되지_않은_경로는_폴백_컨트롤러가_처리한다() throws IOException {
        HttpResponse response = dispatcher.service(get("/style.css"));

        assertThat(response.statusLine().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private HttpRequest get(String path) {
        return new HttpRequest(
                RequestLine.from("GET " + path + " HTTP/1.1 "),
                HttpHeaders.from(List.of()),
                RequestBody.of(ContentType.PLAIN, ""));
    }
}

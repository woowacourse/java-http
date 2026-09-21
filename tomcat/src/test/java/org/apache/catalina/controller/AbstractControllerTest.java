package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.EmptyBody;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.RequestLine;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private final Controller getOnlyController = new AbstractController() {
        @Override
        protected HttpResponse doGet(HttpRequest request) {
            return HttpResponse.ok(EmptyBody.INSTANCE);
        }
    };

    @Test
    void 구현한_메서드로_요청하면_해당_메서드가_처리한다() throws IOException {
        HttpResponse response = getOnlyController.service(request("GET"));

        assertThat(response.statusLine().getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void 구현하지_않은_메서드로_요청하면_405를_반환한다() throws IOException {
        HttpResponse response = getOnlyController.service(request("POST"));

        assertThat(response.statusLine().getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private HttpRequest request(String method) {
        return new HttpRequest(
                RequestLine.from(method + " /any HTTP/1.1 "),
                HttpHeaders.from(List.of()),
                RequestBody.of(ContentType.PLAIN, ""));
    }
}

package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    void html_파일을_받을_수_있다() throws URISyntaxException, IOException {
        // given
        String body = "index.html";
        ResponseEntity responseEntity = ResponseEntity.body(body);

        // when
        URI uri = getClass().getClassLoader().getResource("static/index.html").toURI();
        String expectedBody = new String(Files.readAllBytes(Paths.get(uri)));
        Response response = Response.of(responseEntity);
        String expected = String.join("\r\n",
                "HTTP/1.1 " + 200 + " " + "OK" + " ",
                "Content-Type: text/" + "html" + ";charset=utf-8 ",
                "Content-Length: " + expectedBody.getBytes().length + " ",
                "",
                expectedBody);

        // then
        assertThat(response.asString()).isEqualTo(expected);
    }

    @Test
    void css_컨텐츠_타입을_받을_수_있다() throws URISyntaxException, IOException {
        // given
        String body = "/css/styles.css";
        ResponseEntity responseEntity = ResponseEntity.body(body);

        // when
        URI uri = getClass().getClassLoader().getResource("static/css/styles.css").toURI();
        String expectedBody = new String(Files.readAllBytes(Paths.get(uri)));
        Response response = Response.of(responseEntity);
        String expected = String.join("\r\n",
                "HTTP/1.1 " + 200 + " " + "OK" + " ",
                "Content-Type: text/" + "css" + ";charset=utf-8 ",
                "Content-Length: " + expectedBody.getBytes().length + " ",
                "",
                expectedBody);

        // then
        assertThat(response.asString()).isEqualTo(expected);
    }

    @Test
    void js_컨텐츠_타입을_받을_수_있다() throws URISyntaxException, IOException {
        // given
        String body = "/js/scripts.js";
        ResponseEntity responseEntity = ResponseEntity.body(body);

        // when
        URI uri = getClass().getClassLoader().getResource("static/js/scripts.js").toURI();
        String expectedBody = new String(Files.readAllBytes(Paths.get(uri)));
        Response response = Response.of(responseEntity);
        String expected = String.join("\r\n",
                "HTTP/1.1 " + 200 + " " + "OK" + " ",
                "Content-Type: text/" + "javascript" + ";charset=utf-8 ",
                "Content-Length: " + expectedBody.getBytes().length + " ",
                "",
                expectedBody);

        // then
        assertThat(response.asString()).isEqualTo(expected);
    }

    @Test
    void 문자열을_받을_수_있다() {
        // given
        String body = "eden king";
        ResponseEntity responseEntity = ResponseEntity.body(body);

        // when
        Response response = Response.of(responseEntity);
        String expected = String.join("\r\n",
                "HTTP/1.1 " + 200 + " " + "OK" + " ",
                "Content-Type: text/" + "html" + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);

        // then
        assertThat(response.asString()).isEqualTo(expected);
    }

    @Test
    void redirect인_경우_302를_받을_수_있다() {
        // given
        String body = "redirect:index.html";
        ResponseEntity responseEntity = ResponseEntity.body(body).status(HttpStatus.REDIRECT);

        // when
        Response response = Response.of(responseEntity);
        String expected = String.join("\r\n",
                "HTTP/1.1 " + 302 + " " + "FOUND" + " ",
                "Location: index.html ",
                "",
                ""
                );

        // then
        assertThat(response.asString()).isEqualTo(expected);
    }
}

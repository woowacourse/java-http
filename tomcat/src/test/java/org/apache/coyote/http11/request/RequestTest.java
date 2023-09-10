package org.apache.coyote.http11.request;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

class RequestTest {

    @Test
    void BufferedReader를_입력받아_생성한다() throws IOException {
        // given
        final String requestString = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // when
        final Request request = Request.from(bufferedReader);

        // then
        assertAll(() -> {
            assertThat(request.getRequestLine().getRequestPath()).isEqualTo("/index.html");
            assertThat(request.getRequestLine().getRequestMethod()).isEqualTo(RequestMethod.GET);
            assertThat(request.getBody()).isEmpty();
        });
    }

    @Test
    void 키값으로_요청_파라미터값을_조회한다_쿼리스트링() throws IOException {
        // given
        final String requestString = String.join("\r\n",
                "GET /index.html?a=1&b=2 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final Request request = Request.from(bufferedReader);

        // when
        final Optional<String> actual = request.getParameter("b");

        // then
        assertThat(actual).contains("2");
    }


    @Test
    void 키값으로_요청_파라미터값을_조회한다_리퀘스트바디() throws IOException {
        // given
        String content = "a=1&b=2";
        final String requestString = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + content.length(),
                "",
                content);
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final Request request = Request.from(bufferedReader);

        // when
        final Optional<String> actual = request.getParameter("b");

        // then
        assertThat(actual).contains("2");
    }

    @Test
    void JSESSIONID가_있는_요청에서는_해당_세션을_조회하여_갖고있는다() throws IOException {
        // given
        final Session session = new Session();
        SessionManager.add(session);

        final String requestString = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + session.getId() + " ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final Request request = Request.from(bufferedReader);

        // when
        Session actual = request.getSession();

        // then
        assertThat(actual).isEqualTo(session);
    }

    @Test
    void JSESSIONID가_없는_요청에서는_새로운_세션을_만들어서_갖고있는다() throws IOException {
        // given
        final String requestString = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final Request request = Request.from(bufferedReader);

        // when
        Session actual = request.getSession();

        // then
        assertThat(actual).isNotNull();
    }
}

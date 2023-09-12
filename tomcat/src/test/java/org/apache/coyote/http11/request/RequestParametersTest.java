package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.EntityHeader;
import org.apache.coyote.http11.header.Headers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RequestParametersTest {

    @Test
    void 리퀘스트라인_헤더_바디_정보를_받아서_생성한다_쿼리스트링() {
        //given
        final RequestLine requestLine = RequestLine.from("GET /login?a=1&b=2 HTTP/1.1 ");
        final Headers headers = new Headers();
        final String body = "";

        //when
        final RequestParameters requestParameters = RequestParameters.of(requestLine, headers, body);

        //then
        assertThat(requestParameters.getValues()).hasSize(2);
    }

    @Test
    void 리퀘스트라인_헤더_바디_정보를_받아서_생성한다_리퀘스트바디() {
        //given
        final String content = "a=1&b=2";
        final RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1 ");
        final Headers headers = new Headers();
        headers.addHeader(EntityHeader.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.addHeader(EntityHeader.CONTENT_LENGTH, String.valueOf(content.length()));
        final String body = "a=1&b=2";

        //when
        final RequestParameters requestParameters = RequestParameters.of(requestLine, headers, body);

        //then
        assertThat(requestParameters.getValues()).hasSize(2);
    }

    @Test
    void 쿼리파라미터키값으로_조회한다() {
        //given
        final String content = "a=1&b=2";
        final RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1 ");
        final Headers headers = new Headers();
        headers.addHeader(EntityHeader.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.addHeader(EntityHeader.CONTENT_LENGTH, String.valueOf(content.length()));
        final String body = "a=1&b=2";
        final RequestParameters requestParameters = RequestParameters.of(requestLine, headers, body);

        //when & then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(requestParameters.getValue("a")).contains("1");
            softAssertions.assertThat(requestParameters.getValue("b")).contains("2");
        });
    }
}

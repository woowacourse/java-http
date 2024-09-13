package org.was.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;
import org.junit.jupiter.api.Test;
import org.was.controller.ResponseResult;
import org.was.view.ViewResolver;

class HttpResponseConverterTest {

    @Test
    void 응답_결과로_응답을_생성() throws IOException {
        // given
        String headerAttribute = "Set-Cookie";
        String headerKeyAndValue = "ted=1234";
        String bodyText = "hello world";

        HttpResponse response = new HttpResponse();
        ViewResolver viewResolver = ViewResolver.getInstance();
        ResponseResult responseResult = ResponseResult
                .status(HttpStatusCode.OK)
                .header(headerAttribute, headerKeyAndValue)
                .body(bodyText);

        HttpResponseConverter responseConverter = HttpResponseConverter.getInstance();

        // when
        responseConverter.convert(response, viewResolver, responseResult);

        // then
        StatusLine statusLine = response.getStatusLine();
        ResponseHeader header = response.getHeader();
        ResponseBody body = response.getBody();

        assertAll(
                () -> assertThat(statusLine.getStatusCode()).isEqualTo(HttpStatusCode.OK.getStatusCode()),
                () -> assertThat(header.getHeaders()).hasSize(3),
                () -> assertThat(header.getHeaders()).containsEntry(headerAttribute, headerKeyAndValue),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Type", "text/html;charset=utf-8" ),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Length", "11" ),
                () -> assertThat(body.getContent()).isEqualTo(bodyText)
        );
    }

    @Test
    void 상태코드와_본문으로_응답을_생성() throws IOException {
        // given
        String bodyText = "hello world";
        HttpResponse response = new HttpResponse();
        HttpResponseConverter responseConverter = HttpResponseConverter.getInstance();

        // when
        responseConverter.convert(response, HttpStatusCode.NOT_FOUND, bodyText);

        // then
        StatusLine statusLine = response.getStatusLine();
        ResponseHeader header = response.getHeader();
        ResponseBody body = response.getBody();

        assertAll(
                () -> assertThat(statusLine.getStatusCode()).isEqualTo(HttpStatusCode.NOT_FOUND.getStatusCode()),
                () -> assertThat(header.getHeaders()).hasSize(2),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Type", "text/html;charset=utf-8" ),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Length", "11" ),
                () -> assertThat(body.getContent()).isEqualTo(bodyText)
        );
    }
}

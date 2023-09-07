package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void prepareResponse() {
        //given
        final var request = HttpRequest.builder()
                .version(HTTP_1_1)
                .method(GET)
                .path(new HttpPath("/index.html"))
                .build();

        //when
        HttpResponse response = HttpResponse.prepareFrom(request);

        //then
        final var expected = new HttpResponse(HTTP_1_1);


        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void addHeader() {
        //given
        final var response = new HttpResponse(HTTP_1_1);

        //when
        response.addHeader("key", "value");

        //then
        String responseString = response.buildResponse();
        assertThat(responseString).contains("key: value");
    }

    @ParameterizedTest
    @EnumSource(HttpStatus.class)
    void setStatus(HttpStatus httpStatus) {
        //given
        final var response = new HttpResponse(HTTP_1_1);

        //when
        response.setStatus(httpStatus);

        //then
        assertThat(response).extracting("httpStatus").isEqualTo(httpStatus);
    }

    @Test
    void setBody() {
        //given
        final var response = new HttpResponse(HTTP_1_1);
        final var body = ResponseBody.from("body");

        //when
        response.setBody(body);

        //then
        assertThat(response).extracting("body").isEqualTo(body);
    }

    @Test
    void setContentType() {
        //given
        final var response = new HttpResponse(HTTP_1_1);
        final var contentType = ContentType.PLAINTEXT_UTF8;

        //when
        response.setContentType(contentType);

        //then
        assertThat(response).extracting("contentType").isEqualTo(contentType);
    }



}


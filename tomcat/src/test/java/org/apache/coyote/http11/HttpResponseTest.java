package org.apache.coyote.http11;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

}

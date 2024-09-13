package org.apache.coyote.http11;

import org.apache.coyote.RequestContainer;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class RequestContainerTest {

    @Test
    @DisplayName("요청과 일치하는 Controller가 없는 경우 404 페이지로 리다이렉트 한다.")
    void process() {
        RequestContainer requestContainer = new RequestContainer();
        HttpRequest httpRequest = HttpRequestFixture.NO_EXIST_METHOD_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        requestContainer.invoke(httpRequest, httpResponse);

        assertThat(new String(httpResponse.combineResponseToBytes())).contains("Location: 404.html");
    }
}

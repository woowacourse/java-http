package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import org.apache.coyote.RequestProcessor;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestProcessorTest {

    @Test
    @DisplayName("요청과 일치하는 Controller가 없는 경우 404 페이지로 리다이렉트 한다.")
    void process() {
        RequestProcessor requestProcessor = new RequestProcessor();
        HttpRequest httpRequest = HttpRequestFixture.NO_EXIST_METHOD_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        requestProcessor.process(httpRequest, httpResponse);

        assertThat(new String(httpResponse.combineResponseToBytes())).contains("Location: 404.html");
    }
}

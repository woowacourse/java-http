package nextstep.jwp;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static nextstep.jwp.RequestHandlerTest.postRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DispatcherTest {

    private Dispatcher dispatcher;

    @BeforeAll
    void inject(){
        dispatcher = new Assembler().dispatcher();
    }

    @Test
    void dispatch() {
        // given
        final String requestBody = "account=corgi&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /register HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

//        httpResponse.
        // then
        final String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: index.html \r\n" +
                "\r\n";

//        assertThat(socket.output()).isEqualTo(expected);
    }
}
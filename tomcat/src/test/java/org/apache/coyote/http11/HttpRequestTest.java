package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.util.Symbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("Http 요청 객체가 정상적으로 생성된다.")
    @Test
    void createHttpRequestAndGet() {
        // given
        String method = "GET";
        String path = "/index.html";
        String[] parameters = new String[0];
        String protocal = "HTTP/1.1";
        String requestBody = "";

        String host = "Host";
        String hostName = "localhost:8080";
        String headerHost = host + Symbol.COLON + hostName;
        String connection = "Connection";
        String connectionType = "keep-alive";
        String headerConnection = connection + Symbol.COLON + connectionType;
        String accept = "Accept";
        String acceptValue = "*/*";
        String headerAccept = accept + Symbol.COLON + acceptValue;
        String[] headers = {headerHost, headerConnection, headerAccept};

        // when
        HttpRequest actualRequest = new HttpRequest(method, path, path, parameters, protocal, headers, requestBody);

        // then
        assertAll(
                () -> assertThat(actualRequest.toString()).contains(method),
                () -> assertThat(actualRequest.toString()).contains(path),
                () -> assertThat(actualRequest.toString()).contains(protocal),
                () -> assertThat(actualRequest.toString()).contains(host),
                () -> assertThat(actualRequest.toString()).contains(hostName),
                () -> assertThat(actualRequest.toString()).contains(connection),
                () -> assertThat(actualRequest.toString()).contains(connectionType),
                () -> assertThat(actualRequest.toString()).contains(accept),
                () -> assertThat(actualRequest.toString()).contains(acceptValue),
                () -> assertThat(actualRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(actualRequest.getPath()).isEqualTo(path),
                () -> assertThat(actualRequest.getUri()).isEqualTo(path)
        );
    }
}

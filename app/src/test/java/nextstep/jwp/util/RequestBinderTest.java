package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import nextstep.TestUtil;
import nextstep.jwp.http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBinderTest {

    @DisplayName("BufferedReader를 사용해 HttpRequest를 생성한다.")
    @Test
    void create() throws IOException {
        HttpRequest expected = TestUtil.createRequest("GET /login?account=gugu&password=password HTTP/1.1");

        String requestMessage = "GET /login?account=gugu&password=password HTTP/1.1" + System.lineSeparator() +
            "Host: localhost:8080" + System.lineSeparator() +
            "Connection: keep-alive" + System.lineSeparator() +
            "Accept: */*";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
            new ByteArrayInputStream(requestMessage.getBytes()))
        );

        HttpRequest actual = RequestBinder.createRequestByMessage(bufferedReader);

        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
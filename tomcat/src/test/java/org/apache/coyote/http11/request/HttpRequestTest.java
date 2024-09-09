package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.catalina.session.UuidSessionGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("요청 쿼리 스트링을 파싱한다.")
    @Test
    void parseQuery() throws IOException {
        String rawRequest = String.join("\r\n",
                "GET /subway?bread=white&sauce=pepper HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        BufferedReader requestReader = new BufferedReader(new StringReader(rawRequest));
        HttpRequest httpRequest = new HttpRequestReader(requestReader, new UuidSessionGenerator()).read();

        Queries queries = httpRequest.getQueries();

        assertAll(
                () -> assertThat(queries.get("bread")).isEqualTo("white"),
                () -> assertThat(queries.get("sauce")).isEqualTo("pepper")
        );
    }
}

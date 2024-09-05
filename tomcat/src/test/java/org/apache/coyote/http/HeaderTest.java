package org.apache.coyote.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class HeaderTest {

    private final String bulkHeaders = String.join("\r\n",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

    @Test
    @DisplayName("")
    void ofString() {
        Header header = Header.of(bulkHeaders);

        assertThatCode(() -> header.getKey("Host")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("")
    void ofList() {
        List<String> headers = List.of(bulkHeaders.split("\r\n"));

        Header header = Header.of(headers);

        assertThatCode(() -> header.getKey("Host")).doesNotThrowAnyException();
    }

}

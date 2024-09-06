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
    @DisplayName("하나의 String 으로 들어온 헤더를 파싱하여 저장한다.")
    void ofString() {
        Header header = Header.of(bulkHeaders);

        assertThatCode(() -> header.getValue("Host")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("List<String> 형식으로 들어온 헤더를 파싱하여 저장한다.")
    void ofList() {
        List<String> headers = List.of(bulkHeaders.split("\r\n"));

        Header header = Header.of(headers);

        assertThatCode(() -> header.getValue("Host")).doesNotThrowAnyException();
    }

}

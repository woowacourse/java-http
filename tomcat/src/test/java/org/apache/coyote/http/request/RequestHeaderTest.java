package org.apache.coyote.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class RequestHeaderTest {

    private final String bulkHeaders = String.join("\r\n",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

//    @Test
//    @DisplayName("List<String> 형식으로 들어온 헤더를 파싱하여 저장한다.")
//    void ofList() {
//        List<String> headers = List.of(bulkHeaders.split("\r\n"));
//
//        RequestHeader header = new RequestHeader(headers);
//
//        assertThatCode(() -> header.getValue("Host")).doesNotThrowAnyException();
//    }
}

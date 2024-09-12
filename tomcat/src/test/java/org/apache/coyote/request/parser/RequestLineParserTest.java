package org.apache.coyote.request.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class RequestLineParserTest {

    private static final String WRONG_REQUEST_LINE = "POST / HTTP/1.1 POLLA";
    private static final String ORDINAL_REQUEST_LINE = "POST / HTTP/1.1";
    private static final String EXPECTED_METHOD = "POST";
    private static final String EXPECTED_PATH = "/";
    private static final String EXPECTED_PROTOCOL = "HTTP/1.1";

    @Test
    @DisplayName("Request Line이 적절한 형식이 아닌 경우 에러를 발생한다.")
    void parse_WhenWrongRequestLine() {
        assertThrows(IllegalArgumentException.class,
                () -> new RequestLineParser(WRONG_REQUEST_LINE));
    }

    @Test
    @DisplayName("Request Line을 파싱하여 전달한다.")
    void parseRequestLine() {
        RequestLineParser requestLineParser = new RequestLineParser(ORDINAL_REQUEST_LINE);

        assertAll(
                () -> assertEquals(requestLineParser.parseRequestMethod(), EXPECTED_METHOD),
                () -> assertEquals(requestLineParser.parseRequestPath(), EXPECTED_PATH),
                () -> assertEquals(requestLineParser.parseRequestProtocol(), EXPECTED_PROTOCOL)
        );
    }
}

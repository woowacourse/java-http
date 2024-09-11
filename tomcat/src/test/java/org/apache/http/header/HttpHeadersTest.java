package org.apache.http.header;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    void testToString() {
        HttpHeaders headers = new HttpHeaders(new HttpHeader[]{
                new HttpHeader("Host", "http://localhost:8080"),
                new HttpHeader("Connection", "keep-alive")
        });

        assertEquals("Host: http://localhost:8080\r\nConnection: keep-alive\r\n", headers.toString());
    }
}

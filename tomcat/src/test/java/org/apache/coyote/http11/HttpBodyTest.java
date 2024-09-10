package org.apache.coyote.http11;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpBodyTest {

    @Test
    @DisplayName("Byte 배열로 HttpBody를 생성한다.")
    void createFromByteArray() {
        String content = "Hello, World!";
        HttpBody httpBody = new HttpBody(content.getBytes());
        assertEquals(content, httpBody.getContent());
    }

    @Test
    @DisplayName("content가 null이 주어지는 경우, 빈 문자열로 치환해 저장한다.")
    void emptyContentFromNullInput() {
        String content = null;
        HttpBody httpBody = new HttpBody(content);
        assertEquals("", httpBody.getContent());
    }
}

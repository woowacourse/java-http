package nextstep.jwp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestLineTest {
    @Test
    @DisplayName("GET 요청에 대한 RequestLine 생성 - 성공")
    void createGetRequestLine() {
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        assertTrue(requestLine.isGet());
        assertEquals(requestLine.getPath(), "/");
    }

    @Test
    @DisplayName("POST 요청에 대한 RequestLine 생성 - 성공")
    void createPostRequestLine() {
        RequestLine requestLine = new RequestLine("POST / HTTP/1.1");
        assertTrue(requestLine.isPost());
        assertEquals(requestLine.getPath(), "/");
    }

    @Test
    @DisplayName("RequestLine 생성 - 실패")
    void createRequestLineFailure() {
         assertThrows(IllegalArgumentException.class, () -> new RequestLine(null));
    }
}

package org.apache.coyote.http11.message.parser;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;

// 현재는 Parser를 바로 구현하지만 추후 json, 파일 등의 다른 형태의 데이터가 바디에 들어올 경우
// 해당 클래스를 추상 클래스로 전환하고 전략에 따른 여러 구현체가 생길 수 있음
public class HttpBodyParser implements Parser<HttpBody> {
    private final HttpHeaders headers;

    public HttpBodyParser(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public HttpBody parse(BufferedReader reader) throws IOException {
        if (!headers.hasContentLength()) {
            return HttpBody.init();
        }

        int contentLength = headers.getContentLength();
        char[] bodyChars = new char[contentLength];
        int read = reader.read(bodyChars, 0, contentLength);
        validateReadLength(read, contentLength);
        return HttpBody.from(new String(bodyChars));
    }

    private void validateReadLength(int read, int contentLength) throws IOException {
        if (read != contentLength) {
            throw new IOException("Unexpected end of body");
        }
    }
}

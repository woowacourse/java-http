package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.common.Constants;
import org.apache.coyote.http11.common.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestMessageReader {

    private HttpRequestMessageReader() {
    }

    private static final Logger log = LoggerFactory.getLogger(HttpRequestMessageReader.class.getName());

    public static HttpRequest read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = RequestLine.from(reader.readLine());
        HttpHeader header = HttpHeader.from(readUntilHeaderEnds(reader));
        HttpRequest request = new HttpRequest(requestLine, header, RequestBody.empty());
        header.getContentLength()
                .ifPresent(byteLength -> request.setBody(readRequestBodyOfLength(reader, byteLength)));
        return request;
    }

    private static String readUntilHeaderEnds(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(Constants.CRLF);
            if (line.isEmpty()) {
                break;
            }
        }
        return sb.toString();
    }

    private static String readRequestBodyOfLength(BufferedReader reader, int byteLength) {
        char[] buffer = new char[byteLength];
        try {
            if (reader.read(buffer) != byteLength) {
                log.warn("Content-Length와 실제 요청 본문의 길이가 다릅니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("요청 본문을 읽는 중 오류가 발생했습니다.");
        }
        return new String(buffer);
    }
}

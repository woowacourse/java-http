package org.apache.coyote.http11.message.request;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.message.HttpHeaders;

// TODO: BufferedReader와 BufferedInputStream 같이 사용했을 때 문제가 발생하지 않는지 확인하기
public class HttpRequestReader {

    private final InputStream inputStream;

    public HttpRequestReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest read() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = bufferedReader.readLine();
        HttpHeaders headers = readHeaderLines(bufferedReader);

        return HttpRequest.of(requestLine, headers, readBody(headers));
    }

    private HttpHeaders readHeaderLines(BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String headerLine = bufferedReader.readLine();
        while (StringUtils.isNotBlank(headerLine)) {
            headerLines.add(headerLine);
            headerLine = bufferedReader.readLine();
        }

        return HttpHeaders.from(headerLines);
    }

    private String readBody(HttpHeaders headers) {
        return headers.getFieldByHeaderName("Content-Length")
                .map(Integer::parseInt)
                .map(contentLength -> readBody(contentLength))
                .orElse("");
    }

    private String readBody(int contentLength) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] bodyBytes;

        try {
            bodyBytes = bufferedInputStream.readNBytes(contentLength);
        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP body를 읽는데 실패하였습니다.", e);
        }

        if (bodyBytes.length != contentLength) {
            throw new IllegalArgumentException("HTTP body의 길이와 Content-Type 의 값이 일치하지 않습니다.");
        }
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }
}

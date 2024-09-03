package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Http11RequestParser {

    /*
    https://www.rfc-editor.org/rfc/rfc2616#section-5 이 문서에서 이 메서드가 반환하는 것을 Request-URI라 지칭합니다.
     */
    public String parseRequestURI(String requestMessage) {
        String startLine = parseStartLine(requestMessage);
        validateStartLine(startLine);
        return startLine.split(" ")[1];
    }

    private void validateStartLine(String startLine) {
        String[] split = startLine.split(" ");
        if (split.length != 3) {
            throw new IllegalStateException("올바른 HTTP 요청이 아닙니다.");
        }
    }

    private String parseStartLine(String requestMessage) {
        return requestMessage.split("\r\n")[0];
    }

    public String readAsString(InputStream inputStream) {
        try {
            // 학습 InputStream 의 readAllBytes 는 요청 전체를 읽는다. 이때, EOF를 만날때 까지 읽으므로 EOF가 없다면 Integer.MAX_VALUE 크기만큼 읽는다.
            int readByte = inputStream.available();
            return new String(inputStream.readNBytes(readByte));
        } catch (IOException e) {
            //Todo 커스텀 예외?
            throw new IllegalArgumentException("잘못된 연결에서 읽기 요청이 발생했습니다.", e);
        }
    }

    public Http11Method parseMethod(String requestMessage) {
        String startLine = parseStartLine(requestMessage);
        String rawMethod = startLine.split(" ")[0];
        return Http11Method.valueOf(rawMethod.toUpperCase());
    }

    public List<Http11Header> parseHeaders(String requestMessage) {
        String rawHeaders = requestMessage.split("\r\n\r\n")[0]
                .replace(parseStartLine(requestMessage), "")
                .replaceFirst("\r\n", "");

        return Arrays.stream(rawHeaders.split("\r\n"))
                .filter(rawHeader -> !rawHeader.startsWith("Cookie"))
                .map(rawHeader -> {
                    String[] split = rawHeader.split(":");
                    String key = split[0].trim();
                    String value = split[1].trim();
                    return new Http11Header(key, value);
                })
                .toList();
    }

    public List<Cookie> parseCookies(String requestMessage) {
        String rawHeaders = requestMessage.split("\r\n\r\n")[0]
                .replace(parseStartLine(requestMessage), "")
                .replaceFirst("\r\n", "");

        return Arrays.stream(rawHeaders.split("\r\n"))
                .filter(rawHeader -> rawHeader.startsWith("Cookie"))
                .flatMap(rawCookies -> {
                    String[] split = rawCookies.split(";");
                    return Arrays.stream(split);
                })
                .map(String::trim)
                .map(this::parseCookie)
                .toList();
    }

    private Cookie parseCookie(String rawCookie) {
        String[] split = rawCookie.split("=");
        String key = split[0].trim();
        String value = split[1].trim();
        return new Cookie(key, value);
    }

    public LinkedHashMap<String, String> parseBody(String requestMessage) {
        int startIndex = requestMessage.indexOf("\r\n\r\n");
        if (startIndex == -1) {
            return new LinkedHashMap<>();
        }
        startIndex = startIndex + 4;
        String requestBody = requestMessage.substring(startIndex);
        if (!requestBody.contains("&")) {
            return new LinkedHashMap<>();
        }
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (String singleRequestBody : requestBody.split("&")) {
            putQueryString(singleRequestBody, result);
        }
        return result;
    }

    private void putQueryString(String singleQueryString, LinkedHashMap<String, String> result) {
        String[] split = singleQueryString.split("=");
        result.putLast(split[0], split[1]);
    }
}

package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Http11Header;

class Http11RequestParser {
    private static final String CRLF = "\r\n";

    /*
        https://www.rfc-editor.org/rfc/rfc2616#section-5 이 문서에서 이 메서드가 반환하는 것을 Request-URI라 지칭합니다.
         */

    String parseRequestURI(String requestMessage) {
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
        return requestMessage.split(CRLF)[0];
    }

    String readAsString(InputStream inputStream) {
        try {
            // 학습 InputStream 의 readAllBytes 는 요청 전체를 읽는다. 이때, EOF를 만날때 까지 읽으므로 EOF가 없다면 Integer.MAX_VALUE 크기만큼 읽는다.
            int readByte = inputStream.available();
            return new String(inputStream.readNBytes(readByte));
        } catch (IOException e) {
            //Todo 커스텀 예외?
            throw new IllegalArgumentException("잘못된 연결에서 읽기 요청이 발생했습니다.", e);
        }
    }

    Http11Method parseMethod(String requestMessage) {
        String startLine = parseStartLine(requestMessage);
        String rawMethod = startLine.split(" ")[0];
        try {

            return Http11Method.valueOf(rawMethod.toUpperCase());
        } catch (Exception e) {
            return Http11Method.GET;
        }
    }

    List<Http11Header> parseHeaders(String requestMessage) {
        String rawHeaders = requestMessage.split(CRLF + CRLF)[0]
                .replace(parseStartLine(requestMessage), "")
                .replaceFirst(CRLF, "");

        return Arrays.stream(rawHeaders.split(CRLF))
                .filter(rawHeader -> !rawHeader.startsWith("Cookie"))
                .map(rawHeader -> {
                    String[] split = rawHeader.split(":");
                    String key = split[0].trim();
                    String value = split[1].trim();
                    return new Http11Header(key, value);
                })
                .toList();
    }

    List<Cookie> parseCookies(String requestMessage) {
        String rawHeaders = requestMessage.replace(parseStartLine(requestMessage), "")
                .replaceFirst(CRLF, "");

        return Arrays.stream(rawHeaders.split(CRLF))
                .filter(rawHeader -> rawHeader.startsWith("Cookie"))
                .map(this::removeHeaderKey)
                .flatMap(rawCookies -> {
                    String[] split = rawCookies.split(";");
                    return Arrays.stream(split);
                })
                .map(String::trim)
                .map(this::parseCookie)
                .toList();
    }

    private String removeHeaderKey(String rawHeader) {
        int startIndex = rawHeader.indexOf(":") + 1;
        String headerKeyRemoved = rawHeader.substring(startIndex);
        return headerKeyRemoved.trim();
    }

    private Cookie parseCookie(String rawCookie) {
        String[] split = rawCookie.split("=");
        String key = split[0].trim();
        String value = split[1].trim();
        return new Cookie(key, value);
    }

    LinkedHashMap<String, String> parseBody(String requestMessage) {
        int startIndex = requestMessage.indexOf(CRLF + CRLF);
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

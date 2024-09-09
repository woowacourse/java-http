package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

class Http11RequestParser {

    private static final String CRLF = "\r\n";

    String readAsString(InputStream inputStream) {
        try {
            // 학습 InputStream 의 readAllBytes 는 요청 전체를 읽는다. 이때, EOF를 만날때 까지 읽으므로 EOF가 없다면 Integer.MAX_VALUE 크기만큼 읽는다.
            // readNBytes는 일정 크기만큼 읽는다. 그런데, 정확히 읽어야 하는 길이를 알아내기 어렵다.
            // read(byte[])는 읽을 수 있는 만큼 읽어서 배열에 넣는다. 반환값은 읽은 크기다. 따라서 이를 사용해야 제대로 읽어올 수 있다.
            byte[] data = new byte[100000];
            int read = inputStream.read(data);
            return new String(data, 0, read);
        } catch (IOException e) {
            throw new IllegalArgumentException("잘못된 연결에서 읽기 요청이 발생했습니다.", e);
        }
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

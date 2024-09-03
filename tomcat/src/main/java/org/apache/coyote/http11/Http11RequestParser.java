package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;

public class Http11RequestParser {

    /*
    https://www.rfc-editor.org/rfc/rfc2616#section-5 이 문서에서 이 메서드가 반환하는 것을 Request-URI라 지칭합니다.
     */
    public String parseRequestURI(InputStream inputStream) {
        String startLine = parseStartLine(inputStream);
        validateStartLine(startLine);
        return startLine.split(" ")[1];
    }

    private void validateStartLine(String startLine) {
        String[] split = startLine.split(" ");
        if (split.length != 3) {
            throw new IllegalStateException("올바른 HTTP 요청이 아닙니다.");
        }
    }

    public String parseStartLine(InputStream inputStream) {
        String allHttpRequest = readAsString(inputStream);
        return allHttpRequest.split("\r\n")[0];
    }

    private String readAsString(InputStream inputStream) {
        try {
            // 학습 InputStream 의 readAllBytes 는 요청 전체를 읽는다. 이때, EOF를 만날때 까지 읽으므로 EOF가 없다면 Integer.MAX_VALUE 크기만큼 읽는다.
            int readByte = inputStream.available();
            return new String(inputStream.readNBytes(readByte));
        }catch (IOException e) {
            //Todo 커스텀 예외?
            throw new IllegalArgumentException("잘못된 연결에서 읽기 요청이 발생했습니다.",e);
        }
    }
}

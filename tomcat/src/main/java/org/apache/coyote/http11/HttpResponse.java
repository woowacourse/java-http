package org.apache.coyote.http11;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record HttpResponse(
        HttpStatusLine statusLine,
        String contentType,
        byte[] responseBody
) {
    private static final Charset HEADER_CHARSET = StandardCharsets.US_ASCII;
    private static final String CRLF = "\r\n";
    private static final String CONTENT_TYPE_KEY = "Content-Type: ";
    private static final String CONTENT_LENGTH_KEY = "Content-Length: ";

    public byte[] toBytes() {
        final byte[] header = String.join(
                CRLF,
                statusLine + " ",   // Http11ProcessorTest에서 줄 끝에 공백이 있는 형식을 기대한다.
                CONTENT_TYPE_KEY  + contentType + " ",
                CONTENT_LENGTH_KEY  + responseBody.length + " "
        ).concat(CRLF + CRLF).getBytes(HEADER_CHARSET);   // 마지막 헤더 줄 끝 + 헤더 끝을 알리는 빈 줄

        final byte[] response = Arrays.copyOf(header, header.length + responseBody.length);
        System.arraycopy(responseBody, 0, response, header.length, responseBody.length);

        return response;
    }
}

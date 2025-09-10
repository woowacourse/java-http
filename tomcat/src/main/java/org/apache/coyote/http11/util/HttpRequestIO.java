package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.RequestHeaders;

public final class HttpRequestIO {
    private HttpRequestIO() {
    }

    /**
     * 헤더 바이트를 ISO-8859-1로 문자열화하고, 그 문자열을 파싱용 BufferedReader로 감싸 반환
     */
    public static BufferedReader createHeaderReader(InputStream inputStream) throws IOException {
        byte[] headerBytes = readHeaderBytes(inputStream);
        String headerText = new String(headerBytes, StandardCharsets.ISO_8859_1);
        return new BufferedReader(new StringReader(headerText));
    }

    /**
     * \r\n\r\n(헤더 종료)까지 바이트를 읽어 반환
     */
    private static byte[] readHeaderBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream headerBuffer = new ByteArrayOutputStream(1024);
        int previousByte3 = -1, previousByte2 = -1, previousByte1 = -1, currentByte;
        while ((currentByte = inputStream.read()) != -1) {
            headerBuffer.write(currentByte);
            if (previousByte3 == '\r' && previousByte2 == '\n' &&
                previousByte1 == '\r' && currentByte == '\n') {
                break;
            }
            previousByte3 = previousByte2;
            previousByte2 = previousByte1;
            previousByte1 = currentByte;
        }
        return headerBuffer.toByteArray();
    }

    /**
     * Content-Length 헤더 기반으로 바디를 읽어 charset으로 디코드
     */
    public static String readRequestBody(RequestHeaders requestHeaders,
                                         InputStream inputStream) throws IOException {
        String contentLengthHeader = requestHeaders.getHeader("Content-Length");
        String contentType = requestHeaders.getHeader("Content-Type");
        Charset bodyCharset = HttpHeaderUtils.decideCharset(contentType);

        if (contentLengthHeader == null || contentLengthHeader.isBlank()) {
            return "";
        }
        int expectedLength = Integer.parseInt(contentLengthHeader);
        if (expectedLength <= 0) {
            return "";
        }

        byte[] bodyBytes = readExactBytes(inputStream, expectedLength);
        return new String(bodyBytes, bodyCharset);
    }

    /**
     * 정확히 expectedLength 바이트를 읽어 반환(부분 읽기 대비)
     */
    private static byte[] readExactBytes(InputStream inputStream, int expectedLength) throws IOException {
        byte[] buffer = new byte[expectedLength];
        int totalBytesRead = 0;
        while (totalBytesRead < expectedLength) {
            int bytesRead = inputStream.read(buffer, totalBytesRead, expectedLength - totalBytesRead);
            if (bytesRead == -1) {
                throw new IOException("Unexpected EOF while reading request body");
            }
            totalBytesRead += bytesRead;
        }
        return buffer;
    }
}

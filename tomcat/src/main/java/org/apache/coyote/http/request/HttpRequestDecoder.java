package org.apache.coyote.http.request;

import java.io.InputStream;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpHeaderConverter;
import org.apache.coyote.util.ByteUtil;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.apache.coyote.http.ContentType.APPLICATION_X_WWW_FORM_URL_ENCODED;

public class HttpRequestDecoder {

    private static final int MAXIMUM_BUFFER_LEN = 8192;
    private static final String REQUEST_LINE_HEADER_DELIMITER = "HTTP/1.1\r\n";
    private static final String HEADER_BODY_DELIMITER = "\r\n\r\n";

    public HttpRequest decode(InputStream inputStream) {
        HttpRequestLine httpRequestLine = decodeRequestLine(inputStream);
        HttpHeader httpHeader = decodeHeader(inputStream);
        Map<String, String> parameters = decodeBody(httpHeader.getContentLength(),
            httpHeader.getContentType(), inputStream);

        return new HttpRequest(httpRequestLine, httpHeader, parameters);
    }

    private HttpRequestLine decodeRequestLine(InputStream inputStream) {
        byte[] source = new byte[MAXIMUM_BUFFER_LEN];
        byte[] target = REQUEST_LINE_HEADER_DELIMITER.getBytes(StandardCharsets.UTF_8);
        int sourceLength = ByteUtil.readStreamUntilEndsWith(inputStream, source, target);

        return HttpRequestLine.decode(new String(source, 0, sourceLength));
    }

    private HttpHeader decodeHeader(InputStream inputStream) {
        byte[] source = new byte[MAXIMUM_BUFFER_LEN];
        byte[] target = HEADER_BODY_DELIMITER.getBytes(StandardCharsets.UTF_8);
        int sourceLength = ByteUtil.readStreamUntilEndsWith(inputStream, source, target);

        return HttpHeaderConverter.decode(new String(source, 0, sourceLength));
    }

    private Map<String, String> decodeBody(int contentLength, ContentType contentType,
        InputStream inputStream) {
        if (contentLength <= 0) {
            return Collections.emptyMap();
        }
        if (contentType != APPLICATION_X_WWW_FORM_URL_ENCODED) {
            throw new IllegalArgumentException("지원 되지 않는 타입입니다. 타입: " + contentType.value);
        }

        byte[] source = new byte[contentLength];
        ByteUtil.readStreamOfLength(inputStream, source, contentLength);
        String body = new String(source, 0, contentLength);

        return HttpParameterDecoder.decode(body);
    }
}

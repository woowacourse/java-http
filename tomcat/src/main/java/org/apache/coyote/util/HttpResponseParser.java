package org.apache.coyote.util;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import com.spring.http.response.HttpResponse;

public final class HttpResponseParser {

    private static final String CRLF = "\r\n";

    private HttpResponseParser() {
    }

    public static byte[] parse(HttpResponse httpResponse) {
        StringJoiner joiner = new StringJoiner(CRLF);
        joiner.add(httpResponse.getStartLine() + " ");
        joiner.add(parseHeader(httpResponse));
        final byte[] bytes = (joiner + CRLF).getBytes(StandardCharsets.US_ASCII);

        if (httpResponse.getBody() != null) {
            return concatBytes(bytes, httpResponse.getBody());
        }

        return bytes;
    }

    private static String parseHeader(HttpResponse httpResponse) {
        return httpResponse.getHeaderString();
    }

    private static byte[] concatBytes(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}

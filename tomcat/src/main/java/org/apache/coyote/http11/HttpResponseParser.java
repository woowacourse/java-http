package org.apache.coyote.http11;

import java.util.StringJoiner;
import org.apache.catalina.domain.HttpResponse;

public final class HttpResponseParser {

    private static final String CRLF = "\r\n";

    private HttpResponseParser() {
    }

    public static byte[] parse(HttpResponse httpResponse) {
        StringJoiner joiner = new StringJoiner(CRLF);
        joiner.add(httpResponse.getStartLine());
        joiner.add(parseHeader(httpResponse));
        final byte[] bytes = (joiner + CRLF).getBytes();

        if (httpResponse.getBody() != null) {
            return concat(bytes, httpResponse.getBody());
        }

        return bytes;
    }

    private static String parseHeader(HttpResponse httpResponse) {
        StringBuilder builder = new StringBuilder();
        httpResponse.getHeaders()
                .forEach((key, value) -> builder.append(key).append(": ").append(value).append(" \r\n"));
        return builder.toString();
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}

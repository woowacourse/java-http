package org.apache.coyote;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    private enum MediaTypes {

        HTML(List.of(".html", ".htm"), "text/html;charset=utf-8"),
        CSS(List.of(".css"), "text/css"),
        JAVASCRIPT(List.of(".js"), "application/javascript"),
        GIF(List.of(".gif"), "image/gif"),
        JPG(List.of(".jpg", ".jpeg"), "image/jpeg"),
        PNG(List.of(".png"), "image/png"),
        SVG(List.of(".svg"), "image/svg+xml"),
        DEFAULT(List.of(""), "text/plain");

        private final List<String> fileExtensions;
        private final String type;

        MediaTypes(final List<String> fileExtensions, final String type) {
            this.fileExtensions = fileExtensions;
            this.type = type;
        }

        public static String findMediaType(final String uri) {
            return Arrays.stream(MediaTypes.values())
                    .filter(code -> isMatchingExtension(code.fileExtensions, uri))
                    .findAny()
                    .orElse(DEFAULT)
                    .type;
        }

        private static boolean isMatchingExtension(final List<String> extensions, final String uri) {
            for (String extension : extensions) {
                if (uri.endsWith(extension)) {
                    return true;
                }
            }
            return false;
        }
    }

    public byte[] build(final String requestUri, final HttpStatus status, final byte[] body,
                        final Map<String, String> headers) {
        StringBuilder builder = new StringBuilder();

        builder.append("HTTP/1.1 ").append(status.getName()).append(" \r\n");

        if (requestUri != null) {
            String contentType = getContentType(requestUri);
            builder.append("Content-Type: ").append(contentType).append(" \r\n");
        }
        if (headers != null) {
            headers.forEach((key, value) -> builder.append(key).append(": ").append(value).append(" \r\n"));
        }
        if (body == null) {
            return builder.toString().getBytes();
        }

        builder.append("Content-Length: ").append(body.length).append(" \r\n");
        builder.append("\r\n");

        byte[] messageBytes = builder.toString().getBytes();
        byte[] result = new byte[messageBytes.length + body.length];

        System.arraycopy(messageBytes, 0, result, 0, messageBytes.length);
        System.arraycopy(body, 0, result, messageBytes.length, body.length);

        return result;
    }

    private String getContentType(final String uri) {
        return MediaTypes.findMediaType(uri);
    }
}

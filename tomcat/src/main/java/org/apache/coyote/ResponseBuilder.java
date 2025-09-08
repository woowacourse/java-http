package org.apache.coyote;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseBuilder {

    private enum MediaTypes {

        HTML(List.of(".html", ".htm"), "text/html;charset=utf-8"),
        CSS(List.of(".css"), "text/css"),
        JAVASCRIPT(List.of(".js"), "application/javascript"),
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

    public String build(final String requestUri, final String status, final byte[] body, final Map<String, String> headers) {
        String contentType = getContentType(requestUri);

        if (headers == null) {
            return String.join("\r\n",
                    "HTTP/1.1 " + status + " ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + body.length + " ",
                    "",
                    new String(body));
        }

        String responseHeaders = headers.entrySet()
                .stream()
                .map(set -> set.getKey() + ": " + set.getValue())
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                responseHeaders,
                "",
                new String(body));
    }

    private String getContentType(final String uri) {
        return MediaTypes.findMediaType(uri);
    }
}

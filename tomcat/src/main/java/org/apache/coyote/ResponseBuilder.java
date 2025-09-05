package org.apache.coyote;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    private enum ContentTypes {

        HTML(List.of(".html", ".htm"), "text/html;charset=utf-8"),
        CSS(List.of(".css"), "text/css"),
        JAVASCRIPT(List.of(".js"), "application/javascript"),
        DEFAULT(List.of(""), "text/html;charset=utf-8");

        private final List<String> fileExtensions;
        private final String type;

        ContentTypes(final List<String> fileExtensions, final String type) {
            this.fileExtensions = fileExtensions;
            this.type = type;
        }

        public static String findContentType(final String uri) {
            return Arrays.stream(ContentTypes.values())
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

    public String build(final String requestUri, final Map<String, String> headers, final byte[] body) {
        String contentType = getContentType(requestUri);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "",
                new String(body));
    }

    private String getContentType(final String uri) {
        return ContentTypes.findContentType(uri);
    }
}

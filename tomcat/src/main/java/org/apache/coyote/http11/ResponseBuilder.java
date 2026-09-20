package org.apache.coyote.http11;

public class ResponseBuilder {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    public String build(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String resolveContentType(String accept) {
        if (accept == null || accept.isBlank()) {
            return DEFAULT_CONTENT_TYPE;
        }

        String preferred = accept.split(",")[0]
                .split(";")[0]
                .trim();

        if (preferred.isEmpty() || preferred.equals("*/*")) {
            return DEFAULT_CONTENT_TYPE;
        }

        return preferred;
    }
}

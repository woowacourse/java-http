package org.apache.coyote.http11.fixture;

public class HttpResponseFixture {

    private static final String CRLF = "\r\n";

    private HttpResponseFixture() {
    }

    public static String getGetResponse(String body, String mediaType) {
        return String.join(
                CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mediaType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body
        );
    }

    public static String getGetResponse(String body, String mediaType, String redirectPath) {
        return String.join(
                CRLF,
                "HTTP/1.1 302 FOUND ",
                "Content-Type: " + mediaType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "Location: " + redirectPath + " ",
                "",
                body
        );
    }

    public static String getPostRedirectResponse(String body, String redirectPath) {
        return String.join(
                CRLF,
                "HTTP/1.1 302 FOUND ",
                "Location: " + redirectPath + " ",
                "",
                body
        );
    }
}

package org.apache.coyote.http;


import static org.apache.coyote.page.PageMapper.getPath;

import java.util.Map;

public class HttpResponse {

    private HttpStatus httpStatus;
    private Header header;
    private String body;
    private HttpCookie cookie;

    public HttpResponse(final HttpStatus httpStatus,
                        final Map<String, String> header,
                        final String body,
                        final HttpCookie cookie) {
        this.httpStatus = httpStatus;
        this.header = new Header(header);
        this.body = body;
        this.cookie = cookie;
    }

    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder(HttpStatus.OK);
    }

    public static HttpResponseBuilder notFound() {
        return new HttpResponseBuilder(HttpStatus.NOT_FOUND);
    }

    public static HttpResponseBuilder created(final Long id, final String redirectUrl){
        return new HttpResponseBuilder(HttpStatus.CREATED)
                .header("Location", String.valueOf(id))
                .body(getPath(redirectUrl));
    }

    public static HttpResponseBuilder redirect(final String url) {
        return new HttpResponseBuilder(HttpStatus.FOUND)
                .header("Location", url)
                .body(getPath(url));
    }

    public String getHttpStatus() {
        return httpStatus.getCode() + " " + httpStatus.getMessage();
    }

    public Header getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String makeResultMessage() {
        return String.join("\r\n",
                "HTTP/1.1 " + this.getHttpStatus() + " ",
                this.getHeader().getHeaderMapForMessage(),
                "Content-Length: " + this.getBody().length() + " ",
                "",
                this.getBody());
    }
}

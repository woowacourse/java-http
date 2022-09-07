package org.apache.coyote.http;


import static org.apache.coyote.page.PageMapper.getFilePath;
import static org.apache.coyote.page.PageMapper.getPath;

import java.util.Map;

public class HttpResponse {

    private HttpStatus httpStatus;
    private Header header;
    private String body;

    public HttpResponse(final HttpStatus httpStatus, final Map<String, String> header, final String body) {
        this.httpStatus = httpStatus;
        this.header = new Header(header);
        this.body = body;
    }

    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder(HttpStatus.OK);
    }

    public static HttpResponseBuilder notFound() {
        return new HttpResponseBuilder(HttpStatus.NOT_FOUND);
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
}

package org.apache.coyote.http11;

public class HttpResponse {

    private HttpResponseHeader headers;
    private HttpStatusCode statusCode;
    private String body;

    public static HttpResponse builder() {
        return new HttpResponse();
    }

    public HttpResponse statusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse responseBody(String body) {
        HttpResponseHeader headers = new HttpResponseHeader();
        headers.addHeader("Content-Type", "text/html;charset=utf-8");
        headers.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        this.headers = headers;
        this.body = body;
        return this;
    }

    public String build() {
        return String.join("\r\n",
                statusCode.buildOutput(),
                headers.buildOutput(),
                "",
                body
        );
    }
}

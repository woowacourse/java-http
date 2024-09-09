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

    public HttpResponse staticResource(String path) {
        StaticResourceLoader loader = new StaticResourceLoader();
        String resource = loader.load(path);
        if (resource.isEmpty()) {
            String notFoundResource = loader.load("/404.html");
            return this.statusCode(HttpStatusCode.NOT_FOUND)
                    .responseBody(notFoundResource);
        }

        HttpResponseHeader headers = new HttpResponseHeader();
        HttpContentType contentType = HttpContentType.matchContentType(path);
        headers.addHeader("Content-Type", contentType.getContentType() + ";charset=utf-8");
        headers.addHeader("Content-Length", String.valueOf(resource.getBytes().length));

        this.headers = headers;
        this.body = resource;
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

    public HttpResponse redirect(String uri) {
        this.body = "";
        HttpResponseHeader headers = new HttpResponseHeader();
        headers.addHeader("Location", uri);
        headers.addHeader("Content-Type", "text/html;charset=utf-8");
        headers.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        this.headers = headers;
        return this;
    }
}

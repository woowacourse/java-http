package org.apache.coyote.http11;

public class HttpRequestHandler {

    public HttpResponse handle(RequestLine line) {
        // Method, Path, Body
        if (line.getMethod().equals("GET") && line.getUrl().equals("/")) {
            return rootPage();
        }
        return staticPage(line.getUrl());
    }

    private HttpResponse rootPage() {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .responseBody("Hello world!");
    }

    private HttpResponse staticPage(String url) {
        StaticResourceLoader loader = new StaticResourceLoader();
        String resource = loader.load(url);
        if (resource.isEmpty()) {
            String notFoundResource = loader.load("/404.html");
            return HttpResponse.builder()
                    .statusCode(HttpStatusCode.NOT_FOUND)
                    .responseBody(notFoundResource);
        }
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .responseBody(resource);
    }
}

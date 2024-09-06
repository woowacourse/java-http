package org.apache.coyote.http11;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        byte[] resource = StaticResourceLoader.load(request.getUri());
        String extension = request.getUri().toString()
                .substring(request.getUri().toString().lastIndexOf(".") + 1);
        String responseBody = new String(resource);
        return HttpResponse.builder()
                .ok()
                .contentType(ContentType.fromExtension(extension))
                .body(responseBody)
                .build();
    }
}

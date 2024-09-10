package hoony.was;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceLoader;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        byte[] resource = StaticResourceLoader.load(request.getUri().getPath());
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

package hoony.was;

import java.net.URI;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET;
    }

    @Override
    public String handle(HttpRequest request, HttpResponse response) {
        URI uri = request.getUri();
        return uri.getPath();
    }
}

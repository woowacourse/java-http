package customservlet;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public interface RequestHandler {
    String handle(HttpRequest request, HttpResponse response);
}

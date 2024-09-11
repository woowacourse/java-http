package hoony.was;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface RequestHandler {

    boolean canHandle(HttpRequest request);

    Object handle(HttpRequest request, HttpResponse response);
}

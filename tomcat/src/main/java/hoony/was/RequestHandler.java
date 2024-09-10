package hoony.was;

import org.apache.coyote.http11.HttpRequest;

public interface RequestHandler {

    boolean canHandle(HttpRequest request);

    Object handle(HttpRequest request);
}

package servlet.handler;

import org.apache.coyote.http11.request.HttpRequest;
import servlet.mapping.ResponseEntity;

public interface Controller {
    ResponseEntity service(HttpRequest request);

    String getPath();
}

package servlet.mapping;

import org.apache.coyote.http11.request.HttpRequest;

public interface HandlerMapping {

    ResponseEntity map(HttpRequest request);
}

package servlet.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.Path;
import servlet.mapping.ResponseEntity;

public class ResourceFinder extends AbstractController {

    public ResourceFinder() {
        super("");
    }

    @Override
    protected ResponseEntity doGet(HttpRequest request) {
        Path path = request.getPath();
        return ResponseEntity.ok(path.getPath());
    }
}

package mvc.controller.mapping;

import java.util.List;
import mvc.controller.AbstractPathController;
import mvc.controller.mapping.exception.EmptyControllerException;
import mvc.controller.mapping.exception.UnsupportedHttpRequestException;
import org.apache.coyote.http.request.HttpRequest;
import servlet.Controller;

public class RequestMapping {

    private final List<AbstractPathController> mappings;

    public RequestMapping(final List<AbstractPathController> mappings) {
        validateMappings(mappings);

        this.mappings = mappings;
    }

    private void validateMappings(final List<AbstractPathController> mappings) {
        if (mappings.isEmpty()) {
            throw new EmptyControllerException();
        }
    }

    public Controller getController(final HttpRequest request) {
        for (final AbstractPathController controller : mappings) {
            if (controller.supports(request)) {
                return controller;
            }
        }
        throw new UnsupportedHttpRequestException();
    }
}

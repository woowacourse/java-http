package nextstep.jwp;

import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.ResourceURI;

public class ViewResolver {

    private static final String HTML_EXTENSION = ".html";
    private static final String RESOURCE_SEPARATOR = "/";
    private static final String PREFIX_EXTENSION = ".";

    public void resolve(final String viewName, final HttpResponse response) {
        if (viewName.contains(PREFIX_EXTENSION)) {
            final var resourceURI = ResourceURI.from(viewName);
            response.setResourceURI(resourceURI);
            return;
        }
        final var resourceURI = ResourceURI.from(RESOURCE_SEPARATOR + viewName + HTML_EXTENSION);
        response.setResourceURI(resourceURI);
    }
}

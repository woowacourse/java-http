package customservlet;

import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.ResourceUri;

public class ViewResolver {

    private static final String HTML_EXTENSION = ".html";
    private static final String RESOURCE_SEPARATOR = "/";
    private static final String PREFIX_EXTENSION = ".";

    public void resolve(final String viewName, final HttpResponse response) {
        if (viewName.contains(PREFIX_EXTENSION)) {
            final var resourceUri = ResourceUri.from(viewName);
            response.setResourceUri(resourceUri);
            return;
        }
        final var resourceURI = ResourceUri.from(RESOURCE_SEPARATOR + viewName + HTML_EXTENSION);
        response.setResourceUri(resourceURI);
    }
}

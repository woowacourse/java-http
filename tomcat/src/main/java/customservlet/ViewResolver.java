package customservlet;

import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.Location;
import org.apache.coyote.http11.http.ResourceUri;
import org.apache.coyote.http11.util.HttpStatus;

public class ViewResolver {

    private static final String HTML_EXTENSION = ".html";
    private static final String RESOURCE_SEPARATOR = "/";
    private static final String PREFIX_EXTENSION = ".";

    public void resolve(final String viewName, final HttpResponse response) {
        if (response.getHttpStatus() == HttpStatus.FOUND) {
            setLocation(viewName, response);
            return;
        }
        setResourceUri(viewName, response);
    }

    private void setLocation(final String viewName, final HttpResponse response) {
        final var resourceURI = Location.from(RESOURCE_SEPARATOR + viewName + HTML_EXTENSION);
        response.setLocation(resourceURI);
    }

    private void setResourceUri(final String viewName, final HttpResponse response) {
        if (viewName.contains(PREFIX_EXTENSION)) {
            final var resourceUri = ResourceUri.from(viewName);
            response.setResourceUri(resourceUri);
            return;
        }
        final var resourceURI = ResourceUri.from(RESOURCE_SEPARATOR + viewName + HTML_EXTENSION);
        response.setResourceUri(resourceURI);
    }
}

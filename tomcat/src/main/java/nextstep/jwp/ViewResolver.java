package nextstep.jwp;

import org.apache.coyote.http11.http.HttpResponse;

public class ViewResolver {

    private static final String HTML_EXTENSION = ".html";
    private static final String RESOURCE_SEPARATOR = "/";
    private static final String PREFIX_EXTENSION = ".";

    public void resolve(final String viewName, final HttpResponse response) {
        if (viewName.contains(PREFIX_EXTENSION)) {
            response.setResourceURI(viewName);
            return;
        }
        response.setResourceURI(RESOURCE_SEPARATOR + viewName + HTML_EXTENSION);
    }
}

package org.apache.coyote.handler;

import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.util.ResourceUtil;

public class ViewResolver {

    private static final String DEFAULT_PATH = "/";
    private static final String REDIRECT = "redirect:";
    private static final String EXTENSION = ".html";

    public void resolve(final String path, final HttpResponse response) {
        if (path.startsWith(REDIRECT)) {
            doRedirect(path, response);
            return;
        }
        doResolve(path, response);
    }

    private void doRedirect(final String path, final HttpResponse response) {
        int index = path.indexOf(DEFAULT_PATH);
        String requestUri = createUri(path.substring(index + 1));
        response.setHttpStatus(HttpStatus.FOUND)
                .setContentType(getContentType(requestUri))
                .setLocation(requestUri);
    }

    private void doResolve(final String path, final HttpResponse response) {
        String uri = createUri(path);
        response.setHttpStatus(HttpStatus.OK)
                .setContentType(getContentType(uri))
                .setResponseBody(getResource(uri));
    }

    private String createUri(final String path) {
        return DEFAULT_PATH + path + EXTENSION;
    }

    private String getContentType(final String uri) {
        return ResourceUtil.getContentType(uri);
    }

    private String getResource(final String uri) {
        return ResourceUtil.getResource(uri);
    }
}

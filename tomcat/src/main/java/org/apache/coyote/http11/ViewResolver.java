package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.util.ResourceUtil;

public class ViewResolver {

    private static final String DEFAULT_PATH = "/";
    private static final String REDIRECT = "redirect:";
    private static final String EXTENSION = ".html";
    private static final String CONTENT_TYPE = "text/html";

    public ViewResolver() {
    }

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
        System.out.println(requestUri);
        response.setHttpStatus(HttpStatus.FOUND)
                .setContentType(getContentType(requestUri))
                .setLocation(requestUri);
    }

    private void doResolve(final String path, final HttpResponse response) {
        String uri = createUri(path);
        System.out.println("uri :" + uri);
        response.setHttpStatus(HttpStatus.OK)
                .setContentType(getContentType(uri))
                .setResponseBody(getResource(uri));
    }

    private String createUri(final String substring) {
        return DEFAULT_PATH + substring + EXTENSION;
    }

    private String getContentType(final String uri) {
        try {
            return Files.probeContentType(ResourceUtil.getPath(uri));
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getResource(final String uri) {
        return ResourceUtil.getResource(uri);
    }
}

package org.apache.coyote.handle.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.Accept;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class FileHandler implements Handler {

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final String contentType = getContentType(httpRequest);
        final String body = getBody(httpRequest);

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setContentType(contentType);
        httpResponse.setContent(body);
    }

    private String getContentType(final HttpRequest httpRequest) {
        final List<Accept> accepts = httpRequest.getAccepts();
        String contentType = ContentType.getTypeFrom(accepts);
        if (contentType == null) {
            final String uriPath = httpRequest.getUriPath();
            final String extension = uriPath.substring(uriPath.lastIndexOf(".") + 1);
            contentType = ContentType.getTypeFrom(extension);
        }
        return contentType;
    }

    private String getBody(final HttpRequest httpRequest) throws IOException {
        final String uriPath = httpRequest.getUriPath();
        final URL resource = ClassLoader.getSystemClassLoader().getResource(STATIC_DIRECTORY + uriPath);
        final File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
    }
}

package kokodak.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public class ResourceHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String path = httpRequest.getRequestTarget().getPath();
        final String fileName = "static" + path;
        final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
        final String responseBody = new String(Files.readAllBytes(new File(resourceUrl.getPath()).toPath()));
        final String contentType = decideContentType(httpRequest);
        return HttpResponse.builder()
                           .header("Content-Type", contentType)
                           .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                           .body(responseBody)
                           .build();
    }

    private String decideContentType(final HttpRequest httpRequest) {
        final String lastPathSnippet = getLastPathSnippet(httpRequest.getRequestTarget().getPath());
        final String fileNameExtension = lastPathSnippet.substring(lastPathSnippet.indexOf(".") + 1);
        final String accept = httpRequest.header("Accept");
        if (accept.contains("text/css") || "css".equals(fileNameExtension)) {
            return "text/css;charset=utf-8";
        }
        if (accept.contains("text/javascript") || accept.contains("application/javascript") || "js".equals(fileNameExtension)) {
            return "application/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private String getLastPathSnippet(final String path) {
        final String[] pathSnippets = path.split("/");
        return pathSnippets[pathSnippets.length - 1];
    }

    @Override
    public List<Class<? extends Argument>> requiredArguments() {
        return Collections.emptyList();
    }

    @Override
    public void setArguments(final List<Argument> arguments) {
        throw new UnsupportedOperationException();
    }
}

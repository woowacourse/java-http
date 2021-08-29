package nextstep.jwp.handler;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import nextstep.jwp.ServerConfig;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.SourcePath;

public class ResourceHandler implements Handler {

    private static final String RESOURCE_BASE_PATH = ServerConfig.RESOURCE_BASE_PATH;

    @Override
    public boolean mapping(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.requestLine();
        return (httpRequest.isGet() && isExistResource(requestLine.sourcePath()));
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.requestLine();
        SourcePath sourcePath = requestLine.sourcePath();
        return ResponseEntity.ok(sourcePath.getValue());
    }

    private boolean isExistResource(SourcePath sourcePath) {
        final URL resourceUrl = getClass().getResource(RESOURCE_BASE_PATH + sourcePath.getValue());
        if (Objects.isNull(resourceUrl)) {
            return false;
        }
        return new File(resourceUrl.getFile()).exists();
    }
}

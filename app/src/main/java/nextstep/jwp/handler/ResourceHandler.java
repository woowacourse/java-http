package nextstep.jwp.handler;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.SourcePath;

public class ResourceHandler implements Handler {

    // TODO :: Config 분리
    private static final String RESOURCE_BASE_PATH = "/static";

    @Override
    public boolean mapping(RequestLine requestLine) {
        String method = requestLine.method();
        SourcePath sourcePath = requestLine.sourcePath();
        return (method.equalsIgnoreCase("GET") && isExistResource(sourcePath));
    }

    @Override
    public ModelAndView service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.requestLine();
        SourcePath sourcePath = requestLine.sourcePath();
        return ModelAndView.ok(sourcePath.getValue());
    }

    private boolean isExistResource(SourcePath sourcePath) {
        final URL resourceUrl = getClass().getResource(RESOURCE_BASE_PATH + sourcePath.getValue());
        if (Objects.isNull(resourceUrl)) {
            return false;
        }
        return new File(resourceUrl.getFile()).exists();
    }
}

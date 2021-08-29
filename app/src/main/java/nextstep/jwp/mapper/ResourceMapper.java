package nextstep.jwp.mapper;

import nextstep.jwp.exception.IncorrectMapperException;
import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.SourcePath;

import java.io.File;
import java.net.URL;

public class ResourceMapper implements HandlerMapper {

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
        RequestLine requestLine = httpRequest.getRequestLine();
        String method = requestLine.method();
        SourcePath sourcePath = requestLine.sourcePath();

        if (method.equalsIgnoreCase("GET") && isExistResource(sourcePath)) {
            return ModelAndView.ok(sourcePath.getValue());
        }
        throw new IncorrectMapperException();
    }

    private boolean isExistResource(SourcePath sourcePath) {
        final String fileName = sourcePath.getValue();
        final URL resourceUrl = getClass().getResource(RESOURCE_BASE_PATH + fileName);
        return new File(resourceUrl.getFile()).exists();
    }
}

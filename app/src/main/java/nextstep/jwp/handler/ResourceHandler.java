package nextstep.jwp.handler;

import nextstep.jwp.ServerConfig;
import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.SourcePath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

import java.net.URL;
import java.util.Objects;

public class ResourceHandler implements Handler {

    private static final String RESOURCE_BASE_PATH = ServerConfig.RESOURCE_BASE_PATH;

    @Override
    public boolean mapping(HttpRequest httpRequest) {
        return (httpRequest.isGet() && isExistResource(httpRequest.sourcePath()));
    }

    @Override
    public ModelAndView service(HttpRequest httpRequest, HttpResponse httpResponse) {
        SourcePath sourcePath = httpRequest.sourcePath();
        if (isExistResource(sourcePath)) {
            return ModelAndView.of(sourcePath.getValue(), HttpStatus.OK);
        }
        throw new IncorrectHandlerException();
    }

    public boolean isExistResource(SourcePath sourcePath) {
        final URL resourceUrl = getClass().getResource(RESOURCE_BASE_PATH + sourcePath.getValue());
        return !Objects.isNull(resourceUrl);
    }
}

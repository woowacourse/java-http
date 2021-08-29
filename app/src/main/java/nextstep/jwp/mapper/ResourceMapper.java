package nextstep.jwp.mapper;

import java.io.File;
import java.net.URL;
import nextstep.jwp.exception.IncorrectMapperException;
import nextstep.jwp.handler.Model;
import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpStatus;

public class ResourceMapper implements HandlerMapper {

    private static final String RESOURCE_BASE_PATH = "/static";

    @Override
    public boolean mapping(RequestLine requestLine) {
        String method = requestLine.getMethod();
        String filePath = requestLine.getUriPath().getPath();
        return (method.equalsIgnoreCase("GET") && isExistResource(filePath));
    }

    @Override
    public ModelAndView service(RequestLine requestLine) {
        String method = requestLine.getMethod();
        String filePath = requestLine.getUriPath().getPath();

        if (method.equalsIgnoreCase("GET") && isExistResource(filePath)) {
            return new ModelAndView(Model.of(HttpStatus.OK), filePath);
        }
        throw new IncorrectMapperException();
    }

    private boolean isExistResource(String fileName) {
        final URL resourceUrl = getClass().getResource(RESOURCE_BASE_PATH + fileName);
        return new File(resourceUrl.getFile()).exists();
    }
}

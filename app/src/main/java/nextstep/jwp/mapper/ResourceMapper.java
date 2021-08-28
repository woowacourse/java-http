package nextstep.jwp.mapper;

import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.view.View;

public class ResourceMapper implements HandlerMapper {

    @Override
    public View mapping(RequestLine requestLine) {
        String method = requestLine.getMethod();
        RequestUriPath uriPath = requestLine.getUriPath();

        if (method.equalsIgnoreCase("GET")) {
            // TODO :: 자원 존재 확인
            String path = uriPath.getPath();
            String viewPath = path.substring(1);
            return View.of(viewPath);
        }
        return View.empty();
    }
}

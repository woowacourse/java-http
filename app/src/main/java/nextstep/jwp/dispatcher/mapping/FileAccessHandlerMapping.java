package nextstep.jwp.dispatcher.mapping;

import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.message.HttpMethod;

public class FileAccessHandlerMapping implements HandlerMapping {

    @Override
    public boolean supports(HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getMethod();
        String urlPath = httpRequest.getRequestURI();
        return isFileRequest(urlPath) && isGet(method);
    }

    private boolean isFileRequest(String urlPath) {
        return urlPath.indexOf(".") != -1;
    }

    private boolean isGet(HttpMethod method) {
        return method == HttpMethod.GET;
    }

    @Override
    public Handler getHandler(HttpRequest httpRequest) {
        return null;
    }
}

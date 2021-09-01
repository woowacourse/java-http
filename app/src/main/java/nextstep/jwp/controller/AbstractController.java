package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseStatus;
import nextstep.jwp.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final String INTERNAL_SERVER_ERROR_REDIRECT_URL = "http://localhost:8080/500.html";
    private static final String NOT_FOUND_ERROR_REDIRECT_URL = "http://localhost:8080/404.html";

    private Logger logger = LoggerFactory.getLogger(AbstractController.class);

    public static String getNotFoundErrorRedirectUrl() {
        return NOT_FOUND_ERROR_REDIRECT_URL;
    }

    @Override
    public boolean isMatchingController(HttpRequest httpRequest) {
        return isMatchingUriPath(httpRequest);
    }

    protected HttpResponse renderPage(HttpRequest httpRequest, ContentType contentType) {
        try {
            String responseBody = FileUtil.readStaticFileByUriPath(httpRequest.getPath());
            return HttpResponse.status(
                ResponseStatus.OK,
                HttpHeader.getResponseHeader(responseBody, contentType, httpRequest.getHttpHeader()),
                responseBody);
        } catch (IllegalArgumentException e) {
            logger.error("ERROR! RequestURI : {}, Stack : ", httpRequest.getPath(), e);
            return redirect(INTERNAL_SERVER_ERROR_REDIRECT_URL);
        }
    }

    protected HttpResponse renderPage(HttpRequest httpRequest) {
        return renderPage(httpRequest, ContentType.HTML);
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        try {
            if (httpRequest.getHttpMethod() == HttpMethod.GET) {
                return doGet(httpRequest);
            }
            if (httpRequest.getHttpMethod() == HttpMethod.POST) {
                return doPost(httpRequest);
            }
            return redirect(NOT_FOUND_ERROR_REDIRECT_URL);
        } catch (IllegalArgumentException e) {
            logger.error("ERROR! RequestURI : {}, Stack : ", httpRequest.getPath(), e);
            return redirect(INTERNAL_SERVER_ERROR_REDIRECT_URL);
        }
    }

    protected HttpResponse redirect(String redirectUrl) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", redirectUrl);

        return HttpResponse.status(ResponseStatus.FOUND, new HttpHeader(headers));
    }

    protected abstract HttpResponse doGet(HttpRequest httpRequest);

    protected abstract HttpResponse doPost(HttpRequest httpRequest);

    abstract boolean isMatchingUriPath(HttpRequest httpRequest);
}

package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.util.FileUtil;

public abstract class AbstractController implements Controller {

    private static final String INTERNAL_SERVER_ERROR_REDIRECT_URL = "http://localhost:8080/500.html";
    private static final String NOT_FOUND_ERROR_REDIRECT_URL = "http://localhost:8080/404.html";

    @Override
    public boolean isMatchingController(HttpRequest httpRequest) {
        return isMatchingUriPath(httpRequest);
    }

    protected HttpResponse renderPage(String uriPath) {
        try {
            String responseBody = FileUtil.readStaticFileByUriPath(uriPath);
            return HttpResponse.status(ResponseStatus.OK,
                HttpHeader.getHTMLResponseHeader(responseBody),
                responseBody);
        } catch (IllegalArgumentException e) {
            return redirect(INTERNAL_SERVER_ERROR_REDIRECT_URL);
        }
    }

    protected HttpResponse applyCSSFile(String uriPath) {
        try {
            String responseBody = FileUtil.readStaticFileByUriPath(uriPath);
            return HttpResponse.status(ResponseStatus.OK,
                HttpHeader.getCSSResponseHeader(responseBody),
                responseBody);
        } catch (IllegalArgumentException e) {
            return redirect(INTERNAL_SERVER_ERROR_REDIRECT_URL);
        }
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
            return redirect(INTERNAL_SERVER_ERROR_REDIRECT_URL);
        }
    }

    protected HttpResponse redirect(String redirectUrl) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", redirectUrl);

        return HttpResponse.status(ResponseStatus.FOUND, new HttpHeader(headers));
    }

    public static String getInternalServerErrorRedirectUrl() {
        return INTERNAL_SERVER_ERROR_REDIRECT_URL;
    }

    public static String getNotFoundErrorRedirectUrl() {
        return NOT_FOUND_ERROR_REDIRECT_URL;
    }

    protected abstract HttpResponse doGet(HttpRequest httpRequest);

    protected abstract HttpResponse doPost(HttpRequest httpRequest);

    abstract boolean isMatchingUriPath(HttpRequest httpRequest);
}

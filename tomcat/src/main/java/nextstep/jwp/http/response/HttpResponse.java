package nextstep.jwp.http.response;

import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.model.StaticResource;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(final HttpStatus httpStatus,
                        final ResponseHeaders responseHeaders,
                        final ResponseBody responseBody) {
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse createBody(HttpStatus httpStatus, StaticResource staticResource) {
        ResponseHeaders responseHeaders = ResponseHeaders.createWithBody(staticResource);
        ResponseBody responseBody = new ResponseBody(staticResource.getContent());

        return new HttpResponse(httpStatus, responseHeaders, responseBody);
    }

    public static HttpResponse redirect(HttpStatus httpStatus, String location) {
        ResponseHeaders responseHeaders = ResponseHeaders.createWithDirect(location);
        ResponseBody responseBody = ResponseBody.empty();

        return new HttpResponse(httpStatus, responseHeaders, responseBody);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getHeader(String parameter) {
        return responseHeaders.getHeader(parameter);
    }

    public String getResponseBody() {
        return responseBody.getValue();
    }
}

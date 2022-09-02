package nextstep.jwp.http.response;

import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpVersion;
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

//    public static HttpResponse createRedirect(HttpStatus httpStatus, String location) {
//        ResponseHeaders responseHeaders = ResponseHeaders.createWithDirect(location);
//        ResponseBody responseBody = ResponseBody.empty();
//
//        return new HttpResponse(httpStatus, responseHeaders, responseBody);
//    }

    public String getResponse() {
        String response = String.join("\r\n",
            HttpVersion.HTTP_1_1.getValue() + " " + httpStatus.getCode() + " " + httpStatus.getDescription() + " ",
            "Content-Type: " + responseHeaders.getHeader("Content-Type") + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getValue().getBytes().length + " ",
            "",
            responseBody.getValue());

        return response;
    }
}

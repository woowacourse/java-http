package nextstep.jwp.http.response;

import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.model.StaticResource;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(final HttpStatus httpStatus,
                        final HttpHeaders httpHeaders,
                        final ResponseBody responseBody) {
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse create(HttpStatus httpStatus, HttpHeaders httpHeaders, StaticResource staticResource) {
        httpHeaders.add("Content-Type", staticResource.getContentType());
        ResponseBody responseBody = new ResponseBody(staticResource.getContent());

        return new HttpResponse(httpStatus, httpHeaders, responseBody);
    }

    public String getResponse() {
        String response = String.join("\r\n",
            "HTTP/1.1" + " " + httpStatus.getCode() + " " + httpStatus.getDescription() + " ",
            "Content-Type: " + httpHeaders.getHeader("Content-Type") + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getValue().getBytes().length + " ",
            "",
            responseBody.getValue());

        return response;
    }
}

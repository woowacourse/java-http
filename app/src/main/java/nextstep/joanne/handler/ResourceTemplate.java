package nextstep.joanne.handler;

import nextstep.joanne.converter.HttpRequestResponseConverter;
import nextstep.joanne.http.HttpStatus;
import nextstep.joanne.http.request.ContentType;
import nextstep.joanne.http.response.HttpResponse;

public class ResourceTemplate {
    /*
    HTTP/1.1 302 Found
    Location: " " + url + " "
     */

    public static HttpResponse doPage(HttpStatus httpStatus, String uri) {
        return HttpRequestResponseConverter.convertToHttpResponse(
                httpStatus,
                uri,
                ContentType.resolve(uri)
        );
    }
}

package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpMapper {

    public static HttpResponse createResponse(HttpRequest httpRequest) throws IOException {
        //todo: RequestLine의 method, url, version, RequestBody를 담고 있는 dto 클래스를 반환한다.

        if(httpRequest.containsUri("/login")) {
            new LoginInterceptor().handle(httpRequest);
        }
        return new HttpResponse(httpRequest);
    }
}

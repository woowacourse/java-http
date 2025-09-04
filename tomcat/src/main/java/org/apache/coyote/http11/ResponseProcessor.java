package org.apache.coyote.http11;

import com.http.enums.HttpStatus;
import org.apache.catalina.connector.ResponseHeaderUtil;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.domain.ResponseStartLine;

public class ResponseProcessor {

    public static void handle(HttpRequest request, HttpResponse response, HttpStatus httpStatus) {
        final String version = request.requestStartLine().version();

        response.setStartLine(new ResponseStartLine(version, httpStatus));

        ResponseHeaderUtil.handle(request, response);
    }
}

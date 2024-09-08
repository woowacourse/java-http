package org.apache.coyote.http11.response.maker;

import org.apache.coyote.http11.domain.body.ContentType;
import org.apache.coyote.http11.domain.body.ResponseBody;
import org.apache.coyote.http11.domain.header.ResponseHeader;
import org.apache.coyote.http11.domain.protocolVersion.ProtocolVersion;
import org.apache.coyote.http11.response.domain.HttpStatus;
import org.apache.coyote.http11.response.domain.ResponseLine;
import org.apache.coyote.http11.response.model.HttpResponse;

public class HttpResponseMaker {

    public static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";

    public static HttpResponse make(String body, ContentType contentType, HttpStatus httpStatus) {
        ProtocolVersion protocolVersion = new ProtocolVersion(DEFAULT_PROTOCOL_VERSION);
        ResponseHeader responseHeader = new ResponseHeader(body.getBytes().length, contentType);
        ResponseBody responseBody = new ResponseBody(body, contentType);

        ResponseLine responseLine = new ResponseLine(protocolVersion, httpStatus);
        return new HttpResponse(responseLine, responseHeader, responseBody);
    }
}

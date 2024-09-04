package org.apache.coyote.http11.converter;

import java.util.List;
import org.apache.coyote.http11.domain.RequestLine;
import org.apache.coyote.http11.domain.RequestMethod;
import org.apache.coyote.http11.domain.RequestPath;
import org.apache.coyote.http11.domain.protocolVersion.ProtocolVersion;

public final class RequestLineConverter {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;
    private static final int REQUEST_PROTOCOL = 2;


    private RequestLineConverter() {
    }

    public static RequestLine convertFrom(String inputRequest) {
        List<String> requests = List.of(inputRequest.split(" "));

        String requestMethod = requests.get(REQUEST_METHOD_INDEX).trim();
        String requestPath = requests.get(REQUEST_PATH_INDEX).trim();
        String protocolVersion = requests.get(REQUEST_PROTOCOL).trim();

        return new RequestLine(
                RequestMethod.findMethod(requestMethod),
                new RequestPath(requestPath),
                new ProtocolVersion(protocolVersion)
        );
    }
}

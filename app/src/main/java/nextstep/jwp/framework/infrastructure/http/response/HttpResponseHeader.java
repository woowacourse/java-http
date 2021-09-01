package nextstep.jwp.framework.infrastructure.http.response;

import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class HttpResponseHeader {

    private static final String RESPONSE_LINE_FORMAT = "%s %s %s \r\n";
    private static final String OTHER_RESPONSE_LINE_FORMAT = "%s: %s \r\n";

    private final ResponseLine responseLine;
    private final OtherResponseLines otherResponseLines;

    public HttpResponseHeader(ResponseLine responseLine, OtherResponseLines otherResponseLines) {
        this.responseLine = responseLine;
        this.otherResponseLines = otherResponseLines;
    }

    public String write() {
        Protocol protocol = responseLine.getProtocol();
        HttpStatus httpStatus = responseLine.getHttpStatus();
        String responseLineMessage = String.format(RESPONSE_LINE_FORMAT,
            protocol.getName(),
            httpStatus.getCode(),
            httpStatus.getMessage()
        );
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseLineMessage);
        otherResponseLines.getOtherLines()
            .entrySet()
            .stream()
            .map(this::convertToMessage)
            .forEach(stringBuilder::append);
        return stringBuilder.toString();
    }

    private String convertToMessage(Map.Entry<HttpHeaders, String> entry) {
        return String.format(OTHER_RESPONSE_LINE_FORMAT,
            entry.getKey().getSignature(),
            entry.getValue()
        );
    }
}

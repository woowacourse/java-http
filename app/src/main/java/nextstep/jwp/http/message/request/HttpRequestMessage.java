package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.HttpMessage;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.utils.StringUtils;

import java.util.List;
import java.util.Optional;

public class HttpRequestMessage implements HttpMessage {

    private static final String MESSAGE_SEPARATOR = "\r\n\r\n";
    private static final int HAS_BODY_COUNT = 2;

    private final String message;

    public HttpRequestMessage(String message) {
        this.message = message;
    }

    @Override
    public RequestHeader getHeader() {
        String headerString = parseHeaderString();
        return RequestHeader.from(headerString);
    }

    private String parseHeaderString() {
        return StringUtils.splitWithSeparator(message, MESSAGE_SEPARATOR).get(0);
    }

    @Override
    public Optional<MessageBody> getBody() {
        if (!hasBody()) {
            return Optional.empty();
        }
        String bodyString = parseBodyString();
        return Optional.of(new MessageBody(bodyString));
    }

    private boolean hasBody() {
        List<String> headerBodies = StringUtils.splitWithSeparator(message, MESSAGE_SEPARATOR);
        return headerBodies.size() == HAS_BODY_COUNT;
    }

    private String parseBodyString() {
        return StringUtils.splitWithSeparator(message, MESSAGE_SEPARATOR).get(1);
    }

    @Override
    public String toString() {
        return "HttpRequestMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}

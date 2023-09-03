package nextstep.jwp.protocol.request.line;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocol {

    private static final String HTTP_PROTOCOL_PATTERN = "^HTTP/1\\.[01]$";

    private final String protocol;

    private Protocol(String protocol) {
        this.protocol = protocol;
    }

    public static Protocol from(String protocol) {
        validateProtocol(protocol);
        return new Protocol(protocol);
    }

    private static void validateProtocol(String protocol) {
        Pattern compiledPattern = Pattern.compile(HTTP_PROTOCOL_PATTERN);
        Matcher matcher = compiledPattern.matcher(protocol);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("유효하지 않은 프로토콜입니다. 올바른 프로토콜인지 다시 확인해주세요.");
        }
    }

    public String protocol() {
        return protocol;
    }

}

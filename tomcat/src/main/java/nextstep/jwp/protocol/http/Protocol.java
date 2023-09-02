package nextstep.jwp.protocol.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocol {

    private static final String HTTP_PROTOCOL_PATTERN = "^HTTP/1\\.[01]$";

    private final String protocol;

    public Protocol(String protocol) {
        validateProtocol(protocol);
        this.protocol = protocol;
    }

    private void validateProtocol(String protocol) {
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

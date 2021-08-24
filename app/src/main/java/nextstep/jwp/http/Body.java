package nextstep.jwp.http;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.util.Objects;
import java.util.Optional;

public class Body {

    private final String body;

    public Body(String body) {
        this.body = body;
    }

    public static Body fromHttpRequest(String httpRequest) {
        return new Body(extractBody(httpRequest));
    }

    private static String extractBody(String httpRequest) {
        String bodySignature = LINE_SEPARATOR.repeat(2);

        int offset = httpRequest.indexOf(bodySignature);
        if (offset == -1) {
            return null;
        }

        return httpRequest.substring(offset + bodySignature.length());
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public String asString() {
        if(Objects.isNull(body)) {
            return "";
        }

        return LINE_SEPARATOR + body;
    }
}

package nextstep.jwp.http.request;

import java.util.Optional;

public class Body {

    private final String body;

    public Body(String httpRequest) {
        this.body = extractBody(httpRequest);
    }

    private String extractBody(String httpRequest) {
        String bodySignature = HttpRequest.LINE_SEPARATOR.repeat(2);

        int offset = httpRequest.indexOf(bodySignature);
        if (offset == -1) {
            return null;
        }

        return httpRequest.substring(offset + bodySignature.length());
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }
}

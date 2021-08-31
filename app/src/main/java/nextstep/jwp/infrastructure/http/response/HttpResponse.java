package nextstep.jwp.infrastructure.http.response;


import java.util.Objects;
import nextstep.jwp.infrastructure.http.HttpHeaders;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final HttpHeaders headers;
    private final String messageBody;

    public HttpResponse(final ResponseLine responseLine, final HttpHeaders headers, final String messageBody) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public HttpResponse(final ResponseLine responseLine, final HttpHeaders headers) {
        this(responseLine, headers, "");
    }

    @Override
    public String toString() {
        return String.join("\r\n",
            responseLine.toString(),
            headers.toString(),
            "",
            messageBody
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpResponse that = (HttpResponse) o;
        return Objects.equals(responseLine, that.responseLine) && Objects.equals(headers, that.headers) && Objects
            .equals(messageBody, that.messageBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseLine, headers, messageBody);
    }
}

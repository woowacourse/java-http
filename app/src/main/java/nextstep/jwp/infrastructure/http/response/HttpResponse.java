package nextstep.jwp.infrastructure.http.response;


import java.util.Objects;
import nextstep.jwp.infrastructure.http.Headers;

public class HttpResponse {

    private final Headers headers;
    private ResponseLine responseLine;
    private String messageBody;

    public HttpResponse() {
        this(new ResponseLine(StatusCode.OK), new Headers(), "");
    }

    public HttpResponse(final ResponseLine responseLine, final Headers headers, final String messageBody) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public HttpResponse(final ResponseLine responseLine, final Headers headers) {
        this(responseLine, headers, "");
    }

    public void setResponseLine(final ResponseLine responseLine) {
        this.responseLine = responseLine;
    }

    public void addHeader(final String key, final String value) {
        this.headers.add(key, value);
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setMessageBody(final String messageBody) {
        this.messageBody = messageBody;
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

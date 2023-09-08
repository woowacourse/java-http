package support;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.common.HttpHeaders;
import org.apache.coyote.http.common.Protocol;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.request.RequestUri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;

public class StubSocket extends Socket {

    private static final String SPACE = " ";
    private static final String HEADER_DELIMETER = ": ";
    private static final String CRLF = "\r\n";
    private static final String BLANK_LINE = "";

    private final HttpRequest request;
    private final ByteArrayOutputStream outputStream;

    public StubSocket(final HttpRequest request) {
        this.request = request;
        this.outputStream = new ByteArrayOutputStream();
    }

    public StubSocket() {
        this(createDefaultRequest());
    }

    private static HttpRequest createDefaultRequest() {
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/"), Protocol.HTTP_1_1);
        final HttpHeaders headers = new HttpHeaders(new EnumMap<>(HttpHeader.class));
        headers.add(HttpHeader.HOST, "localhost:8080");
        return new HttpRequest(requestLine, headers, HttpBody.empty());
    }

    @Override
    public InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException ignored) {
            return null;
        }
    }

    @Override
    public int getPort() {
        return 8080;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(serialize(request).getBytes());
    }

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(final int b) {
                outputStream.write(b);
            }
        };
    }

    public String output() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private String serialize(final HttpRequest request) {
        final StringBuilder builder = new StringBuilder();

        serializeRequestLine(request, builder);
        serializeHeaders(request, builder);

        if (request.getHttpBody() == null || request.getHttpBody().getValue().isEmpty()) {
            builder.append(CRLF);
            return builder.toString();
        }

        builder.append(HttpHeader.CONTENT_LENGTH.getValue())
                .append(HEADER_DELIMETER)
                .append(request.getHttpBody().getValue().getBytes().length)
                .append(CRLF);

        serializeBody(request, builder);
        return builder.toString();
    }

    private void serializeRequestLine(final HttpRequest request, final StringBuilder builder) {
        builder.append(request.getRequestLine().getHttpMethod().name()).append(SPACE);
        builder.append(request.getRequestLine().getRequestUri().getRequestUri()).append(SPACE);
        builder.append(request.getRequestLine().getProtocol().getName()).append(SPACE).append(CRLF);
    }

    private void serializeHeaders(final HttpRequest request, final StringBuilder builder) {
        request.getHeaders().getHeaders()
                .forEach((key, value) -> builder.append(key.getValue()).append(HEADER_DELIMETER).append(value).append(SPACE).append(CRLF));
    }

    private void serializeBody(final HttpRequest request, final StringBuilder builder) {
        builder.append(BLANK_LINE).append(CRLF);
        builder.append(request.getHttpBody().getValue());
    }
}

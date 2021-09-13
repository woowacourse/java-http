package nextstep.jwp.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;
import nextstep.jwp.http.ResponseHeaders;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class HttpResponseWriter {

    private final OutputStream outputStream;

    public HttpResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeHttpResponse(HttpResponse httpResponse) throws IOException {
        writeStatusLine(httpResponse.getHttpStatus());
        if (httpResponse.getHttpStatus().isRedirect()) {
            writeRedirect(httpResponse.getBody());
            return;
        }
        writeHeaders(httpResponse.getHeaders());
        writeBody(httpResponse.getBody());
    }

    public void writeStatusLine(HttpStatus status) throws IOException {
        final String line = String.format(
                "HTTP/1.1 %d %s \r\n", status.getCode(), status.getMessage());

        outputStream.write(line.getBytes());
    }

    public void writeHeaders(ResponseHeaders responseHeaders) throws IOException {
        String headers = responseHeaders.getHeaders()
                .keySet().stream()
                .map(key -> String.format("%s: %s ", key, responseHeaders.getHeaders().get(key)))
                .collect(Collectors.joining("\r\n"));

        outputStream.write(headers.getBytes());
        outputStream.write("\r\n".getBytes());
        outputStream.write("\r\n".getBytes());
    }

    public void writeBody(String body) throws IOException {
        outputStream.write(body.getBytes());
        outputStream.flush();
    }

    public void writeRedirect(String path) throws IOException {
        final String location = String.format("Location: %s", path);
        outputStream.write(location.getBytes());
        outputStream.flush();
    }
}

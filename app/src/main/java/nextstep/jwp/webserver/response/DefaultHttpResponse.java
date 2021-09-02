package nextstep.jwp.webserver.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.webserver.exception.NoFileExistsException;

public class DefaultHttpResponse implements HttpResponse {

    private static final String RESPONSE_FORM = "%s\r\n%s\r\n\r\n%s";
    private static final String LINE_FORM = "%s %s %s";
    private static final String HEADER_FORM = "%s: %s";
    private static final String HEADER_DELIMITER = ", ";
    private static final String HTTP = "HTTP/1.1";
    private static final String ENTER = "\r\n";
    private static final String LOCATION = "Location";

    private final OutputStream outputStream;
    private final Map<String, String> headers;
    private StatusCode statusCode;
    private StringBuilder content;

    public DefaultHttpResponse(OutputStream outputStream) {
        this.statusCode = StatusCode.OK;
        this.headers = new HashMap<>();
        this.content = new StringBuilder();
        this.outputStream = outputStream;
    }

    @Override
    public void addStatus(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void addHeader(String key, String... values) {
        headers.put(key, String.join(HEADER_DELIMITER, values));
    }

    @Override
    public void addBody(String content) {
        this.content.append(content);
    }

    @Override
    public void addPage(String path) throws NoFileExistsException {
        try {
            final URL url = getClass().getClassLoader().getResource(path);
            byte[] body = Files.readAllBytes(new File(url.toURI()).toPath());
            content = new StringBuilder(new String(body));
        } catch (Exception e) {
            throw new NoFileExistsException();
        }
    }

    @Override
    public void addRedirectUrl(String url) {
        addHeader(LOCATION, url);
        addStatus(StatusCode.FOUND);
    }

    @Override
    public void flush() {
        try {
            outputStream.write(totalResponse().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException("error during output stream");
        }
    }

    @Override
    public void flushAsRedirect(String redirectUrl) {
        addStatus(StatusCode.FOUND);
        addHeader(LOCATION, redirectUrl);
        replaceNewContent();
        flush();
    }

    private void replaceNewContent() {
        this.content = new StringBuilder();
    }

    @Override
    public String totalResponse() {
        return String.format(RESPONSE_FORM, statusLine(), headerLine(), content);
    }

    private String statusLine() {
        return String.format(LINE_FORM, HTTP, statusCode.statusNumber(), statusCode.name());
    }

    private String headerLine() {
        return headers.entrySet().stream()
                .map(entry -> String.format(HEADER_FORM, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(ENTER));
    }


}

package nextstep.jwp.response;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.PageNotFoundException;

public class DefaultHttpResponse implements HttpResponse {

    private static final String RESPONSE_FORM = "%s\r\n\r\n%s\r\n\r\n%s";
    private static final String LINE_FORM = "%s %s %s";
    private static final String HEADER_FORM = "%s: %s";
    private static final String HEADER_DELIMITER = ", ";
    private static final String HTTP = "HTTP/1.1";
    private static final String ENTER = "\r\n";

    private StatusCode statusCode;
    private Map<String, String> headers;
    private StringBuilder content;

    public DefaultHttpResponse() {
        this.statusCode = StatusCode.OK;
        this.headers = new HashMap<>();
        this.content = new StringBuilder();
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
    public void addPage(String path) {
        try {
            final URL url = getClass().getClassLoader().getResource(path);
            byte[] body = Files.readAllBytes(new File(url.toURI()).toPath());
            content = new StringBuilder(new String(body));
        } catch (Exception e) {
            throw new PageNotFoundException();
        }
    }

    @Override
    public void addRedirectUrl(String url) {

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

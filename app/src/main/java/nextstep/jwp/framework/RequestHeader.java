package nextstep.jwp.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHeader {

    public static final String DELIMITER = " ";

    private final String header;

    private RequestHeader(String header) {
        this.header = header;
    }

    public static RequestHeader from(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder header = new StringBuilder();

        while (bufferedReader.ready()) {
            header.append(bufferedReader.readLine())
                .append("\r\n");
        }

        return new RequestHeader(header.toString());
    }

    public URL getURL() {
        return RequestURI.findResourceURL(header);
    }

    public HttpMethod getHttpMethod() {
        return HttpMethod.findRequest(header);
    }

    public String getHeader() {
        return header;
    }
}

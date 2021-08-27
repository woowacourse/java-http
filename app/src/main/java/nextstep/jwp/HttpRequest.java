package nextstep.jwp;

import com.google.common.base.Splitter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String HEADER_DELIMITER = ":";
    private static final String URI_AND_QUERY_STRING_DELIMITER = "\\?";

    private HttpMethod httpMethod;

    private String uri;

    private String protocol;

    private Map<String, String> httpHeaders = new HashMap<>();

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        initialize(bufferedReader);
    }

    private void initialize(BufferedReader bufferedReader) throws IOException {
        List<String> startLine = Arrays.asList(bufferedReader.readLine().split(" "));
        this.httpMethod = HttpMethod.of(startLine.get(HTTP_METHOD_INDEX));
        this.uri = startLine.get(URI_INDEX);
        this.protocol = startLine.get(PROTOCOL_INDEX);

        String header = bufferedReader.readLine();
        while (header != null && !header.isEmpty()) {
            String[] keyValue = header.split(HEADER_DELIMITER);
            this.httpHeaders.put(keyValue[0].trim(), keyValue[1].trim());
            header = bufferedReader.readLine();
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return this.uri.split(URI_AND_QUERY_STRING_DELIMITER)[0];
    }

    public Map<String, String> getQueryStrings() {
        return Splitter.on("&")
            .withKeyValueSeparator("=")
            .split(this.uri.split(URI_AND_QUERY_STRING_DELIMITER)[1]);
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }
}

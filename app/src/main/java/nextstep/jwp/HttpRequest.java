package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String HEADER_DILIMETER = ":";

    private String httpMethod;

    private String uri;

    private String protocol;

    private Map<String, String> httpHeaders = new HashMap<>();

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        initialize(bufferedReader);
    }

    private void initialize(BufferedReader bufferedReader) throws IOException {
        List<String> request = Arrays.asList(bufferedReader.readLine().split(" "));
        this.httpMethod = request.get(HTTP_METHOD_INDEX);
        this.uri = request.get(URI_INDEX);
        this.protocol = request.get(PROTOCOL_INDEX);

        String header = bufferedReader.readLine();
        while (header != null && !header.isEmpty()) {
            String[] keyValue = header.split(HEADER_DILIMETER);
            this.httpHeaders.put(keyValue[0].trim(), keyValue[1].trim());
            header = bufferedReader.readLine();
        }
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }
}

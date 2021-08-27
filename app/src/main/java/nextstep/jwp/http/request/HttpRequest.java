package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpProtocol;

public class HttpRequest {

    private final HttpRequestHeaders headers;
    private final HttpMethod method;
    private final HttpProtocol protocol;
    private final String filepath;
    private final HttpRequestBody body;

    public HttpRequest(HttpRequestHeaders headers,
                       HttpMethod method,
                       HttpProtocol httpProtocol,
                       String filepath,
                       HttpRequestBody body) {
        this.headers = headers;
        this.method = method;
        this.protocol = httpProtocol;
        this.filepath = filepath;
        this.body = body;
    }

    public static HttpRequest of(InputStream inputStream) {
        InputStreamHttpRequestConverter inputStreamHttpRequestConverter =
            new InputStreamHttpRequestConverter(inputStream);
        return inputStreamHttpRequestConverter.toRequest();
    }

    public HttpMethod method() {
        return this.method;
    }

    public String filepath() {
        return this.filepath;
    }

    public HttpRequestHeaders headers() {
        return this.headers;
    }

    public HttpRequestHeaderValues header(String key) {
        return this.headers.get(key);
    }

    public HttpRequestBody body() {
        return this.body;
    }

    @Override
    public String toString() {
        final List<String> headers = this.headers.map().entrySet().stream()
            .map(set -> set.getKey() + ": " + set.getValue().toValuesString())
            .collect(Collectors.toList());
        return method + " " + filepath + " " + protocol.getProtocolName() + "\r\n" +
            String.join("\r\n", headers) +
            "\r\n" +
            body.getBody();
    }

    static class InputStreamHttpRequestConverter {

        private static final int METHOD_INDEX = 0;
        private static final int FILEPATH_INDEX = 1;
        private static final int KEY_AND_VALUE_MINIMUM_SIZE = 2;
        private static final int HEADER_KEY_INDEX = 0;
        private static final int COMMA_LENGTH = 1;
        private static final int HTTP_PROTOCOL_INDEX = 2;

        private HttpRequestHeaders headers = new HttpRequestHeaders();
        private HttpMethod method = null;
        private HttpProtocol httpProtocol = null;
        private String filepath = "";
        private HttpRequestBody body = new TextHttpRequestBody("\r\n");

        public InputStreamHttpRequestConverter(InputStream inputStream) {
            parse(inputStream);
        }

        public HttpRequest toRequest() {
            return new HttpRequest(headers, method, httpProtocol, filepath, body);
        }

        private void parse(InputStream inputStream) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            try (bufferedReader) {
                parseStatusLine(bufferedReader.readLine());
                while (bufferedReader.ready()) {
                    parseHeader(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void parseStatusLine(String line) {
            List<String> parsedStartHeader = Arrays.asList(line.split(" "));

            this.method = HttpMethod.of(parsedStartHeader.get(METHOD_INDEX));
            this.filepath = parsedStartHeader.get(FILEPATH_INDEX);
            this.httpProtocol = HttpProtocol.findByName(parsedStartHeader.get(HTTP_PROTOCOL_INDEX));
        }

        private void parseHeader(String line) {
            if (line.isBlank()) {
                return;
            }

            List<String> parsedHeader = Arrays.asList(line.split(":"));

            if (parsedHeader.size() < KEY_AND_VALUE_MINIMUM_SIZE) {
                throw new RuntimeException("request 헤더의 양식이 맞지 않습니다.");
            }

            String headerKey = parsedHeader.get(HEADER_KEY_INDEX);
            String rawHeaderValue = line.substring(headerKey.length() + COMMA_LENGTH);

            List<String> values = Arrays.asList(rawHeaderValue.split(","));
            headers.add(headerKey, values);
        }
    }
}

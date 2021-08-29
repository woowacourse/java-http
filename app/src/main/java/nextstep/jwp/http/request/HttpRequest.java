package nextstep.jwp.http.request;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpProtocol;

public class HttpRequest {

    private final HttpHeaders headers;
    private final HttpMethod method;
    private final HttpProtocol protocol;
    private final String url;
    private final QueryParams queryParams;
    private final HttpRequestBody body;

    public HttpRequest(HttpHeaders headers, HttpMethod method, HttpProtocol protocol,
                       String url, QueryParams queryParams,
                       HttpRequestBody body) {
        this.headers = headers;
        this.method = method;
        this.protocol = protocol;
        this.url = url;
        this.queryParams = queryParams;
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

    public String url() {
        return this.url;
    }

    public QueryParams queryParam() {
        return queryParams;
    }

    public HttpProtocol protocol() {
        return protocol;
    }

    public HttpHeaders headers() {
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
        return method + " " + url + " " + protocol.getProtocolName() + "\r\n" +
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
        private static final String BLANK = " ";

        private HttpHeaders headers = new HttpHeaders();
        private HttpMethod method = null;
        private HttpProtocol httpProtocol = null;
        private String url = "";
        private QueryParams queryParams = null;
        private HttpRequestBody body = new TextHttpRequestBody("\r\n");

        public InputStreamHttpRequestConverter(InputStream inputStream) {
            parse(inputStream);
        }

        public HttpRequest toRequest() {
            return new HttpRequest(headers, method, httpProtocol, url, queryParams, body);
        }

        private void parse(InputStream inputStream) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                parseStatusLine(bufferedReader.readLine());
                while (bufferedReader.ready()) {
                    parseHeader(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void parseStatusLine(String line) {
            List<String> parsedStatusLine = Arrays.asList(line.split(BLANK));

            this.method = HttpMethod.of(parsedStatusLine.get(METHOD_INDEX));
            QueryParser queryParser = new QueryParser(parsedStatusLine.get(FILEPATH_INDEX));
            this.queryParams = queryParser.queryParams();
            this.url = queryParser.parsedUrl();
            this.httpProtocol = HttpProtocol.findByName(parsedStatusLine.get(HTTP_PROTOCOL_INDEX));
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

    static class QueryParser {

        private static final int NOT_FOUND_INDEX = -1;
        private static final int QUERY_KEY_INDEX = 0;
        private static final int QUERY_VALUE_INDEX = 1;

        private static final String QUERY_SEPARATOR = "=";
        private static final String QUERY_STARTER = "?";
        private static final String QUERY_AMPERSAND = "&";

        private final String parsedUrl;
        private final QueryParams queryParams;

        public QueryParser(String url) {
            int index = url.indexOf(QUERY_STARTER);
            if (index != NOT_FOUND_INDEX) {
                this.parsedUrl = url.substring(0, index);
                this.queryParams = parseQueryParams(url.substring(index + 1));
                return;
            }
            this.parsedUrl = url;
            this.queryParams = new QueryParams();
        }

        private QueryParams parseQueryParams(String queryParams) {
            return new QueryParams(
                Arrays.stream(queryParams.split(QUERY_AMPERSAND))
                    .map(it -> Arrays.asList(it.split(QUERY_SEPARATOR)))
                    .collect(toMap(
                        it -> it.get(QUERY_KEY_INDEX),
                        it -> it.get(QUERY_VALUE_INDEX), (o1, o2) -> o1, LinkedHashMap::new))
            );
        }

        public QueryParams queryParams() {
            return queryParams;
        }

        public String parsedUrl() {
            return parsedUrl;
        }
    }
}

package nextstep.jwp.web.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.MimeType;
import nextstep.jwp.web.http.request.body.FormDataHttpRequestBody;
import nextstep.jwp.web.http.request.body.HttpRequestBody;
import nextstep.jwp.web.http.request.body.TextHttpRequestBody;
import nextstep.jwp.web.http.util.QueryParser;

public class HttpRequest {

    private final HttpHeaders headers;
    private final HttpProtocol protocol;
    private final MethodUrl methodUrl;
    private final HttpRequestBody body;

    private HttpRequest(HttpHeaders headers, HttpProtocol protocol,
                        MethodUrl methodUrl,
                        HttpRequestBody body) {
        this.headers = headers;
        this.protocol = protocol;
        this.methodUrl = methodUrl;
        this.body = body;
    }

    public static HttpRequest of(InputStream inputStream) {
        InputStreamHttpRequestConverter inputStreamHttpRequestConverter =
            new InputStreamHttpRequestConverter(inputStream);
        return inputStreamHttpRequestConverter.toRequest();
    }

    public boolean isJsonOrHtml() {
        return mimeType() == MimeType.TEXT_HTML || mimeType() == MimeType.JSON;
    }

    public MimeType mimeType() {
        return this.headers.mimeType();
    }

    public MethodUrl methodUrl() {
        return methodUrl;
    }

    public QueryParams queryParam() {
        return methodUrl.queryParams();
    }

    public HttpProtocol protocol() {
        return protocol;
    }

    public HttpRequestHeaderValues header(String key) {
        return this.headers.get(key);
    }

    public HttpRequestBody body() {
        return this.body;
    }

    public HttpRequest changeMethodUrl(MethodUrl methodUrl) {
        return new HttpRequest(headers, protocol, methodUrl, body);
    }

    @Override
    public String toString() {
        final List<String> headers = this.headers.map().entrySet().stream()
            .map(set -> set.getKey() + ": " + set.getValue().toValuesString())
            .collect(Collectors.toList());
        return methodUrl + " " + protocol.getProtocolName() + "\r\n" +
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
        private static final int NOT_FOUND_INDEX = -1;
        private static final int START = 0;
        private static final String CRLF = "\r\n";
        private static final String QUERY_STARTER = "?";
        private static final String BLANK = " ";
        private static final String COLON = ":";
        private static final String COMMA = ",";

        private HttpHeaders headers = new HttpHeaders();
        private HttpProtocol httpProtocol = null;
        private MethodUrl methodUrl = null;
        private HttpRequestBody body = new TextHttpRequestBody(CRLF);

        public InputStreamHttpRequestConverter(InputStream inputStream) {
            parse(inputStream);
        }

        public HttpRequest toRequest() {
            return new HttpRequest(headers, httpProtocol, methodUrl, body);
        }

        private void parse(InputStream inputStream) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                parseStatusLine(bufferedReader);
                parseHeaders(bufferedReader);
                int contentLength = this.headers.contentLength();
                if (contentLength != 0) {
                    char[] buffer = new char[contentLength];
                    bufferedReader.read(buffer, 0, contentLength);
                    parseBody(new String(buffer));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void parseStatusLine(BufferedReader bufferedReader)
            throws IOException {
            List<String> parsedStatusLine = Arrays.asList(bufferedReader.readLine().split(BLANK));

            this.httpProtocol = HttpProtocol.findByName(parsedStatusLine.get(HTTP_PROTOCOL_INDEX));

            HttpMethod method = HttpMethod.findByName(parsedStatusLine.get(METHOD_INDEX));
            String url = parsedStatusLine.get(FILEPATH_INDEX);
            int index = url.indexOf(QUERY_STARTER);
            this.methodUrl = getMethodUrl(method, url, index);
        }

        private MethodUrl getMethodUrl(HttpMethod method, String url, int index) {
            if (index != NOT_FOUND_INDEX) {
                QueryParams queryParams = new QueryParser(url.substring(index + 1)).queryParams();
                return new MethodUrl(method, url.substring(START, index), queryParams);
            }
            return new MethodUrl(method, url, QueryParams.empty());
        }

        private void parseHeaders(BufferedReader bufferedReader) throws IOException {
            StringBuilder headersBuilder = new StringBuilder();
            String line = BLANK;
            while (!"".equals(line)) {
                line = bufferedReader.readLine();
                if (Objects.isNull(line)) {
                    break;
                }
                headersBuilder.append(line).append(CRLF);
            }

            List<String> headerLines = Arrays.asList(
                headersBuilder.toString().split(CRLF)
            );

            headerLines.forEach(this::parseHeader);
        }

        private void parseHeader(String line) {
            List<String> parsedHeader = Arrays.asList(line.split(COLON));

            if (parsedHeader.size() < KEY_AND_VALUE_MINIMUM_SIZE) {
                throw new RuntimeException("request 헤더의 양식이 맞지 않습니다.");
            }

            String headerKey = parsedHeader.get(HEADER_KEY_INDEX);
            String rawHeaderValue = line.substring(headerKey.length() + COMMA_LENGTH);

            List<String> values = Arrays.asList(rawHeaderValue.split(COMMA));
            headers.add(headerKey, values);
        }

        private void parseBody(String body) {
            this.body = new FormDataHttpRequestBody(body);
        }
    }
}

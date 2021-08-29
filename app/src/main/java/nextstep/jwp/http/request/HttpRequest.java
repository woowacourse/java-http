package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final String DELIMITER = " ";

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private HttpMethod httpMethod;
    private RequestURI requestURI;
    private HttpHeader httpHeader;
    private RequestBody requestBody;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        try {
            String firstLine = bufferedReader.readLine();
            String[] lines = firstLine.split(DELIMITER);
            this.httpMethod = new HttpMethod(lines[METHOD_INDEX]);
            this.requestURI = new RequestURI(lines[URI_INDEX]);
            this.httpHeader = readHeaders(bufferedReader);
            this.requestBody = readRequestBody(bufferedReader, httpHeader.getContentLength());
        } catch (IOException e) {
            log.error("stream exception");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청입니다.");
        }

    }

    private RequestBody readRequestBody(BufferedReader bufferedReader, int contentLength)
        throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new RequestBody(new String(buffer));
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    private HttpHeader readHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> map = new HashMap<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if ("".equals(line)) {
                break;
            }
            String[] params = line.split(": ");
            map.put(params[0], params[1]);
        }
        return new HttpHeader(map);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestURI getRequestURI() {
        return this.requestURI;
    }

    public boolean isGet() {
        return httpMethod.isGet();
    }

    public boolean isPost() {
        return httpMethod.isPost();
    }
}

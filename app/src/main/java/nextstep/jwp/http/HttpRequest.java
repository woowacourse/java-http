package nextstep.jwp.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// 클라이언트의 요청 데이터를 읽은 후 각 데이터를 사용하기 좋은 형태로 분리하는 역할
// 데이터를 파싱하는 작업만 담당, 사용하는 부분은 RequestHandler가 담당
public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream inputStream) {
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }
            requestLine = new RequestLine(line);

            line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                if ("".equals(line)) {
                    break;
                }
                String[] tokens = line.split(": ");
                headers.put(tokens[0], tokens[1]);
                line = bufferedReader.readLine();
            }

            if (getMethod().isPost()) {
                String body = readData(bufferedReader);
                parseValues(body);
            } else {
                params = requestLine.getParams();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void parseValues(String values) {
        String[] tokens = values.split("&");
        Arrays.stream(tokens).forEach(this::getDataFromQueryString);
    }

    private void getDataFromQueryString(String data) {
        params.put(data.substring(0, data.indexOf("=")), data.substring(data.indexOf("=") + 1));
    }

    private String readData(BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(getHeader("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public Map<String, String> getParams() {
        return params;
    }
}

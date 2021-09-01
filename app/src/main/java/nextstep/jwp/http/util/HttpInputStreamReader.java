package nextstep.jwp.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.stateful.HttpCookie;
import nextstep.jwp.http.request.HttpHeader;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestLine;
import nextstep.jwp.http.request.Parameters;

public class HttpInputStreamReader {

    private static final String HEADER_KEY_VALUE_SEPARATOR = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final BufferedReader bufferedReader;

    public HttpInputStreamReader(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public HttpRequest createHttpRequest() throws IOException {
        HttpRequestLine httpRequestLine = new HttpRequestLine(bufferedReader.readLine());
        HttpHeader httpHeader = new HttpHeader(parseHeader());
        HttpCookie httpCookie = new HttpCookie(httpHeader.getCookie());
        String httpBody = parseBody(httpHeader);
        Parameters parameters = new Parameters(httpRequestLine.getRequestURI(), httpBody);

        return new HttpRequest(httpRequestLine, httpHeader, httpCookie, httpBody, parameters);
    }

    private String parseBody(HttpHeader httpHeader) throws IOException {
        if (httpHeader.containContentLength()) {
            int contentLength = Integer.parseInt(httpHeader.getContentLength());
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return new String(buffer);
        }
        return "";
    }

    private Map<String, String> parseHeader() throws IOException {
        Map<String, String> httpHeaders = new HashMap<>();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line == null || line.equals("")) {
                break;
            }
            String[] splitHeaderLine = line.split(HEADER_KEY_VALUE_SEPARATOR);
            httpHeaders.put(splitHeaderLine[HEADER_KEY_INDEX], splitHeaderLine[HEADER_VALUE_INDEX]);
        }
        return httpHeaders;
    }

}

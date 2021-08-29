package nextstep.jwp.webserver.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javassist.tools.web.BadHttpRequest;
import nextstep.jwp.webserver.exception.BadRequestException;

public class DefaultHttpRequest implements HttpRequest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_DATA = "application/x-www-form-urlencoded";
    private static final String UTF_8 = "UTF-8";

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private RequestParams requestParams;

    public DefaultHttpRequest(InputStream inputStream) {

        try {
            final BufferedReader br =
                    new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            this.requestLine = new RequestLine(br.readLine());
            this.requestHeader = parseHeader(br);
            this.requestParams = parseParams(br);
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }

    private RequestHeader parseHeader(BufferedReader br) throws IOException {
        final RequestHeader newHeader = new RequestHeader();
        String line;
        while (!(line = br.readLine()).equals("")) {
            newHeader.add(line);
        }
        return newHeader;
    }

    private RequestParams parseParams(BufferedReader br) throws IOException {
        RequestParams newParams = new RequestParams();
        newParams.addParams(requestLine.queryString());
        char[] body = readBody(br);

        if (isFormData(requestHeader.get(CONTENT_TYPE))) {
            newParams.addParams(String.copyValueOf(body));
            return newParams;
        }

        newParams.addBody(String.copyValueOf(body));
        return newParams;
    }

    private char[] readBody(BufferedReader br) throws IOException {
        int contentLength = requestHeader.contentLength();
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return body;
    }

    private boolean isFormData(String contentType) {
        return FORM_DATA.equals(contentType);
    }

    @Override
    public HttpMethod httpMethod() {
        return requestLine.httpMethod();
    }

    @Override
    public String httpUrl() {
        return requestLine.httpUrl();
    }

    @Override
    public String getAttribute(String key) {
        return requestParams.getParam(key);
    }
}

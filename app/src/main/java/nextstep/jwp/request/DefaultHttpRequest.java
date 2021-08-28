package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DefaultHttpRequest implements HttpRequest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_DATA = "application/x-www-form-urlencoded";

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private RequestParams requestParams;

    public DefaultHttpRequest(InputStream inputStream) {

        try {
            final BufferedReader br =
                    new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            this.requestLine = new RequestLine(br.readLine());
            this.requestHeader = parseHeader(br);
            this.requestParams = parseParams(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RequestHeader parseHeader(BufferedReader br) throws IOException {
        final RequestHeader requestHeader = new RequestHeader();
        String line;
        while (!(line = br.readLine()).equals("")) {
            requestHeader.add(line);
        }
        return requestHeader;
    }

    private RequestParams parseParams(BufferedReader br) throws IOException {
        RequestParams requestParams = new RequestParams();
        requestParams.addParams(requestLine.queryString());
        int contentLength = requestHeader.contentLength();
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        if(isFormData(requestHeader.get(CONTENT_TYPE))) {
            requestParams.addParams(String.copyValueOf(body));
        } else {
            requestParams.addBody(String.copyValueOf(body));
        }


        return requestParams;
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
}

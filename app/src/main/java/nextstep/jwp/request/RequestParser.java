package nextstep.jwp.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private static final String HTTP_METHOD_REQUEST_URL_SEPARATOR = " /";

    final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public ClientRequest extractClientRequest() throws IOException {
        final String request = bufferedReader.readLine();
        log.info("client request = {}", request);
        final HttpMethod httpMethod = extractHttpMethod(request);
        final RequestUrl requestUrl = extractRequestUrl(request);
        return new ClientRequest(httpMethod, requestUrl);
    }

    private HttpMethod extractHttpMethod(String request) {
        final String parsedHttpMethod = request.substring(0, request.indexOf(HTTP_METHOD_REQUEST_URL_SEPARATOR));
        return HttpMethod.of(parsedHttpMethod);
    }

    private RequestUrl extractRequestUrl(String request) {
        final int spaceAndSlashIndex = request.indexOf(HTTP_METHOD_REQUEST_URL_SEPARATOR);
        final int urlHttpVersionSeparator = request.lastIndexOf(" ");

        final String parsedRequestUrl = request.substring(spaceAndSlashIndex + 2, urlHttpVersionSeparator);
        return RequestUrl.of(parsedRequestUrl);
    }
}

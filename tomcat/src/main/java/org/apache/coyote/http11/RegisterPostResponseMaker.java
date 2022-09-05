package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.QueryParamsParser;

public class RegisterPostResponseMaker implements ResponseMaker {
    @Override
    public String createResponse(final HttpRequest httpRequest) throws URISyntaxException, IOException {
        final String requestUrl = httpRequest.getRequestUrl();
        final String requestBody = httpRequest.getRequestBody();
        final HashMap<String, String> registerData = QueryParamsParser.parseByBody(requestBody);
        final URL resource =
                this.getClass().getClassLoader().getResource("static" + requestUrl + ".html");
        final Path path = Paths.get(resource.toURI());
        final var responseBody = new String(Files.readAllBytes(path));
        final HttpResponse httpResponse =
                new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/index.html");
        return httpResponse.toFoundString();
    }
}

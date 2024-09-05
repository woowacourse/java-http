package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpResponseBody;
import org.apache.coyote.http11.httpresponse.HttpResponseHeader;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse servicePost(HttpRequest httpRequest) {
        String requestBody = httpRequest.getBody();
        String[] token = requestBody.split("&");
        String account = token[0].split("=")[1];
        String email = token[1].split("=")[1];
        String password = token[2].split("=")[1];
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND);
        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.addHeaders("Location", "/index.html");

        return new HttpResponse(httpStatusLine, httpResponseHeader);
    }

    @Override
    protected HttpResponse serviceGet(HttpRequest httpRequest) {
        try {
            HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);

            String fileName = "static/register.html";
            var resourceUrl = getClass().getClassLoader().getResource(fileName);
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));
            HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
            httpResponseHeader.addHeaders("Content-Type", ContentType.HTML.getContentType());
            httpResponseHeader.addHeaders("Content-Length", String.valueOf(responseBody.getBytes().length));
            HttpResponseBody httpResponseBody = new HttpResponseBody(responseBody);

            return new HttpResponse(httpStatusLine, httpResponseHeader, httpResponseBody);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

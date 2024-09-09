package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.util.StringUtils;

import java.io.IOException;
import java.util.Map;

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public class RegisterController extends AbstractController {

    private static final String REDIRECT_LOCATION = "/index.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        try {
            Path path = request.getPath();
            return generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        try {
            Path path = request.getPath();
            HttpResponse response = generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND);
            response.setRedirectLocation(REDIRECT_LOCATION);

            //TODO: LoginService 혹은 RegisterService 로 분리하기
            Map<String, String> parsedBody = StringUtils.separateKeyValue(request.getBody());
            InMemoryUserRepository.save(new User(parsedBody.get("account"), parsedBody.get("password"), parsedBody.get("email")));

            return response;
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }
}

package org.apache.coyote.http11.response;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.QueryParams;
import org.apache.coyote.http11.request.HttpRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RegisterPostResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final HttpRequest request) throws Exception {
        saveMemberFromQuery(request);

        final HttpResponse httpResponse = new HttpResponse(StatusCode.CREATED, ContentType.HTML, new String(getResponseBodyBytes("/index.html"), UTF_8));
        return httpResponse.getResponse();
    }

    private void saveMemberFromQuery(final HttpRequest request) {
        final QueryParams queryParams = QueryParams.from(request.getRequestBody());
        InMemoryUserRepository.save(new User(
                queryParams.getValueFromKey("account"),
                queryParams.getValueFromKey("password"),
                queryParams.getValueFromKey("email")));
    }

    private byte[] getResponseBodyBytes(String resourcePath) throws IOException {
        final URL fileUrl = this.getClass().getClassLoader().getResource("static" + resourcePath);
        return Files.readAllBytes(Paths.get(fileUrl.getPath()));
    }

}

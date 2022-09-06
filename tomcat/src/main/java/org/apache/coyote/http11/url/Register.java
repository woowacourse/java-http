package org.apache.coyote.http11.url;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpDataRequest;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.IOUtils;

public class Register extends Url {
    public Register(String url, Http11Request request) {
        super(url, request);
    }

    @Override
    public Http11Response handle(HttpHeaders httpHeaders, String requestBody) {
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            return new Http11Response(getPath(), HttpStatus.OK, IOUtils.readResourceFile(getPath()));
        }
        if (HttpMethod.POST.equals(request.getHttpMethod())) {
            HttpDataRequest joinData = HttpDataRequest.extractRequest(requestBody);
            User user = new User(joinData.get("account"), joinData.get("password"), joinData.get("email"));
            InMemoryUserRepository.save(user);
            log.info("save user: {}", user);
            return new Http11Response(getPath(), HttpStatus.FOUND, IOUtils.readResourceFile("/index.html"));
        }
        throw new IllegalArgumentException("Register에 해당하는 HTTP Method가 아닙니다. : " + request.getHttpMethod());
    }
}

package org.apache.coyote.http11.url;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.dto.JoinQueryDto;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.IOUtils;
import org.apache.coyote.http11.utils.UrlParser;

public class Register extends Url {
    public Register(String url, String httpMethod) {
        super(url, httpMethod);
    }

    @Override
    public Http11Response getResponse(HttpHeaders httpHeaders) {
        return new Http11Response(getPath(), HttpStatus.OK, IOUtils.readResourceFile(getPath()));

    }

    @Override
    public Http11Response postResponse(HttpHeaders httpHeaders, String requestBody) {
        JoinQueryDto joinQueryDto = UrlParser.joinQuery(requestBody);
        User user = new User(joinQueryDto.getAccount(), joinQueryDto.getPassword(),
                joinQueryDto.getEmail());
        InMemoryUserRepository.save(user);
        log.info("save user: {}", user);
        return new Http11Response(getPath(), HttpStatus.FOUND, IOUtils.readResourceFile("/index.html"));
    }
}

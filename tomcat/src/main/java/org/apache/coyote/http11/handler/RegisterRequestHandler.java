package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequestHandler implements RequestHandler{
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest){
        final Map<String, String> headers = new HashMap<>();
        try{
            register(httpRequest.params());
            log.info("계정 : {} 회원가입 완료", httpRequest.params().get("account"));
            log.info("이메일 : {}", httpRequest.params().get("email"));
            headers.put("Location", "/index.html");
            return new HttpResponse("/index.html", HttpStatus.CREATED, headers);
        } catch (IllegalArgumentException e){
            headers.put("Location", "/500.html");
            return new HttpResponse("/500.html", HttpStatus.INTERNAL_SERVER_ERROR, headers);
        }
    }

    private void register(Map<String,String> params){
        if (InMemoryUserRepository.findByAccount(params.get("account")).isPresent()){
            throw new IllegalArgumentException("이미 가입한 회원");
        }

        InMemoryUserRepository.save(
                new User(params.get("account"), params.get("password"), params.get("email")));
    }
}

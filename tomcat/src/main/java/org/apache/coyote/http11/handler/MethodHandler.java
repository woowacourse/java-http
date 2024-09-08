package org.apache.coyote.http11.handler;

import java.io.IOException;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class MethodHandler implements RequestHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String handle(Request request) throws IOException {
        MethodRequest methodRequest = new MethodRequest(request.getTarget());
        return Response.writeResponse(request, "application/json", handleMethod(methodRequest));
    }

    private String handleMethod(MethodRequest methodRequest) throws JsonProcessingException {
        if (!methodRequest.getEndPoint().equals("/login")) {
            return null;
        }
        String account = methodRequest.getParam("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException(account + " 이름의 유저가 없습니다."));
        return objectMapper.writeValueAsString(user);
    }
}

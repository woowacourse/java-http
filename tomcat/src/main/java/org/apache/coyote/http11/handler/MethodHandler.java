package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        String[] targetToken = request.getTarget().split("\\?");
        Map<String, String> queryParams = new HashMap<>();
        String[] queryParamTokens = targetToken[1].split("&");
        for (String queryParam : queryParamTokens) {
            String[] split = queryParam.split("=");
            queryParams.put(split[0], split[1]);
        }
        return Response.writeResponse(request, "application/json", handleMethod(targetToken[0], queryParams));
    }

    private String handleMethod(String endPoint, Map<String, String> queryParams) throws JsonProcessingException {
        if (!endPoint.equals("/login")) {
            return null;
        }
        String account = queryParams.get("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException(account + " 이름의 유저가 없습니다."));
        return objectMapper.writeValueAsString(user);
    }
}

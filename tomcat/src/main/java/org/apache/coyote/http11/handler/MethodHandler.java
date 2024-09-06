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

    private static class MethodRequest {
        private final String endPoint;
        private final Map<String, String> queryParams;

        private MethodRequest(String url) {
            String[] targetToken = url.split("\\?");
            this.endPoint = targetToken[0];

            Map<String,String> queryParams = new HashMap<>();
            String[] queryParamTokens = targetToken[1].split("&");
            for (String queryParam : queryParamTokens) {
                String[] split = queryParam.split("=");
                queryParams.put(split[0], split[1]);
            }
            this.queryParams = Map.copyOf(queryParams);
        }

        public String getParam(String key) {
            return queryParams.get(key);
        }

        public String getEndPoint() {
            return this.endPoint;
        }
    }

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

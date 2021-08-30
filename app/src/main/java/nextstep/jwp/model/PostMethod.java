package nextstep.jwp.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class PostMethod extends Method {
    private String requestUrl;

    public PostMethod(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String matchFunction(final String requestBody) {
        try {
            String request = String.join(" ", requestUrl, requestBody);
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(jwpController.mapResponse(request).entrySet()).get(0);
            return makeResponse(responseEntry.getKey(), responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private String getParam(final String request) {
        try {
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(jwpController.mapResponse(request).entrySet()).get(0);
            return makeResponse(responseEntry.getKey(), responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private String getPage(final String request) {
        try {
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(pageController.mapResponse(Optional.empty(), request).entrySet()).get(0);
            return makeResponse(responseEntry.getKey(), responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

}

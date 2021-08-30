package nextstep.jwp.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class GetMethod extends Method{
    private String request;

    public GetMethod(final String request) {
        this.request = request;
    }

    @Override
    public String matchFunction(final String requestBody) {
        if (request.contains("?") && request.contains("=")) {
            return getParam(request);
        }
        return getPage(request);
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

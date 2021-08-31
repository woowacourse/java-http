package nextstep.jwp.http.Params;

import java.util.Map;

public class BodyParams {

    private final Map<String, String> BodyParamData;

    public BodyParams(final Map<String, String> bodyType, final String body) {
        this.BodyParamData = parseBodyData(bodyType, body);
    }

    private Map<String, String> parseBodyData(final Map<String, String> bodyType, final String body) {
        String[] parseRequestBody = parseRequestBody(body);

        for (String bodyData : parseRequestBody) {
            String[] parseBodyData = bodyData.split("=");
            bodyType.put(parseBodyData[0], parseBodyData[1]);
        }
        return bodyType;
    }

    private String[] parseRequestBody(final String body) {
        return body.split("&");
    }

    public String get(final String key) {
        return BodyParamData.get(key);
    }
}

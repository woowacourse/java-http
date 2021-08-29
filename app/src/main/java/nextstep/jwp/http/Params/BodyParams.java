package nextstep.jwp.http.Params;

import java.util.Map;

public class BodyParams {

    private final Map<String, String> BodyParamData;

    public BodyParams(final Map<String, String> BodyParamData) {
        this.BodyParamData = BodyParamData;
    }

    public void put(final String key, final String value) {
        BodyParamData.put(key, value);
    }

    public String get(final String key) {
        return BodyParamData.get(key);
    }
}

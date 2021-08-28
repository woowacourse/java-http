package nextstep.mockweb.option.template;

import java.util.HashMap;
import java.util.Map;
import nextstep.mockweb.option.OptionTemplate;
import nextstep.mockweb.request.RequestInfo;

public class HeaderOptionTemplate implements OptionTemplate {

    private Map<String, String> headers = new HashMap<>();

    @Override
    public void beforeOperation(RequestInfo requestInfo) {
        requestInfo.addHeader(headers);
    }

    @Override
    public void afterOperation(String result) {

    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}

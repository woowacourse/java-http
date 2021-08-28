package nextstep.mockweb.option;

import nextstep.mockweb.request.RequestInfo;

public interface OptionTemplate {

    void beforeOperation(RequestInfo requestInfo);

    void afterOperation(String result);
}

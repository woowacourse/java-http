package nextstep.mockweb.option.template;

import nextstep.mockweb.option.OptionTemplate;
import nextstep.mockweb.request.RequestInfo;

public class LogOptionTemplate implements OptionTemplate {


    @Override
    public void beforeOperation(RequestInfo requestInfo) {
        System.out.println("==========request start==========");
        System.out.println(requestInfo.asRequest());
        System.out.println("==========request end==========");
    }

    @Override
    public void afterOperation(String result) {
        System.out.println("==========result start==========");
        System.out.println(result);
        System.out.println("==========result end==========");
    }
}

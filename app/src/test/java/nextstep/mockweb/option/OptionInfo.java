package nextstep.mockweb.option;

import java.util.ArrayList;
import java.util.List;
import nextstep.mockweb.option.template.HeaderOptionTemplate;
import nextstep.mockweb.option.template.LogOptionTemplate;
import nextstep.mockweb.request.RequestInfo;

public class OptionInfo {

    private List<OptionTemplate> optionTemplates;

    public OptionInfo() {
        this.optionTemplates = new ArrayList<>();
    }

    public void executeBeforeOption(RequestInfo requestInfo) {
        optionTemplates.forEach(optionTemplate -> {
            optionTemplate.beforeOperation(requestInfo);
        });
    }

    public void executeAfterOption(String result) {
        optionTemplates.forEach(optionTemplate -> {
            optionTemplate.afterOperation(result);
        });
    }

    public void addOptionTemplate(OptionTemplate optionTemplate) {
        this.optionTemplates.add(optionTemplate);
    }

    public void logAll() {
        addOptionTemplate(new LogOptionTemplate());
    }
}

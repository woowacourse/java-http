package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import utils.StringSplitter;

public class Parameters {

    private Map<String, String> params;

    private Parameters(final Map<String, String> params) {
        this.params = params;
    }

    public static Parameters parse(final String query) {
        final String paramsDelimiter = "&";
        final List<String> params = StringSplitter.split(paramsDelimiter, query);

        final String paramDelimiter = "=";
        return new Parameters(StringSplitter.getPairs(paramDelimiter, params));
    }

    public String find(final String name) {
        validateParameterNameExist(name);
        return params.get(name);
    }

    private void validateParameterNameExist(final String name) {
        if (!params.containsKey(name)) {
            throw new NoSuchElementException("파라미터를 찾을 수 없습니다 : " + name);
        }
    }
}

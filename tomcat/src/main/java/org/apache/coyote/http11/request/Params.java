package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import utils.StringSplitter;

public class Params {

    private final Map<String, String> params;

    private Params(final Map<String, String> params) {
        this.params = params;
    }

    public static Params parse(final String query) {
        if (query == null || query.equals("")) {
            return new Params(Collections.emptyMap());
        }

        final String paramsDelimiter = "&";
        final List<String> params = StringSplitter.split(paramsDelimiter, query);

        final String paramDelimiter = "=";
        return new Params(StringSplitter.getPairs(paramDelimiter, params));
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

    public Map<String, String> getParams() {
        return params;
    }
}

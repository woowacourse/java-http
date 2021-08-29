package nextstep.jwp.handler;

import nextstep.jwp.http.response.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class Model {

    private Map<String, Object> attributes;

    public Model() {
        this.attributes = new HashMap<>();
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object get(String key) {
        return attributes.get(key);
    }

    public HttpStatus httpStatus() {
        return (HttpStatus) attributes.get("HttpStatus");
    }

    public boolean contains(String key){
        return attributes.containsKey(key);
    }
}

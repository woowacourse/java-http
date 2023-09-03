package org.apache.coyote.requests;

public class RequestBody {

    private final String item;

    public RequestBody() {
        this.item = "";
    }

    public RequestBody(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }
}

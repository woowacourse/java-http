package org.apache.session;

public class SessionResult {
    String jsessionId;
    boolean isNew;

    public SessionResult(String jsessionId, boolean isNew) {
        this.jsessionId = jsessionId;
        this.isNew = isNew;
    }

    public String getJsessionId() {
        return jsessionId;
    }

    public boolean isNew() {
        return isNew;
    }
}

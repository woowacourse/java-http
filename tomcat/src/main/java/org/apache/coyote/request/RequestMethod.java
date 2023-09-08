package org.apache.coyote.request;

public enum RequestMethod {
    GET, POST;

    public static RequestMethod get(final String method) {
        try{
            return RequestMethod.valueOf(method);
        } catch (IllegalArgumentException e){
            return RequestMethod.GET;
        }
    }

    public boolean isPost(){
        return this == POST;
    }

    public boolean isGet(){
        return this == GET;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

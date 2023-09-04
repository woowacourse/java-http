package org.apache.coyote.request;

public enum RequestMethod {
    GET, POST;

    public static RequestMethod get(String method) {
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
}

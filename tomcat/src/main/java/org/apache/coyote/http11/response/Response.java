package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.stream.Collectors;

public class Response {
    private final HttpStatus status;
    private final String contentType;
    private final String responseBody;
    private final String location;
    private final Map<String,String> cookie;

    private Response(Builder builder){
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.responseBody = builder.responseBody;
        this.location = builder.location;
        this.cookie = builder.cookie;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static Response badResponse(HttpStatus httpStatus){
        return builder()
                .status(httpStatus)
                .responseBody("")
                .contentType("html")
                .location(httpStatus.getValue()+".html")
                .build();
    }

    public String getResponse(){
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                makeLocation() + makeCookie(),
                responseBody);
    }

    public Response redirect(String file, String location){
        return new Builder()
                .status(this.status)
                .contentType("html")
                .location(location)
                .cookie(cookie)
                .responseBody(file)
                .build();
    }

    private String makeLocation(){
        if(location==null){
            return "";
        }
        return "location : " + location + "\r\n";
    }

    private String makeCookie(){
        if(cookie == null){
            return "";
        }
        return "Set-Cookie :" + cookie.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    public static class Builder{
        private HttpStatus status;
        private String contentType;
        private String responseBody;
        private String location;
        private Map<String,String> cookie;

        public Builder status(HttpStatus status){
            this.status = status;
            return this;
        }

        public Builder contentType(String contentType){
            this.contentType = contentType;
            return this;
        }

        public Builder responseBody(String responseBody){
            this.responseBody = responseBody;
            return this;
        }

        public Builder location(String location){
            this.location = location;
            return this;
        }

        public Builder cookie(Map<String,String> cookie){
            this.cookie = cookie;
            return this;
        }

        public Response build(){
            return new Response(this);
        }
    }
}

package org.apache.coyote.http11.headers;

public class Extension {
    private final String name;

    private Extension(String name) {
        validateExtension(name);
        this.name = name;
    }

    public static Extension create(String file) {
        String name = file.split("\\.")[1];
        return new Extension(name);
    }

    private void validateExtension(String name) {
        if(!("css".equals(name) || "js".equals(name) || "html".equals(name))){
            throw new IllegalArgumentException("올바른 확장자가 아닙니다.");
        }
    }

    public String getName(){
        return name;
    }
}

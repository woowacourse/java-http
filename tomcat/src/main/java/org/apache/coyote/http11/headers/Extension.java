package org.apache.coyote.http11.headers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Extension {
    private static final Logger log = LoggerFactory.getLogger(Extension.class);

    private final String name;

    private Extension(String name) {
        validateExtension(name);
        this.name = name;
    }

    public static Extension create(String file) {
        int index = file.indexOf(".");
        if (index == -1) {
            return new Extension("html");
        }
        return new Extension(file.substring(index + 1));
    }

    private void validateExtension(String name) {
        if(!("css".equals(name) || "js".equals(name) || "html".equals(name) || "svg".equals(name) || "ico".equals(name))){
            log.info("extension : {} ", name);
            throw new IllegalArgumentException("올바른 확장자가 아닙니다.");
        }
    }

    public String getContentType(){
        if ("svg".equals(name)) {
            return "image/" + name + "+xml";
        }
        return "text/" + name;
    }
}

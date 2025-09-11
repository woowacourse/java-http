package org.apache.catalina.resource;

public class StaticResourceLoader extends ResourceLoader {

    @Override
    public String resolve(String responsePath) {
        return responsePath;
    }
}

package org.qupring.mvc;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationScanner {

    public List<Class<?>> scanForControllers(Class<?> applicationClass) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String applicationPath = classLoader.getResource(applicationClass.getName()).getPath();


        // TODO : 루트 애플리케이션 클래스패스를 받아오고 그 밑으로 스캔 진행
        return List.of();
    }

    public Map<String, String> scanForResources(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource("static");

        List<String> contents = new ArrayList<>();
        if (resourceUrl != null) {
            File folder = new File(resourceUrl.getFile());
            File[] fileList = folder.listFiles();

            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        contents.add(file.getName());
                    }
                }
            }
        } else {
            System.out.println("폴더를 찾을 수 없습니다.");
        }

        final Map<String, String> resources = new HashMap<>();
        for(String file : contents) {
            String[] split = file.split("\\.");
            if(split.length == 2 && split[1].equals("html")) {
                resources.put(split[0], file);
            }
        }
        return resources;
    }
}

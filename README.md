# 톰캣 구현하기

---

## 학습한 내용

### ClassLoader.getSystemResource

`ClassLoader.getSystemResource()` 메소드를 활용해서 간단하게 Class Path 에서 Resource를 찾는 방법을 배우게 되었다.
클래스를 로딩하기 위해서는 class 파일을 바이트로 읽어서 메모리에 로딩하게 된다.
설정 파일이나 다른 파일들은 바이트로 읽기 위해서 `InputStream` 을 얻어야 한다.
즉, 클래스 패스에 존재하는 모든 클래스 파일들, 설정 파일, 그 외 파일들 등등 모든 파일들은 `ClassLoader` 에서 찾을 수 있다.

참고 : [[JAVA] CLASS PATH에서 RESOURCE 찾기](https://whitecold89.tistory.com/9#recentEntries)



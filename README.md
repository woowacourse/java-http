# 톰캣 구현하기

## 구현 내용

- [x] http://localhost:8080/index.html 페이지에 접근 가능하다.
- [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] uri의 QueryString을 파싱하는 기능이 있다.
- [x] HTTP Reponse의 상태 응답 코드를 302로 반환한다.
- [x] POST 로 들어온 요청의 Request Body를 파싱할 수 있다.
- [x] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie 가 존재한다.
- [x] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.

## 미션 진행 중 배운 내용

| 간단하게 참고했던 자료나 확인했던 내용만 적어놓았습니다 :) 추후 추가 예정.

- 파일 위치 읽기
    - `getClass().getResource()` → 상대적인 리소스 경로로 탐색
    - `getClass().getClassLoader()` → 절대적인 경로로 탐색
    - 코드

        ```
         getClass()
            .getClassLoader()
            .getResource(fileName)
            .toString()
        ```

        ```java
        // Paths를 통해서 Path 객체 생성 
        final Path path = Paths.get(getClass()
            .getClassLoader()
            .getResource(fileName)
            .toURI()); 
        
        // 값을 불러옴
        final List<String> actual = Files.readAllLines(path.toAbsolutePath());
        ```
- outputStream
    - BufferedOutputStream과 ByteArrayOutputStream의 차이
    - try-with-resources 사용하기

- InputStream
    - [내용 읽기](https://developer-talk.tistory.com/681)

# 톰캣 구현하기

---

## 레벨1 구현 내용 정리

- [x] GET /index.html 응답하기
  - [x] `http://localhost:8080/index.html` 요청시 index.html 파일을 응답한다.
  - [x] `http://localhost:8080` 으로 요청시 index.html 을 기본으로 응답해준다.
  - [x] 파일 확장자가 명시되지 않은 경우 `.html` 을 붙인 파일을 응답한다.
- [x] CSS 지원하기
  - [x] Accept 에 명시한 MIME 타입으로 반해줄 수 있다.
- [x] Query String 파싱
  - [x] Query String 이 포함된 요청인 경우 이를 처리해준다. 구체적으로 account 로 InMemory DB에 미리 저장되어 있는 사용자를 조회하여 로그를 남긴다. 

## 새롭게 알게 된 내용 (레벨1)

### ClassLoader.getSystemResource

`ClassLoader.getSystemResource()` 메소드를 활용해서 간단하게 Class Path 에서 Resource를 찾는 방법을 배우게 되었다.
클래스를 로딩하기 위해서는 class 파일을 바이트로 읽어서 메모리에 로딩하게 된다.
설정 파일이나 다른 파일들은 바이트로 읽기 위해서 `InputStream` 을 얻어야 한다.
즉, 클래스 패스에 존재하는 모든 클래스 파일들, 설정 파일, 그 외 파일들 등등 모든 파일들은 `ClassLoader` 에서 찾을 수 있다.

참고 : [[JAVA] CLASS PATH에서 RESOURCE 찾기](https://whitecold89.tistory.com/9#recentEntries)

### 로그 테스트

1단계 미션에서 다음과 같은 요구사항을 구현하였다.

```
http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.
그리고 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과가 나오도록 만들자.
```

그런데, 여기서 회원을 조회한 이후에 `로그` 를 통해서 회원을 조회한 결과를 확인하게 된다.

따라서 로그가 제대로 찍혔는지를 확인하는 테스트 코드를 작성하는 법에 대해서 고민하게 되었고,
다음의 글을 참고하여 로그 또한 테스트 코드에서 확인해볼 수 있음을 알게 되었다.

참고 : [추가된 LOG를 JUnit에서 확인하는 방법](https://blog.advenoh.pe.kr/java/%EC%B6%94%EA%B0%80%EB%90%9C-LOG%EB%A5%BC-JUnit-%EC%97%90%EC%84%9C-%ED%99%95%EC%9D%B8%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95/)
<br>
참고 : [JUnit을 이용한 Log 메시지 테스트](https://xlffm3.github.io/java/log-junit-test/)

### Content-Type

HTTP 응답 헤더에 실어지는 `Content-Type` 이 무엇을 하는지에 대해서 알 수 있었다.
`Content-Type` 헤더는 리소스의 `media type` 을 나타내기 위한 헤더로 클라이언트에게 반환되는 컨텐츠의 컨텐츠 유형이 실제로 무엇인지를 나타낸다.
(브라우저들은 어떤 경우에는 MIME 스니핑을 해서 이 헤더의 값을 꼭 따르지는 않는다.)
css 파일과 같은 것을 응답으로 반환해줄 때에는 'text/css' 와 같이 지정해주어야 브라우저에서 정확하게 인식할 수 있음으로 특히 주의해야 한다.
또한 클라이언트 입장에서는 HTTP 요청 헤더의 `Accept` 를 통해서 응답으로 받길 원하는 컨텐츠 형식을 지정해줄 수 있는데,
이 때 우선순위에 따라서 여러개를 명시해줄 수 있게 된다.

[RFC 문서 Accept](https://www.rfc-editor.org/rfc/rfc2616#page-100)

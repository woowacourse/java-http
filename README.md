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

참고 : [RFC 문서 Accept](https://www.rfc-editor.org/rfc/rfc2616#page-100)

---

## 레벨2 구현 내용 정리

- [x] HTTP Status Code 302
  - [x] `http://localhost:8080/login` 요청시 login.html 파일을 응답한다.
  - [x] `http://locahost:8080/login` 페이지에서 입력한 아이디와 비밀번호는 query string으로 전달된다.
  - [x] `http://localhost:8080/login` 페이지에서 로그인 버튼 클릭시 302 상태코드를 응답한다.
  - [x] 로그인 성공시에는 index.html로 리다이렉트한다.
  - [x] 로그인 실패시에는 401.html로 리다이렉트한다.
  

- [x] POST 방식으로 회원가입
  - [x] 기존 로그인 POST 방식으로 변경
    - [x] `login.html` form 태그 변경
    - [x] 기존 로그인 방식을 `POST` 로 변경
    - [x] `HttpResopnse` 에서 responseBody 생성하도록 변경
    - [x] `StartLine` 및 `HttpMethod` 도출
    - [x] 기존 쿼리파라미터를 받던 `LoginHandler` 에서 바디를 읽도록 수정
    - [x] `ContentLength` 헤더를 이용해 POST 요청의 바디를 읽도록 구현
  - [x] 회원가입 처리
    - [x] `http://localhost:8080/register` GET 요청시 register.html 을 응답한다.
    - [x] 회원가입 요청(POST) 받기
      - [x] `http://localhost:8008/register` POST 요청시 회원가입 처리를 한다.
    - [x] 회원가입 요청 처리
      - [x] 회원가입 성공시 index.html 로 리다이렉트한다.
      - [x] 회원가입 실패시 ExistUserException 예외를 던진다.


- [x] Cookie에 JSESSIONID 값 저장하기
  - [x] 로그인 성공시에 `JSESSIONID` 쿠키를 생성해서 응답(Set-Cookie)에 함께 보낸다.
  - [x] 만약 HTTP 요청 헤더의 Cookie에 `JSESSIONID` 가 존재하면 Set-Cookie 응답을 보내지 않는다.
 
## 새롭게 알게 된 내용 (레벨2)

### Content-Length 헤더 필드

큰 의미가 없다고 생각했던 `Content-Length` 헤더 필드에 대해서 직접 POST 요청의 바디 부분을 읽으면서 그 사용용도를 알게 되었다.
`Content-Length` 는 수신자에게 보내지는 바이트 단위를 가지는 요청 바디(Request Body)의 크기를 나타낸다.
수신자쪽에서는 해당 바이트의 크기만큼 읽어들여 처리할 수 있다.

```java
final int contentLength = Integer.parseInt(contentLengthHeader.trim());
final char[] buffer = new char[contentLength];
bufferedReader.read(buffer, 0, contentLength);

return new String(buffer);
```

참고 : [Content-Length - HTTP](https://developer.mozilla.org/ko/docs/Web/HTTP/Headers/Content-Length)

### InMemoryDB rollback

회원가입을 구현하고, 이를 테스트하기 위해 단위 테스트(`RegisterHandlerTest`) 를 작성하다보니 각 단위테스트가 격리되지 못하는 문제가 발생하였다.
앞선 단위 테스트에서 저장한 `User` 가 다른 단위테스트에도 영향을 끼치는 것이다.

<img width="1622" alt="KakaoTalk_Photo_2022-09-06-06-51-11" src="https://user-images.githubusercontent.com/57028386/188536783-314637b5-2b9d-460f-9e71-7e45fafbf42a.png">

따라서 이를 격리해줄 필요성이 있게 되었고, 다음과 같은 메소드를 `InMemoryUserRepositroy` 에 만들고,
`BeforeEach` 를 통해서 각 단위 테스트 진행 전에 호출하도록 구현해주었다.
하지만 아직 테스트를 위해서 Production 코드에 `rollback()` 과 같은 메소드를 만들어 두는 것이 최선인지에 대해서는 고민이든다.
조금 더 나은 방법을 고민해보아야겠다.

```java
public static void rollback() {
    database.clear();
    final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
    database.put(user.getAccount(), user);
}
```

```java
class RegisterHandlerTest {

  @BeforeEach
  void setUp() {
    InMemoryUserRepository.rollback();
  }
    
    ...
}
```

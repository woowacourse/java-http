# 🚀 1단계 - HTTP 서버 구현하기

# 미션 설명

간단한 HTTP 서버를 만들어보자.
저장소에서 소스코드를 받아와서 메인 클래스를 실행하면 HTTP 서버가 실행된다.
웹브라우저로 로컬 서버([http://localhost:8080](http://localhost:8080/))에 접속하면 `Hello world!`가 보인다.
정상 동작을 확인했으면 새로운 기능을 추가해보자.

![Jun-15-2022 11-58-49.gif](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/b21d3e3f97694decb2457219b2bf5d81)

# 기능 요구 사항

## 1. GET /index.html 응답하기

인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있도록 만들자.
`Http11ProcessorTest` 테스트 클래스의 모든 테스트를 통과하면 된다.
브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```plaintext
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

## 실행 결과

![Jun-08-2022 15-12-13.gif](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/e3113da5b2c044a4909ff92886d0beb5)

# 

## 2. CSS 지원하기

![image](https://user-images.githubusercontent.com/15669435/127278698-0c3d76cd-e1e2-4023-8023-a06d1590bad6.png)

인덱스 페이지에 접속하니까 화면이 이상하게 보인다.
개발자 도구를 열어서 에러 메시지를 체크해보니 브라우저가 CSS를 못 찾고 있다.
사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 기능을 추가하자.

```plaintext
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

## 실행 결과

![Jun-15-2022 11-45-55.gif](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/fbb9adb0c9cd45cf9a7fb7847fc9dd27)

# 

## 3. Query String 파싱

http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.
그리고 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과가 나오도록 만들자.

## 실행 결과

![Jun-15-2022 13-19-25.gif](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/58458cc89e1244509f0d3d5cbb7caad4)

# 체크리스트

- http://localhost:8080/index.html 페이지에 접근 가능하다.
- 접근한 페이지의 js, css 파일을 불러올 수 있다.
- uri의 QueryString을 파싱하는 기능이 있다.

# 힌트

## 1. GET /index.html 응답하기

1. 먼저

    

   첫 번째 라인(Request URI)

    

   을 읽어오자.

   - 첫 줄만 읽어와도 첫 미션은 해결 가능하다.

2. 나머지 http request header는 어떻게 읽을까?

   - **IOStreamTest**를 참고해서 http request를 읽어오자.

   - line이

      

     null

     인 경우에 예외 처리를 해준다. 그렇지 않으면 무한 루프에 빠진다.

     - `if (line == null) { return; }`

   - 헤더 마지막은 `while (!"".equals(line)) {}`으로 확인 가능하다.

3. http request의

    

   첫 번째 라인

   에서 request uri를 추출한다.

   - `line.split(" ");`을 활용해서 문자열을 분리한다.

4. **FileTest**를 참고해서 요청 url에 해당되는 파일을 **resource** 디렉토리에서 읽는다.

# 

## 2. CSS 지원하기

1. 응답 헤더의 **Content-Type**을 **text/html**로 보내면 브라우저는 HTML 파일로 인식하기 때문에 CSS가 정상적으로 동작하지 않는다.
2. CSS인 경우 응답 헤더의 **Content-Type**을 **text/css**로 전송한다.
3. Content-Type은 확장자를 통해 구분할 수도 있으며, 요청 헤더의 Accept를 활용할 수도 있다.

## 생각해보기 🤔

개발자 도구 > 네트워크 항목을 열어 놓고 페이지를 리로드하니 URL로 접속하여 받은 응답 외에도 css, js, favicon 같은 파일도 호출되고 있다.

index.html 페이지만 접근했는데 CSS 같은 정적 파일들은 어떻게 호출된걸까?

# 

## 3. Query String 파싱

1. http request의 **첫 번째 라인**에서 request uri를 추출한다.
2. login.html 파일에서 태그에 name을 추가해준다.
3. 추출한 request uri에서 접근 경로와 **이름=값**으로 전달되는 데이터를 추출해서 User 객체를 조회하자.

```java
String uri = "/login?account=gugu&password=password";
int index = uri.indexOf("?");
String path = uri.substring(0, index);
String queryString = uri.substring(index + 1);
```

1. **InMemoryUserRepository**를 사용해서 미리 가입되어 있는 회원을 조회하고 로그로 확인해보자.
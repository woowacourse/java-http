# 만들면서 배우는 스프링

## 톰캣 구현하기

### 학습목표
- 웹 서버 구현을 통해 HTTP 이해도를 높인다.
- HTTP의 이해도를 높혀 성능 개선할 부분을 찾고 적용할 역량을 쌓는다.
- 서블릿에 대한 이해도를 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

### 시작 가이드
1. 미션을 시작하기 전에 파일, 입출력 스트림 학습 테스트를 먼저 진행합니다.
    - [File, I/O Stream](study/src/test/java/study)
    - 나머지 학습 테스트는 다음 강의 시간에 풀어봅시다.
2. 학습 테스트를 완료하면 LMS의 1단계 미션부터 진행합니다.

## 학습 테스트
1. [File, I/O Stream](study/src/test/java/study)
2. [HTTP Cache](study/src/test/java/cache)
3. [Thread](study/src/test/java/thread)

---

힌트

## GET /index.html 응답하기
   
1. 먼저 첫 번째 라인(Request URI) 을 읽어오자.
   첫 줄만 읽어와도 첫 미션은 해결 가능하다.
   
2. 나머지 http request header는 어떻게 읽을까?
   - IOStreamTest를 참고해서 http request를 읽어오자.
   - line이 null인 경우에 예외 처리를 해준다. 그렇지 않으면 무한 루프에 빠진다.
     - `if (line == null) { return; }`
   - 헤더 마지막은 `while (!"".equals(line)) {}`으로 확인 가능하다.

3. http request의 첫 번째 라인에서 request uri를 추출한다.
   - `line.split(" ");`을 활용해서 문자열을 분리한다.

4. FileTest를 참고해서 요청 url에 해당되는 파일을 resource 디렉토리에서 읽는다.


---

## CSS 지원하기

1. 응답 헤더의 Content-Type을 text/html로 보내면 브라우저는 HTML 파일로 인식하기 때문에 CSS가 정상적으로 동작하지 않는다.

2. CSS인 경우 응답 헤더의 Content-Type을 text/css로 전송한다.
   
3. Content-Type은 확장자를 통해 구분할 수도 있으며, 요청 헤더의 Accept를 활용할 수도 있다.
   
### 생각해보기 🤔

개발자 도구 > 네트워크 항목을 열어 놓고 페이지를 리로드하니 URL로 접속하여 받은 응답 외에도 css, js, favicon 같은 파일도 호출되고 있다.

index.html 페이지만 접근했는데 CSS 같은 정적 파일들은 어떻게 호출된걸까?

---

## Query String 파싱
1. http request의 첫 번째 라인에서 request uri를 추출한다.
2. login.html 파일에서 태그에 name을 추가해준다.
3. 추출한 request uri에서 접근 경로와 이름=값으로 전달되는 데이터를 추출해서 User 객체를 조회하자.
```java
String uri = "/login?account=gugu&password=password";
int index = uri.indexOf("?");
String path = uri.substring(0, index);
String queryString = uri.substring(index + 1);
```
4. InMemoryUserRepository를 사용해서 미리 가입되어 있는 회원을 조회하고 로그로 확인해보자.

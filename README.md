# 톰캣 구현하기

- HTTP와 서블릿에 대한 이해도를 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

<br/>

### 클래스 다이어그램

![image](./docs/class.png)

<br/>

### 미션 일정

> 마감 시간은 18:00으로 고정

- 9/2 (금) 첫 PR 요청
- 9/6 (화) 첫 PR 전체 머지
- 9/8 (금) 마지막 PR 요청
- 9/13 (화) 마지막 PR 전체 머지

<br/>

# 1단계

### 기능 요구사항

- GET /index.html 응답하기
- CSS 지원하기
- Query String 파싱

### 체크리스트

- [x] http://localhost:8080/index.html 페이지에 접근 가능하다.
- [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] uri의 QueryString을 파싱하는 기능이 있다.

<br/>

# 2단계

### 기능 요구사항

- HTTP Status Code 302
- POST 방식으로 회원가입
- Cookie에 JSESSIONID 값 저장하기
- Session 구현하기

### 체크리스트

- [x] HTTP Reponse의 상태 응답 코드를 302로 반환한다.
- [x] POST로 들어온 요청의 Request Body를 파싱할 수 있다.
- [x] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
- [x] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.

<br/>

# 3단계

### 기능 요구사항

- HttpRequest 클래스 구현하기
- HttpResponse 클래스 구현하기
- Controller 인터페이스 추가하기

### 체크리스트

- [x] HTTP Request, HTTP Response 클래스로 나눠서 구현했다.
- [x] Controller 인터페이스와 RequestMapping 클래스를 활용하여 if절을 제거했다.

<br/>

# 4단계

### 기능 요구사항

- Executors로 Thread Pool 적용
- 동시성 컬렉션 사용하기

### 체크리스트

- [ ] Executors로 만든 ExecutorService 객체를 활용하여 스레드 처리를 하고 있다.

<br/>

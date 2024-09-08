# 1단계 - HTTP 서버 구현하기

- 아래 순서는 구현 순서대로 작성되었습니다.

### 1. GET /index.html 응답하기

- HTTP Start Line 읽기
  - HTTP Request의 첫 번째 줄을 읽는다.
- HTTP Start Line을 요소 별로 구분하기
  - HTTP method, Request Uri, HTTP version으로 구분하기
- HTTP Request 요청 url에 해당되는 파일을 resource 디렉토리에서 읽는다.

### 2. CSS 지원하기

- '.css' 파일을 받았을 경우, `Content-Type: text/css;charset=utf-8;`로 설정
  - '.js', '.csv' 파일의 경우, `text/javascript`, `image/csv+xml`로 설정함

### 리펙터링

- Tomcat 과 Spring MVC의 구조를 참고하여 전반적인 구조를 설정한다.
  1. ServletContainer에 등록된 핸들러로 요청할 경우, View 객체로 처리한다.
  2. 정적 파일을 요청할 경우, Tomcat 내부에서 처리한다.

- 구조도
  ![img.png](step1-structure.png)

### 3. Query String 파싱

- `/login?account=gugu&password=password`으로 접속하면 로그인 페이지(login.html)를 보여주기
  - 유저 정보('account', 'password')가 레포지토리 정보와 일치할 경우, 로그를 남딘다.

# 3단계 - 리팩터링 (1,2 단계)

### 1. HttpRequest 클래스 구현하기

- HttpRequest에 구성 요소 추가하기
  - Header 추가하기
  - Body 추가하기
- HttpRequest 사용의 편의성을 위해, 빌더 추가하기

### 2. HttpResponse 클래스 구현하기

- HttpResponse 구현하기
  - HttpMethod 추가하기
  - Header 추가하기
  - Body 추가하기
- HttpResponse 사용의 편의성을 위해, 빌더 추가하기

### 기존 구현 변경하기

- 


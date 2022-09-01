# 톰캣 구현하기

## 기능 요구사항

### 정적 파일 서빙

```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive

```

start line = `HTTP Method + URL + HTTP version`

* HTTP 메서드가 GET이고 URL이 올바른 경우 해당하는 경로의 자원을 응답한다.


#### 예외

* HTTP 메서드가 올바르지 않은 경우: 405 Method Not Allowed 응답
* 존재하지 않는 자원을 요청하는 경우: 404 Not Found 응답과 함께 404.html을 응답

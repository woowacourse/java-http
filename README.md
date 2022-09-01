# 수달 톰캣 구현하기

## 요구사항
### 1. GET /index.html 응답하기
- [ ] http://localhost:8080/index.html 페이지에 접근 가능하다.

```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
실행 결과
Jun-08-2022 15-12-13.gif

```
### 2. CSS 지원하기
- [ ] 접근한 페이지의 js, css 파일을 불러올 수 있다.
```
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
실행 결과
Jun-15-2022 11-45-55.gif
```

3. Query String 파싱
- [ ] uri의 QueryString을 파싱하는 기능이 있다.
```java
실행 결과
Jun-15-2022 13-19-25.gif


```





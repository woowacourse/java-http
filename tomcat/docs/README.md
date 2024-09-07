# 톰캣 구현하기 v1


## 기능 요구사항

1. GET /index.html 응답하기
2. CSS 지원하기 
3. Query String 파싱

### 1. GET /index.html 응답하기
**요청**
```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```
**응답**
src/main/resources/static/index.html


### 2. CSS 지원하기
**요청**
```http request
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

**응답**
src/main/resources/static/css/style.css

### 3. Query String 파싱
- [ ] ?, ; URL 컴포넌트 별 분리  
- [ ] & 기준 Query key value 분리

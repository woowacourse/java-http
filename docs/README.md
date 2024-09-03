# 레벨 4 미션

### 질문
* [테스트](study.IOStreamTest.OutputStream_학습_테스트.BufferedOutputStream을_사용하면_버퍼링이_가능하다)를 수행하는데 아래와 같은 WARNING이 발생하였다. 무엇일까?
알아보자.  

```text
WARNING: A Java agent has been loaded dynamically (C:\Users\User\.gradle\caches\modules-2\files-2.1\net.bytebuddy\byte-buddy-agent\1.14.16\4a451ee6484abac3a498df0f3b33ed00a6fced4d\byte-buddy-agent-1.14.16.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
```
![img.png](img.png)

* BufferedOutputStream와 ByteArrayOutputStream의 차이
* inputStream에서 바이트로 반환한 값을 문자열로 어떻게 바꿈
  * [참고](https://www.baeldung.com/convert-input-stream-to-string)
* 버퍼 크기를 지정하지 않으면 버퍼의 기본 사이즈는 얼마일까?
  * DEFAULT_BUFFER_SIZE = 8192
* 
* 

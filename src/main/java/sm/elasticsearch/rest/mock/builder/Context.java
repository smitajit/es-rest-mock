package sm.elasticsearch.rest.mock.builder;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Context implements Comparable<Context> {

    private String method;
    private String endPoint;
    private Map<String, String> params;
    private Header[] headers;
    private int expectedStatusCode;
    private ContentType expectedContentType;
    private String expectedResponseBody;
    private Exception expectedError;
    private Header[] expectedHeaders;
    private boolean globalContext;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header... headers) {
        this.headers = headers;
    }

    public Header[] getExpectedHeaders() {
        return expectedHeaders;
    }

    public void setExpectedHeaders(Header... expectedHeaders) {
        this.expectedHeaders = expectedHeaders;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public ContentType getExpectedContentType() {
        return expectedContentType;
    }

    public void setExpectedContentType(ContentType expectedContentType) {
        this.expectedContentType = expectedContentType;
    }

    public String getExpectedResponseBody() {
        return expectedResponseBody;
    }

    public void setExpectedResponseBody(String expectedResponseBody) {
        this.expectedResponseBody = expectedResponseBody;
    }

    public Exception getExpectedError() {
        return expectedError;
    }

    public void setExpectedError(Exception expectedError) {
        this.expectedError = expectedError;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isGlobalContext() {
        return globalContext;
    }

    public void setGlobalContext(boolean globalContext) {
        this.globalContext = globalContext;
    }


    @Override
    public int compareTo(Context that) {
        final AtomicInteger score = new AtomicInteger(0);

        String trappedMethod = this.getMethod();
        String actualMethod = that.getMethod();
        if (!trappedMethod.equals(actualMethod)) {
            return 0;
        }

        //endpoint checking. Exact match will increase 2 points and regex matching will increase 1 point
        String trappedEndPoint = this.getEndPoint();
        String actualEndPoint = that.getEndPoint();
        if (trappedEndPoint.equals(actualEndPoint)) {
            score.set(score.get() + 2);
        } else if (actualEndPoint.matches(trappedEndPoint)) {
            score.incrementAndGet();
        }

        //params matching
        Map<String, String> trappedParams = this.getParams();
        Map<String, String> actualParams = that.getParams();
        if (null != actualParams) {
            if (null != trappedParams) {
                for (String paramName : actualParams.keySet()) {
                    if (null != trappedParams.get(paramName)) {
                        score.incrementAndGet();
                    }
                }
            }
        }

        //header matching
        Header[] trappedHeaders = this.getHeaders();
        Header[] actualHeaders = that.getHeaders();
        if (null != actualHeaders) {
            if (null != trappedHeaders) {
                for (Header h : actualHeaders) {
                    for (Header h1 : trappedHeaders) {
                        if (h1.getName().equals(h.getName())) {
                            score.incrementAndGet();
                        }
                    }
                }
            }
        }

        //increasing the score in case non global match
        if (score.get() > 0 && !this.isGlobalContext()) {
            score.incrementAndGet();
        }

        return score.get();
    }
}

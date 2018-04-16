package com.github.smitajit.elasticsearch.rest.mock.builder;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stores the Request Context and Response Context for the mocked rest call
 */
public class MockContext implements Comparable<MockContext> {

    private boolean globalContext = false;
    private RequestContext requestContext = new RequestContext();
    private ResponseContext responseContext = new ResponseContext();

    public boolean isGlobalContext() {
        return globalContext;
    }

    public void setGlobalContext(boolean globalContext) {
        this.globalContext = globalContext;
    }


    @Override
    public int compareTo(MockContext that) {
        final AtomicInteger score = new AtomicInteger(0);

        String trappedMethod = this.requestContext.getMethod();
        String actualMethod = that.requestContext.getMethod();
        if (!trappedMethod.equals(actualMethod)) {
            return 0;
        }

        //endpoint checking. Exact match will increase 2 points and regex matching will increase 1 point
        String trappedEndPoint = this.requestContext.getEndPoint();
        String actualEndPoint = that.requestContext.getEndPoint();
        if (trappedEndPoint.equals(actualEndPoint)) {
            score.set(score.get() + 2);
        } else if (actualEndPoint.matches(trappedEndPoint)) {
            score.incrementAndGet();
        }

        //params matching
        Map<String, String> trappedParams = this.requestContext.getParams();
        Map<String, String> actualParams = that.requestContext.getParams();
        if (null != actualParams) {
            if (null != trappedParams) {
                for (String paramName : actualParams.keySet()) {
                    if (null != trappedParams.get(paramName) && trappedParams.get(paramName).equals(actualParams.get(paramName))) {
                        score.incrementAndGet();
                    }
                }
            }
        }

        //header matching
        Header[] trappedHeaders = this.requestContext.getHeaders();
        Header[] actualHeaders = that.requestContext.getHeaders();
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

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public ResponseContext getResponseContext() {
        return responseContext;
    }

    public void setResponseContext(ResponseContext responseContext) {
        this.responseContext = responseContext;
    }

    /**
     * Class to represent the request context for mocked rest call
     */
    public class RequestContext {
        private String method;
        private String endPoint;
        private Map<String, String> params;
        private Header[] headers;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

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
    }

    /**
     * class to represent the response context for the rest call
     */
    public class ResponseContext {
        private int statusCode;
        private ContentType contentType;
        private String responseBody;
        private Exception error;
        private Header[] headers;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public ContentType getContentType() {
            return contentType;
        }

        public void setContentType(ContentType contentType) {
            this.contentType = contentType;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public Exception getError() {
            return error;
        }

        public void setError(Exception error) {
            this.error = error;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }
    }
}

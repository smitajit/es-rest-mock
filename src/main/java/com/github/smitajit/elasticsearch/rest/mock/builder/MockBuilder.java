package com.github.smitajit.elasticsearch.rest.mock.builder;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import com.github.smitajit.elasticsearch.rest.mock.ESRestMockCore;

import java.util.Map;

public class MockBuilder {

    private MockContext context = new MockContext();

    /**
     * Response will be mocked for the httpMethod
     *
     * @param httpMethod the expected httpMethod
     * @return MockBuilder
     */
    public MockBuilder forMethod(String httpMethod) {
        assert null != httpMethod;
        context.getRequestContext().setMethod(httpMethod);
        return this;
    }

    /**
     * Response will be mocked for this endPoint
     * The endpoint also supports java regular expression
     *
     * @param endPoint expected endPoint
     * @return MockBuilder
     */
    public MockBuilder forEndPoint(String endPoint) {
        assert null != endPoint;
        context.getRequestContext().setEndPoint(endPoint);
        return this;
    }

    /**
     * Response will be mocked for these params
     *
     * @param params expected params
     * @return MockBuilder
     */
    public MockBuilder forParams(Map<String, String> params) {
        context.getRequestContext().setParams(params);
        return this;
    }

    /**
     * Response will be mocked for this headers
     *
     * @param headers expected headers
     * @return
     */
    public MockBuilder forHeaders(Header... headers) {
        context.getRequestContext().setHeaders(headers);
        return this;
    }

    /**
     * Wheather the rest call mock should effect globally or threadlocal specific
     *
     * @return
     */
    public MockBuilder useGlobal() {
        this.context.setGlobalContext(true);
        return this;
    }


    /**
     * Mocking can expect a error response
     *
     * @param error expected error
     * @return MockBuilder
     */
    public MockBuilder expectError(Exception error) {
        context.getResponseContext().setError(error);
        return this;
    }

    /**
     * Mocking can expect a http response
     *
     * @param responseCode expected responseCode
     * @param responseBody expected responseBody
     * @param contentType expected contentType
     * @param headers expected headers
     * @return MockBuilder
     */
    public MockBuilder expectResponse(int responseCode, String responseBody, ContentType contentType, Header... headers) {
        context.getResponseContext().setContentType(contentType);
        context.getResponseContext().setStatusCode(responseCode);
        context.getResponseContext().setResponseBody(responseBody);
        context.getResponseContext().setHeaders(headers);
        return this;
    }

    /**
     * Build will publish the MockContext to the Threadlocal cache or global cache based useGlobal flag
     *
     * @return the MockContext
     */
    public MockContext mock() {
        ESRestMockCore.putContext(this.context);
        return this.context;
    }
}

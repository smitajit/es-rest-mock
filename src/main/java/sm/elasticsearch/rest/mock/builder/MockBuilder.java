package sm.elasticsearch.rest.mock.builder;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import sm.elasticsearch.rest.mock.ESRestMockCore;

import java.util.Map;

public class MockBuilder {

    public Context context = new Context();

    /**
     * Response will be mocked for the httpMethod
     *
     * @param httpMethod
     * @return
     */
    public MockBuilder forMethod(String httpMethod) {
        assert null != httpMethod;
        context.setMethod(httpMethod);
        return this;
    }

    /**
     * Response will be mocked for this endPoint
     * The endpoint also supports regular expression
     *
     * @param endPoint
     * @return
     */
    public MockBuilder forEndPoint(String endPoint) {
        assert null != endPoint;
        context.setEndPoint(endPoint);
        return this;
    }

    /**
     * Response will be mocked for these params
     *
     * @param params
     * @return
     */
    public MockBuilder forParams(Map<String, String> params) {
        context.setParams(params);
        return this;
    }

    /**
     * Response will be mocked for this headers
     *
     * @param headers
     * @return
     */
    public MockBuilder forHeaders(Header... headers) {
        context.setHeaders(headers);
        return this;
    }

    /**
     * Wheather the rest call mock should effect globally or threadlocal specific
     *
     * @param useGLobal
     * @return
     */
    public MockBuilder useGlobal(boolean useGLobal) {
        this.context.setGlobalContext(useGLobal);
        return this;
    }


    /**
     * Mocking can expect a error response
     *
     * @param error
     * @return
     */
    public MockBuilder expectError(Exception error) {
        context.setExpectedError(error);
        return this;
    }

    /**
     * Mocking can expect a http reponse
     *
     * @param responseCode
     * @param responseBody
     * @param contentType
     * @param headers
     * @return
     */
    public MockBuilder expectReponse(int responseCode, String responseBody, ContentType contentType, Header... headers) {
        context.setExpectedContentType(contentType);
        context.setExpectedStatusCode(responseCode);
        context.setExpectedResponseBody(responseBody);
        context.setExpectedHeaders(headers);
        return this;
    }

    /**
     * Build will publish the MockContext to the Threadlocal cache or global cache based useGlobal flag
     *
     * @return the MockContext
     */
    public Context build() {
        ESRestMockCore.putContext(this.context);
        return this.context;
    }
}

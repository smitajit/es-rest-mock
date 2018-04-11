package sm.elasticsearch.rest.mock.handler;

import javassist.util.proxy.MethodHandler;
import org.apache.http.Header;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import sm.elasticsearch.rest.mock.ESRestMockCore;
import sm.elasticsearch.rest.mock.builder.MockContext;
import sm.elasticsearch.rest.mock.util.MockException;
import sm.elasticsearch.rest.mock.util.Utils;

import java.lang.reflect.Method;
import java.util.Map;

public class RestMethodHandler implements MethodHandler {

    public Object invoke(Object o, Method method, Method method1, Object[] objects) throws Throwable {
        MethodArgs methodArgs = extractMethodArgs(objects);
        MockContext context = ESRestMockCore.getContext(methodArgs.method, methodArgs.endPoint, methodArgs.params, methodArgs.headers);
        if (null == context) {
            throw new MockException("Mocked rest call not found for method [" + methodArgs.method + "] and endpoint [" + methodArgs.endPoint + "]");
        }

        InvokeResponseListener invokeResponseListener = new InvokeResponseListener(methodArgs.responseListener);
        try {
            Response response = Utils.createResponse(context);
            invokeResponseListener.onSuccess(response);
        } catch (Exception e) {
            invokeResponseListener.onFailure(e);
        }
        return invokeResponseListener.get();
    }

    class InvokeResponseListener implements ResponseListener {

        private ResponseListener delegate;
        private Response response;
        private Exception exception;

        InvokeResponseListener(ResponseListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onSuccess(Response response) {
            if (null != delegate) {
                delegate.onSuccess(response);
            } else {
                this.response = response;
            }
        }

        @Override
        public void onFailure(Exception exception) {
            if (null != delegate) {
                delegate.onFailure(exception);
            } else {
                this.exception = exception;
            }
        }

        public Response get() throws Exception {
            if (null != exception) {
                throw exception;
            } else {
                return this.response;
            }
        }
    }

    private MethodArgs extractMethodArgs(Object[] args) {
        if (args.length < 2) {
            throw new MockException("Method and endpoint not found for Rest the call");
        }

        MethodArgs mArgs = new MethodArgs();
        mArgs.method = String.valueOf(args[0]);
        mArgs.endPoint = String.valueOf(args[1]);

        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Map) {
                    mArgs.params = (Map<String, String>) arg;
                } else if (arg instanceof Header[]) {
                    mArgs.headers = (Header[]) arg;
                } else if (arg instanceof HttpAsyncResponseConsumerFactory) {
                    mArgs.httpAsyncResponseConsumerFactory = (HttpAsyncResponseConsumerFactory) arg;
                } else if (arg instanceof ResponseListener) {
                    mArgs.responseListener = (ResponseListener) arg;
                }
            }
        }
        return mArgs;
    }

    class MethodArgs {
        private String method;
        private String endPoint;
        private Header[] headers;
        private Map<String, String> params;
        private HttpAsyncResponseConsumerFactory httpAsyncResponseConsumerFactory;
        private ResponseListener responseListener;
    }
}

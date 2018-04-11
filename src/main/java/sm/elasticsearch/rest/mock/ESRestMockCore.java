package sm.elasticsearch.rest.mock;

import sm.elasticsearch.rest.mock.builder.MockContext;
import sm.elasticsearch.rest.mock.builder.MockBuilder;
import org.apache.http.Header;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ESRestMockCore {

    private static ThreadLocal<List<MockContext>> tLocalContext = ThreadLocal.withInitial(() -> new ArrayList<>());
    private static List<MockContext> globalContext = new ArrayList<>();


    /**
     * Puts the context in to ThreadLocal cache or globalCache
     *
     * @param context
     */
    public static void putContext(MockContext context) {
        if (context.isGlobalContext()) {
            globalContext.add(context);
        } else {
            tLocalContext.get().add(context);
        }
    }

    /**
     * Gets the most matched contexts. Threadlocal match has more score than global match
     *
     * @param method
     * @param endPoint
     * @param params
     * @param headers
     * @return
     */
    public static MockContext getContext(String method, String endPoint, Map<String, String> params, Header... headers) {

        final AtomicInteger prevScore = new AtomicInteger();
        final AtomicReference<MockContext> result = new AtomicReference<>();

        MockContext userContext = new MockContext();
        userContext.getRequestContext().setMethod(method);
        userContext.getRequestContext().setEndPoint(endPoint);
        userContext.getRequestContext().setParams(params);
        userContext.getRequestContext().setHeaders(headers);

        //Finds the best scored context
        Consumer<MockContext> compareAndUpdate = (cachedContext) -> {
            int res = cachedContext.compareTo(userContext);
            if (res > prevScore.get()) {
                prevScore.set(res);
                result.set(cachedContext);
            }

        };

        tLocalContext.get().forEach(compareAndUpdate);
        globalContext.forEach(compareAndUpdate);

        return result.get();
    }


    /**
     * @return a new MockBuilder
     */
    public static MockBuilder newBuilder() {
        return new MockBuilder();
    }

}

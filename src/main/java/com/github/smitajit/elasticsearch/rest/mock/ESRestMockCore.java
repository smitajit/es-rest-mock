package com.github.smitajit.elasticsearch.rest.mock;

import com.github.smitajit.elasticsearch.rest.mock.builder.MockContext;
import com.github.smitajit.elasticsearch.rest.mock.builder.MockBuilder;
import org.apache.http.Header;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ESRestMockCore {

    private static ThreadLocal<List<MockContext>> tLocalContext = ThreadLocal.withInitial(ArrayList::new);
    private static List<MockContext> globalContext = new ArrayList<>();

    /**
     * Puts the context in to ThreadLocal cache or globalCache
     *
     * @param context the mocked context
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
     * @param method called method
     * @param endPoint called endPoint
     * @param params called params
     * @param headers called headers
     * @return the mock context
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
    public static MockBuilder newMocker() {
        return new MockBuilder();
    }

    /**
     * Clears the MockContext cache. if clearGlobalCache then global cache will also be cleared
     * @param clearGlobalCache flag to clear the global context cache
     */
    public static void clear(boolean clearGlobalCache) {
        tLocalContext.get().clear();
        if(clearGlobalCache) {
            globalContext.clear();
        }

    }
}

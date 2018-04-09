package sm.elasticsearch.rest.mock;

import sm.elasticsearch.rest.mock.builder.Context;
import sm.elasticsearch.rest.mock.builder.MockBuilder;
import org.apache.http.Header;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ESRestMockCore {

    private static ThreadLocal<List<Context>> tLocalContext = ThreadLocal.withInitial(() -> new ArrayList<>());
    private static List<Context> globalContext = new ArrayList<>();


    public static void putContext(Context context) {
        if (context.isGlobalContext()) {
            globalContext.add(context);
        } else {
            tLocalContext.get().add(context);
        }
    }

    public static Context getContext(String method, String endPoint, Map<String, String> params, Header... headers) {

        AtomicInteger prevScore = new AtomicInteger();
        AtomicReference<Context> result = new AtomicReference<>();
        Context actual = new Context();
        actual.setMethod(method);
        actual.setEndPoint(endPoint);
        actual.setParams(params);
        actual.setHeaders(headers);
        tLocalContext.get().forEach(c -> {
            int res = c.compareTo(actual);
            if (res > prevScore.get()) {
                prevScore.set(res);
                result.set(c);

            }
        });
        globalContext.forEach(c -> {
            int res = c.compareTo(actual);
            if (res > prevScore.get()) {
                prevScore.set(res);
                result.set(c);
            }
        });

        return result.get();
    }

    public static MockBuilder newBuilder() {
        return new MockBuilder();
    }

}

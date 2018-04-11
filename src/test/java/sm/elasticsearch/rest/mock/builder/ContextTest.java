package sm.elasticsearch.rest.mock.builder;

import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ContextTest {

    @Test
    public void testCompareToZeroResult() {
        MockContext context = new MockContext();
        context.getRequestContext().setMethod("GET");
        context.getRequestContext().setEndPoint("/_cat/indices");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.getRequestContext().setParams(params);


        MockContext context1 = new MockContext();
        context1.getRequestContext().setMethod("POST");

        int result = context.compareTo(context1);
        Assert.assertEquals("Because method is not same should return 0", 0, result);
    }

    @Test
    public void testCompareToNonZeroResult() {
        MockContext context = new MockContext();
        context.getRequestContext().setMethod("GET");
        context.getRequestContext().setEndPoint("/_cat/indices");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.getRequestContext().setParams(params);


        MockContext context1 = new MockContext();
        context1.getRequestContext().setMethod("GET");
        context1.getRequestContext().setEndPoint("/_cat/indices");

        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 3. Because Endpoint Exact match and is not global context", 3, result);
    }

    @Test
    public void testCompareToNonZeroResult1() {
        MockContext context = new MockContext();
        context.getRequestContext().setMethod("GET");
        context.getRequestContext().setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.getRequestContext().setParams(params);


        MockContext context1 = new MockContext();
        context1.getRequestContext().setMethod("GET");
        context1.getRequestContext().setEndPoint("/_cat/indices");

        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 2. Because Endpoint regex match and is not global context", 2, result);
    }


    @Test
    public void testCompareToNonZeroResult2() {
        MockContext context = new MockContext();
        context.getRequestContext().setMethod("GET");
        context.getRequestContext().setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.getRequestContext().setParams(params);


        MockContext context1 = new MockContext();
        context1.getRequestContext().setMethod("GET");
        context1.getRequestContext().setEndPoint("/_cat/indices");
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        context1.getRequestContext().setParams(params);


        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 3. Because Endpoint regex match and is not global context and param matches"
                , 3
                , result);
    }

    @Test
    public void testCompareToNonZeroResult3() {
        MockContext context = new MockContext();
        context.getRequestContext().setMethod("GET");
        context.getRequestContext().setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.getRequestContext().setParams(params);
        context.setGlobalContext(true);


        MockContext context1 = new MockContext();
        context1.getRequestContext().setMethod("GET");
        context1.getRequestContext().setEndPoint("/_cat/indices");
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        context1.getRequestContext().setParams(params);


        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 2. Because Endpoint regex match and is global context and param matches"
                , 2
                , result);
    }


    @Test
    public void testCompareToNonZeroResult4() {
        MockContext context = new MockContext();
        context.getRequestContext().setMethod("GET");
        context.getRequestContext().setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.getRequestContext().setParams(params);
        context.setGlobalContext(true);


        context.getRequestContext().setHeaders(new BasicHeader("key" , "value"));


        MockContext context1 = new MockContext();
        context1.getRequestContext().setMethod("GET");
        context1.getRequestContext().setEndPoint("/_cat/indices");
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        context1.getRequestContext().setParams(params);
        context1.getRequestContext().setHeaders(new BasicHeader("key" , "value"));


        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 2. Because Endpoint regex match and is global context and param matches and header matches"
                , 3
                , result);
    }

}

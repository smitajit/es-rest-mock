package sm.elasticsearch.rest.mock.builder;

import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ContextTest {

    @Test
    public void testCompareToZeroResult() {
        Context context = new Context();
        context.setMethod("GET");
        context.setEndPoint("/_cat/indices");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.setParams(params);


        Context context1 = new Context();
        context1.setMethod("POST");

        int result = context.compareTo(context1);
        Assert.assertEquals("Because method is not same should return 0", 0, result);
    }

    @Test
    public void testCompareToNonZeroResult() {
        Context context = new Context();
        context.setMethod("GET");
        context.setEndPoint("/_cat/indices");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.setParams(params);


        Context context1 = new Context();
        context1.setMethod("GET");
        context1.setEndPoint("/_cat/indices");

        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 3. Because Endpoint Exact match and is not global context", 3, result);
    }

    @Test
    public void testCompareToNonZeroResult1() {
        Context context = new Context();
        context.setMethod("GET");
        context.setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.setParams(params);


        Context context1 = new Context();
        context1.setMethod("GET");
        context1.setEndPoint("/_cat/indices");

        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 2. Because Endpoint regex match and is not global context", 2, result);
    }


    @Test
    public void testCompareToNonZeroResult2() {
        Context context = new Context();
        context.setMethod("GET");
        context.setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.setParams(params);


        Context context1 = new Context();
        context1.setMethod("GET");
        context1.setEndPoint("/_cat/indices");
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        context1.setParams(params);


        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 3. Because Endpoint regex match and is not global context and param matches"
                , 3
                , result);
    }

    @Test
    public void testCompareToNonZeroResult3() {
        Context context = new Context();
        context.setMethod("GET");
        context.setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.setParams(params);
        context.setGlobalContext(true);


        Context context1 = new Context();
        context1.setMethod("GET");
        context1.setEndPoint("/_cat/indices");
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        context1.setParams(params);


        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 2. Because Endpoint regex match and is global context and param matches"
                , 2
                , result);
    }


    @Test
    public void testCompareToNonZeroResult4() {
        Context context = new Context();
        context.setMethod("GET");
        context.setEndPoint("/_cat/.*");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        context.setParams(params);
        context.setGlobalContext(true);


        context.setHeaders(new BasicHeader("key" , "value"));


        Context context1 = new Context();
        context1.setMethod("GET");
        context1.setEndPoint("/_cat/indices");
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        context1.setParams(params);
        context1.setHeaders(new BasicHeader("key" , "value"));


        int result = context.compareTo(context1);
        Assert.assertEquals("Should return 2. Because Endpoint regex match and is global context and param matches and header matches"
                , 3
                , result);
    }

}

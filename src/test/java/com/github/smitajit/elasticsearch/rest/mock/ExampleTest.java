package com.github.smitajit.elasticsearch.rest.mock;

import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.github.smitajit.elasticsearch.rest.mock.runner.ESRestMockRunner;
import com.github.smitajit.elasticsearch.rest.mock.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(ESRestMockRunner.class)
public class ExampleTest {

    private RestHighLevelClient hClient;
    private RestClient lClient;

    @Before
    public void setUp() {
        hClient = new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200)));
        lClient = hClient.getLowLevelClient();
    }

    @After
    public void clear(){
        ESRestMockCore.clear(false);
    }
    /**
     * TestCase for exact Method and endPoint matching
     */
    @Test public void testEndpointMatching() {
        String mockedResponse = "green  open .monitoring-kibana-6-2018.04.09                                 RqhbnOwfTYS2COX1PkKpSA 1 0   677   0 190.5kb 190.5kb\n" +
                "green  open .monitoring-es-6-2018.04.09                                     2rKXZoj-Rue2elC5gTTe6w 1 0  9521  46   4.3mb   4.3mb";
        ESRestMockCore.newMocker()
                .forMethod("GET")
                .forEndPoint("/_cat/indices")
                .expectResponse(200, mockedResponse, ContentType.APPLICATION_OCTET_STREAM)
                .mock();
        try {
            Response response = lClient.performRequest("GET", "/_cat/indices");
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals(mockedResponse, Utils.toString(response.getEntity().getContent()));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Testcase for Exact Method matching and Regex endPoint Matching
     */
    @Test public void testEndpointRegExMatching() {
        String mockedResponse = "{\n" +
                "  \"_index\": \".monitoring-kibana-6-2018.04.09\",\n" +
                "  \"_type\": \"doc\",\n" +
                "  \"_id\": \"1\",\n" +
                "  \"found\": false\n" +
                "}";

        ESRestMockCore.newMocker()
                .forMethod("GET")
                .forEndPoint("/.monitoring-kibana-.*/doc/1")
                .expectResponse(200, mockedResponse, ContentType.APPLICATION_JSON)
                .mock();

        GetRequest request = new GetRequest(".monitoring-kibana-6-2018.04.09", "doc", "1");

        try {
            GetResponse response = hClient.get(request);
            Assert.assertEquals(".monitoring-kibana-6-2018.04.09", response.getIndex());
            Assert.assertEquals("doc", response.getType());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * TestCase for param matching
     */
    @Test public void testParamMatching() {

        Map<String, String> greenStatusParam = new HashMap<>();
        greenStatusParam.put("wait_for_status", "green");

        Map<String, String> yellowStatusParam = new HashMap<>();
        yellowStatusParam.put("wait_for_status", "yellow");

        String greenStatusResponse = "{\n" +
                "  \"cluster_name\": \"mdm.next\",\n" +
                "  \"status\": \"green\",\n" +
                "  \"timed_out\": false,\n" +
                "  \"number_of_nodes\": 1,\n" +
                "  \"number_of_data_nodes\": 1,\n" +
                "  \"active_primary_shards\": 2,\n" +
                "  \"active_shards\": 2,\n" +
                "  \"relocating_shards\": 0,\n" +
                "  \"initializing_shards\": 0,\n" +
                "  \"unassigned_shards\": 0,\n" +
                "  \"delayed_unassigned_shards\": 0,\n" +
                "  \"number_of_pending_tasks\": 0,\n" +
                "  \"number_of_in_flight_fetch\": 0,\n" +
                "  \"task_max_waiting_in_queue_millis\": 0,\n" +
                "  \"active_shards_percent_as_number\": 100\n" +
                "}";

        String yellowStatusResponse = "{\n" +
                "  \"cluster_name\": \"mdm.next\",\n" +
                "  \"status\": \"yellow\",\n" +
                "  \"timed_out\": false,\n" +
                "  \"number_of_nodes\": 1,\n" +
                "  \"number_of_data_nodes\": 1,\n" +
                "  \"active_primary_shards\": 2,\n" +
                "  \"active_shards\": 2,\n" +
                "  \"relocating_shards\": 0,\n" +
                "  \"initializing_shards\": 0,\n" +
                "  \"unassigned_shards\": 0,\n" +
                "  \"delayed_unassigned_shards\": 0,\n" +
                "  \"number_of_pending_tasks\": 0,\n" +
                "  \"number_of_in_flight_fetch\": 0,\n" +
                "  \"task_max_waiting_in_queue_millis\": 0,\n" +
                "  \"active_shards_percent_as_number\": 100\n" +
                "}";


        ESRestMockCore.newMocker()
                .forMethod("GET")
                .forEndPoint("/_cluster/health")
                .forParams(yellowStatusParam)
                .expectResponse(200, yellowStatusResponse, ContentType.APPLICATION_JSON)
                .mock();

        ESRestMockCore.newMocker()
                .forMethod("GET")
                .forEndPoint("/_cluster/health")
                .forParams(greenStatusParam)
                .expectResponse(200, greenStatusResponse, ContentType.APPLICATION_JSON)
                .mock();

        try {
            Response response = lClient.performRequest("GET", "/_cluster/health", greenStatusParam);
            Map<String, Object> map = XContentHelper.convertToMap(XContentType.JSON.xContent(), response.getEntity().getContent(), true);
            Assert.assertEquals("green" , map.get("status"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Response response = lClient.performRequest("GET", "/_cluster/health", yellowStatusParam);
            Map<String, Object> map = XContentHelper.convertToMap(XContentType.JSON.xContent(), response.getEntity().getContent(), true);
            Assert.assertEquals("yellow" , map.get("status"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test public void testThrowError(){
        ESRestMockCore.newMocker()
                .forMethod("GET")
                .forEndPoint("/_cat/indices")
                .expectError(new IOException("mocked exception"))
                .mock();
        try {
            lClient.performRequest("GET", "/_cat/indices");
            Assert.fail("Should throw exception");
        } catch (IOException e) {
            Assert.assertEquals("mocked exception" , e.getMessage());
        }
    }
}

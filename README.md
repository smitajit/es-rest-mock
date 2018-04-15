# es-rest-mock

A simple framework to mock the elasticsearch rest client.
Designed to Unit test High and Low Level Elastic searh rest client operations

# Dependency
```xml
<dependency>
    <groupId>com.github.smitajit</groupId>
    <artifactId>es-rest-mock</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

## Usage Example 
```java
@RunWith(ESRestMockRunner.class)
public class ExampleTest {

    private RestHighLevelClient hClient;
    private RestClient lClient;

    @Before
    public void setUp() {
        hClient = new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200)));
        lClient = hClient.getLowLevelClient();
    }

    @Test
    public void testGetIndices() {
        String mockedResponse = "green  open .monitoring-kibana-6-2018.04.09                                 RqhbnOwfTYS2COX1PkKpSA 1 0   677   0 190.5kb 190.5kb\n" +
                "green  open .monitoring-es-6-2018.04.09                                     2rKXZoj-Rue2elC5gTTe6w 1 0  9521  46   4.3mb   4.3mb";
        ESRestMockCore.newBuilder()
                .forMethod("GET")
                .forEndPoint("/_cat/indices")
                .expectResponse(200, mockedResponse, ContentType.APPLICATION_OCTET_STREAM)
                .build();
        try {
            Response response = lClient.performRequest("GET", "/_cat/indices");
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals(mockedResponse, Utils.toString(response.getEntity().getContent()));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetIndexRequest() {

        String mockedReponse = "{\n" +
                "  \"_index\": \".monitoring-kibana-6-2018.04.09\",\n" +
                "  \"_type\": \"doc\",\n" +
                "  \"_id\": \"1\",\n" +
                "  \"found\": false\n" +
                "}";

        ESRestMockCore.newBuilder()
                .forMethod("GET")
                .forEndPoint("/.monitoring-kibana-6-2018.04.09/doc/1")
                .expectResponse(200, mockedReponse, ContentType.APPLICATION_JSON)
                .build();

        GetRequest request = new GetRequest(".monitoring-kibana-6-2018.04.09", "doc", "1");

        try {
            GetResponse response = hClient.get(request);
            Assert.assertEquals(".monitoring-kibana-6-2018.04.09", response.getIndex());
            Assert.assertEquals("doc", response.getType());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
```

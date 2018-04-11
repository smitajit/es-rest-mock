package sm.elasticsearch.rest.mock.util;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.BasicStatusLine;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import sm.elasticsearch.rest.mock.builder.MockContext;
import sm.elasticsearch.rest.mock.handler.RestMethodHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static Response createResponse(MockContext context) throws Exception {

        if (null != context.getResponseContext().getError()) {
            throw context.getResponseContext().getError();
        }

        ProtocolVersion version = new ProtocolVersion("http", 1, 2);
        RequestLine requestLine = new BasicRequestLine(context.getRequestContext().getMethod(), context.getRequestContext().getEndPoint(), version);
        HttpHost httpHost = new HttpHost("rest-mock-host", 0, "http");
        StatusLine statusLine = new BasicStatusLine(version, context.getResponseContext().getStatusCode(), "");
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        basicHttpEntity.setContent(new ByteArrayInputStream(context.getResponseContext().getResponseBody().getBytes()));
        basicHttpEntity.setContentType(context.getResponseContext().getContentType().toString());
        HttpResponse response = new BasicHttpResponse(statusLine);
        if (null != context.getResponseContext().getHeaders()) {
            response.setHeaders(context.getResponseContext().getHeaders());
        }
        response.setEntity(basicHttpEntity);

        return doCreateResponse(requestLine, httpHost, response);
    }

    private static Response doCreateResponse(RequestLine requestLine, HttpHost httpHost, HttpResponse response) {
        try {
            Constructor<Response> constructor = Response.class.getDeclaredConstructor(RequestLine.class, HttpHost.class, HttpResponse.class);
            constructor.setAccessible(true);
            return constructor.newInstance(requestLine, httpHost, response);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new MockException(e);
        }
    }

    public static RestClient getProxiedClient() throws Exception {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(RestClient.class);
        Class clazz = factory.createClass();

        Constructor c1 = null;
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor c : constructors
                ) {
            c1 = c;
        }


        Class[] parameterTypes = c1.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            args[i] = getDummyArgument(parameterTypes[i]);
        }

        Object[] args1 = new Object[]{
                null
                , 0l
                , new Header[]{}
                , new HttpHost[]{new HttpHost("rest-mock-host", 0)}
                , ""
                , null
        };

        Object instance = c1.newInstance(args1);

        ((ProxyObject) instance).setHandler(new RestMethodHandler());
        return (RestClient) instance;
    }

    public static Object getDummyArgument(Class type) {
        if (type.equals(CloseableHttpAsyncClient.class)) {
            return null;
        }
        return null;
    }

    public static String toString(InputStream stream) {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {
            throw new MockException(e);
        }
        return textBuilder.toString();
    }
}

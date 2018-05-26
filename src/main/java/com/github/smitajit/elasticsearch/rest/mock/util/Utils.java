package com.github.smitajit.elasticsearch.rest.mock.util;

import com.github.smitajit.elasticsearch.rest.mock.builder.MockContext;
import com.github.smitajit.elasticsearch.rest.mock.handler.RestMethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.apache.http.*;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.BasicStatusLine;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Utils {

    private static final String MOCK_ES_HOST = "rest-mock-host";
    private static final int MOCK_ES_PORT = 0;
    private static final String MOCK_ES_PROTOCOL = "http";

    public static Response createResponse(MockContext context) throws Exception {

        if (null != context.getResponseContext().getError()) {
            throw context.getResponseContext().getError();
        }
        ProtocolVersion version = new ProtocolVersion(MOCK_ES_PROTOCOL, 1, 2);
        RequestLine requestLine = new BasicRequestLine(context.getRequestContext().getMethod(), context.getRequestContext().getEndPoint(), version);
        HttpHost httpHost = new HttpHost(MOCK_ES_HOST, MOCK_ES_PORT, MOCK_ES_PROTOCOL);
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

        //getting the first Constructor
        Constructor constructors = clazz.getConstructors()[0];
        constructors.setAccessible(true);

        Class[] parameterTypes = constructors.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            args[i] = getDummyArgument(parameterTypes[i]);
        }

        Object instance = constructors.newInstance(args);

        ((ProxyObject) instance).setHandler(new RestMethodHandler());
        return (RestClient) instance;
    }

    private static Object getDummyArgument(Class type) {
        if (type.equals(CloseableHttpAsyncClient.class)) {
            return null;
        } else if (type.equals(long.class)) {
            return 0l;
        } else if (type.equals(Header[].class)) {
            return new Header[]{};
        } else if (type.equals(HttpHost[].class)) {
            return new HttpHost[]{new HttpHost(MOCK_ES_HOST, MOCK_ES_PORT)};
        } else if (type.equals(String.class)) {
            return "";
        } else if (type.equals(RestClient.FailureListener.class)) {
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

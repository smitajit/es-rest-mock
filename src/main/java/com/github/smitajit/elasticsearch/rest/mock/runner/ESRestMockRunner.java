package com.github.smitajit.elasticsearch.rest.mock.runner;

import javassist.*;
import org.elasticsearch.client.RestClient;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Junit runner which instrument the RestClientBuilder class to return the Proxied class uses the mocked request reponse instead of actually sending the data over network
 */
public class ESRestMockRunner extends BlockJUnit4ClassRunner {

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public ESRestMockRunner(Class<?> klass) throws InitializationError {
        super(klass);
        try {
            updateClass();
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    private void updateClass() throws Exception {
        ClassPool pool = ClassPool.getDefault();

        pool.insertClassPath(new ClassClassPath(RestClient.class));

        CtClass builderCtClass = pool.get("org.elasticsearch.client.RestClientBuilder");
        CtField proxyClient = new CtField(CtClass.booleanType, "proxyClient", builderCtClass);
        builderCtClass.addField(proxyClient, CtField.Initializer.constant(true));

        CtMethod build = findMethod(builderCtClass, "build");
        build.insertBefore("if(this.proxyClient) {" +
                "" +
                "return com.github.smitajit.elasticsearch.rest.mock.util.Utils.getProxiedClient();" +
                "}");
        builderCtClass.toClass();
    }

    private CtMethod findMethod(CtClass ctClass, String method) {
        CtMethod ctMethod = null;
        for (CtMethod m : ctClass.getMethods()) {
            if (m.getName().equals(method)) {
                ctMethod = m;
            }
        }
        return ctMethod;
    }
}

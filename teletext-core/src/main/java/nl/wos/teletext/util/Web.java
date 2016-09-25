package nl.wos.teletext.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Web {
    private static final Logger log = Logger.getLogger(Web.class.getName());

    private static RequestConfig config = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).build();
    private static HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(config).create();

    public static HttpEntity doWebRequest(String url) throws Exception {
        try {
            HttpResponse response = builder
                    .create()
                    .build()
                    .execute(new HttpGet(url));
            return response.getEntity();
        }
        catch (IOException ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
            throw new Exception("Something went wrong with the web request", ex);
        }
    }

    public static HttpEntity doWebRequest(String url, CredentialsProvider credentialsProvider) throws Exception {
        try {
            HttpResponse response = builder
                    .create()
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build()
                    .execute(new HttpGet(url));
            return response.getEntity();
        }
        catch (IOException ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
            throw new Exception("Something went wrong with the web request", ex);
        }
    }
}

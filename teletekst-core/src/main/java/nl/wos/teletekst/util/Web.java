package nl.wos.teletekst.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class Web {
    public static HttpEntity doWebRequest(String url) throws Exception {
        try {
            HttpResponse response = HttpClientBuilder
                    .create()
                    .build()
                    .execute(new HttpGet(url));
            return response.getEntity();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Something went wrong with the web request", e);
        }
    }

    public static HttpEntity doWebRequest(String url, CredentialsProvider credentialsProvider) throws Exception {
        try {
            HttpResponse response = HttpClientBuilder
                    .create()
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build()
                    .execute(new HttpGet(url));
            return response.getEntity();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Something went wrong with the web request", e);
        }
    }
}

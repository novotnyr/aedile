package com.github.novotnyr.aedile;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

public class ConsulClientProvider {
    private ConsulConfiguration configuration;

    public ConsulClientProvider(ConsulConfiguration configuration) {
        this.configuration = configuration;
    }

    public ConsulClient getConsulClient() {
        return new ConsulClient(getConsulRawClient());
    }

    protected ConsulRawClient getConsulRawClient() {
        CredentialsProvider provider = getCredentialsProvider();

        AbstractHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCredentialsProvider(provider);

        return new ConsulRawClient(this.configuration.getHost(), this.configuration.getPort(), httpClient);
    }

    private CredentialsProvider getCredentialsProvider() {
        if(this.configuration.getUser() == null) {
            return null;
        }

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.configuration.getUser(), this.configuration.getPassword());
        provider.setCredentials(AuthScope.ANY, credentials);
        return provider;
    }
}

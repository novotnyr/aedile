package com.github.novotnyr.aedile;

import com.github.novotnyr.aedile.git.PropertyDirectoryConsulKeyAndValueImporter;

/**
 * A configuration object for the Consul instance.
 */
public class ConsulConfiguration {
    public static final String DEFAULT_CONSUL_HOST = "localhost";

    public static final int DEFAULT_CONSUL_PORT = 8500;

    public static final String DEFAULT_TOKEN = null;

    private String host = DEFAULT_CONSUL_HOST;

    private int port = DEFAULT_CONSUL_PORT;

    private String aclToken = DEFAULT_TOKEN;

    private String user;

    private String password;

    private String prefix = PropertyDirectoryConsulKeyAndValueImporter.DEFAULT_CONFIGURATION_PREFIX;

    private String datacenter;

    /**
     * Create a configuration with default values.
     * <p>
     *     Uses the default consul host,
     *     the default port,
     *     the default configuration prefix,
     *     and no user, password, datacenter nor ACL token.
     * </p>
     */
    public ConsulConfiguration() {
        // empty constructor
    }

    /**
     * Create a configuration that points to the specific
     * host and port
     * @param host a Consul host
     * @param port a consul port
     */
    public ConsulConfiguration(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Return the host used by this configuration
     * @return the configuration host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the hostname of the Consul agent that will
     * be used.
     * @param host the Consul hostname
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Return the port of the Consul agent that will
     * be used.
     * @return
     */
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAclToken() {
        return aclToken;
    }

    /**
     * Set the ACL token that will be sent to Consul along with requests.
     * The token corresponds to the <code>X-Consul-Token</code> request header or the
     * <code>token</code> querystring parameter.
     * @param aclToken ACL token to send with requests
     */
    public void setAclToken(String aclToken) {
        this.aclToken = aclToken;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    /**
     * Build the Consul configuration from the environment variables.
     * @return
     */
    public static ConsulConfiguration fromEnvironment() {
        ConsulConfiguration configuration = new ConsulConfiguration();
        configuration.setHost(getHostEnvironmentVariable());
        configuration.setPort(getPortEnvironmentVariable());
        configuration.setAclToken(getAclTokenEnvironmentVariable());
        configuration.setUser(getUserEnvironmentVariable());
        configuration.setPassword(getPasswordEnvironmentVariable());
        configuration.setPrefix(getPrefixEnvironmentVariable());
        configuration.setDatacenter(getDatacenterEnvironmentVariable());

        return configuration;
    }

    private static String getHostEnvironmentVariable() {
        String consulEndpoint = System.getenv("CONSUL_ENDPOINT");
        if(consulEndpoint == null) {
            consulEndpoint = DEFAULT_CONSUL_HOST;
        }
        return consulEndpoint;
    }

    private static int getPortEnvironmentVariable() {
        String consulPortString = System.getenv("CONSUL_PORT");
        int consulPort;
        try {
            consulPort = Integer.parseInt(consulPortString);
        } catch (NumberFormatException e) {
            consulPort = DEFAULT_CONSUL_PORT;
        }
        return consulPort;
    }

    private static String getAclTokenEnvironmentVariable() {
        String token = System.getenv("TOKEN");
        if(token == null) {
            return token;
        }
        return token;
    }

    private static String getUserEnvironmentVariable() {
        String user = System.getenv("CONSUL_HTTP_USER");
        if(user == null) {
            return user;
        }
        return user;
    }

    private static String getPasswordEnvironmentVariable() {
        String password = System.getenv("CONSUL_HTTP_PASSWORD");
        if(password == null) {
            return password;
        }
        return password;
    }

    private static String getPrefixEnvironmentVariable() {
        String consulEndpoint = System.getenv("CONSUL_KEY_PREFIX");
        if(consulEndpoint == null) {
            consulEndpoint = PropertyDirectoryConsulKeyAndValueImporter.DEFAULT_CONFIGURATION_PREFIX;
        }
        return consulEndpoint;
    }

    public static String getDatacenterEnvironmentVariable() {
        String datacenter = System.getenv("CONSUL_DATACENTER");
        if(datacenter == null) {
            return null;
        }
        return datacenter;

    }

}

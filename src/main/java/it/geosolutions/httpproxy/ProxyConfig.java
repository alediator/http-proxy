/*
 *  Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.httpproxy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * ProxyConfig class to define the proxy configuration.
 * 
 * @author Tobia Di Pisa at tobia.dipisa@geo-solutions.it
 */
public final class ProxyConfig {

    private final static Logger LOGGER = Logger.getLogger(ProxyConfig.class.toString());

    /**
     * A list of regular expressions describing hostnames the proxy is permitted to forward to
     */
    private Set<String> hostnameWhitelist = new HashSet<String>();

    /**
     * A list of regular expressions describing MIMETypes the proxy is permitted to forward
     */
    private Set<String> mimetypeWhitelist = new HashSet<String>();

    /**
     * A list of regular expressions describing Request Types the proxy is permitted to forward
     */
    private Set<String> reqtypeWhitelist = new HashSet<String>();

    /**
     * A list of regular expressions describing request METHODS the proxy is permitted to forward
     */
    private Set<String> methodsWhitelist = new HashSet<String>();

    /**
     * A list of regular expressions describing request HOSTS the proxy is permitted to forward
     */
    private Set<String> hostsWhitelist = new HashSet<String>();

    /**
     * The servlet context
     */
    private ServletContext context;

    /**
     * The path of the properties file
     */
    private String propertiesFilePath;

    /**
     * The request timeout
     */
    private int soTimeout = 30000;

    /**
     * The connection timeout
     */
    private int connectionTimeout = 30000;

    /**
     * The maximum total connections available
     */
    private int maxTotalConnections = 60;

    /**
     * The maximum connections available per host
     */
    private int defaultMaxConnectionsPerHost = 6;
    
    private int defaultStreamByteSize = 1024;
    
    /**
     * 
     */
    private PropertiesConfiguration proxyProperties;

	/**
     * @param context
     * @param propertiesFilePath
     */
    public ProxyConfig(ServletContext context, String propertiesFilePath) {
        this.context = context;
        this.propertiesFilePath = propertiesFilePath;

        configProxy();
    }

	/**
	 * Create a new proxy configuration with proxy properties
     * @param proxyProperties
     */
    public ProxyConfig(PropertiesConfiguration proxyProperties) {
        this.proxyProperties = proxyProperties;
        configProxy();
    }

    /**
     * Provide the proxy configuration
     * 
     * @throws IOException
     */
    private void configProxy() {
        PropertiesConfiguration props = propertiesLoader();

        // ////////////////////////////////////////////////////////////
        // Load proxy configuration white lists from properties file
        // ////////////////////////////////////////////////////////////

        if (props != null) {
            Set<String> p = Utils.getProperty(props, "hostnameWhitelist");
            if (p != null)
                this.setHostnameWhitelist(p);

            p = Utils.getProperty(props, "mimetypeWhitelist");
            //p = Utils.getProperty(props, "mimetypeWhitelist");
            if (p != null)
                this.setMimetypeWhitelist(p);

            p = Utils.getProperty(props, "methodsWhitelist");
            if (p != null)
                this.setMethodsWhitelist(p);

            p = Utils.getProperty(props, "hostsWhitelist");
            if (p != null)
                this.setHostsWhitelist(p);

            // ////////////////////////////////////////
            // Read various request type properties
            // ////////////////////////////////////////

            Set<String> rt = new HashSet<String>();
            String s = props.getString("reqtypeWhitelist.capabilities");
            if (s != null)
                rt.add(s);

            s = props.getString("reqtypeWhitelist.geostore");
            if (s != null)
                rt.add(s);

            s = props.getString("reqtypeWhitelist.csw");
            if (s != null)
                rt.add(s);
            
            s = props.getString("reqtypeWhitelist.featureinfo");
            if (s != null)
                rt.add(s);
            
            s = props.getString("reqtypeWhitelist.generic");
            if (s != null)
                rt.add(s);

            this.setReqtypeWhitelist(rt);

            try {
                // /////////////////////////////////////////////////
                // Load byte size configuration from
                // properties file.
                // /////////////////////////////////////////////////
            	
                String bytesSize = props.getString("defaultStreamByteSize");
                this.setDefaultStreamByteSize(bytesSize != null ? Integer.parseInt(bytesSize) : 
                	this.defaultStreamByteSize);
                
                // /////////////////////////////////////////////////
                // Load connection manager configuration from
                // properties file.
                // /////////////////////////////////////////////////
                
                String timeout = props.getString("timeout");
                this.setSoTimeout(timeout != null ? Integer.parseInt(timeout) : this.soTimeout);

                String conn_timeout = props.getString("connection_timeout");
                this.setConnectionTimeout(conn_timeout != null ? Integer.parseInt(conn_timeout)
                        : this.connectionTimeout);

                String max_conn = props.getString("max_total_connections");
                this.setMaxTotalConnections(max_conn != null ? Integer.parseInt(max_conn)
                        : this.maxTotalConnections);

                String def_conn_host = props.getString("default_max_connections_per_host");
                this.setMaxTotalConnections(def_conn_host != null ? Integer.parseInt(def_conn_host)
                        : this.defaultMaxConnectionsPerHost);

            } catch (NumberFormatException e) {
                if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE,
                            "Error parsing the proxy properties file using default", e);

                this.setSoTimeout(this.soTimeout);
                this.setConnectionTimeout(this.connectionTimeout);
                this.setMaxTotalConnections(this.maxTotalConnections);
                this.setMaxTotalConnections(this.defaultMaxConnectionsPerHost);
                this.setDefaultStreamByteSize(this.defaultStreamByteSize);
            }
        }
    }

    /**
     * Read the proxy properties file.
     * 
     * @return Properties
     */
    public PropertiesConfiguration propertiesLoader() {
    	if(this.proxyProperties != null){
    		return this.proxyProperties;
    	}else{
            

            try {

        		PropertiesConfiguration config = new PropertiesConfiguration(propertiesFilePath);
        		config.setReloadingStrategy(new FileChangedReloadingStrategy());
                config.load();
                return config;

            } catch (Exception e) {
                if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "Error loading the proxy properties file ", e);
                return null;
            }
    	}
    }

    /**
     * @return the soTimeout
     */
    public int getSoTimeout() {
        return soTimeout;
    }

    /**
     * @param soTimeout the soTimeout to set
     */
    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    /**
     * @return the connectionTimeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the maxTotalConnections
     */
    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    /**
     * @param maxTotalConnections the maxTotalConnections to set
     */
    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    /**
     * @return the defaultMaxConnectionsPerHost
     */
    public int getDefaultMaxConnectionsPerHost() {
        return defaultMaxConnectionsPerHost;
    }

    /**
     * @param defaultMaxConnectionsPerHost the defaultMaxConnectionsPerHost to set
     */
    public void setDefaultMaxConnectionsPerHost(int defaultMaxConnectionsPerHost) {
        this.defaultMaxConnectionsPerHost = defaultMaxConnectionsPerHost;
    }

    /**
     * @return the hostnameWhitelist
     */
    public Set<String> getHostnameWhitelist() {
        PropertiesConfiguration props = propertiesLoader();

        if (props != null) {
            Set<String> set = Utils.getProperty(props, "hostnameWhitelist");
            if (set != null)
                this.setHostnameWhitelist(set);
        }

        return hostnameWhitelist;
    }

    /**
     * @param hostnameWhitelist the hostnameWhitelist to set
     */
    public void setHostnameWhitelist(Set<String> hostnameWhitelist) {
        this.hostnameWhitelist = hostnameWhitelist;
    }

    /**
     * @return the mimetypeWhitelist
     */
    public Set<String> getMimetypeWhitelist() {
        PropertiesConfiguration props = propertiesLoader();

        if (props != null) {
            Set<String> set = Utils.getProperty(props, "mimetypeWhitelist");
            if (set != null)
                this.setMimetypeWhitelist(set);
        }

        return mimetypeWhitelist;
    }

    /**
     * @param mimetypeWhitelist the mimetypeWhitelist to set
     */
    public void setMimetypeWhitelist(Set<String> mimetypeWhitelist) {
        this.mimetypeWhitelist = mimetypeWhitelist;
    }

    /**
     * @return the reqtypeWhitelist
     */
    public Set<String> getReqtypeWhitelist() {
        PropertiesConfiguration props = propertiesLoader();

        if (props != null) {
            Set<String> rt = new HashSet<String>();
            String s = props.getString("reqtypeWhitelist.capabilities");
            if (s != null)
                rt.add(s);

            s = props.getString("reqtypeWhitelist.geostore");
            if (s != null)
                rt.add(s);

            s = props.getString("reqtypeWhitelist.csw");
            if (s != null)
                rt.add(s);
            
            s = props.getString("reqtypeWhitelist.featureinfo");
            if (s != null)
                rt.add(s);
            
            s = props.getString("reqtypeWhitelist.generic");
            if (s != null)
                rt.add(s);

            this.setReqtypeWhitelist(rt);
        }

        return reqtypeWhitelist;
    }

    /**
     * @param reqtypeWhitelist the reqtypeWhitelist to set
     */
    public void setReqtypeWhitelist(Set<String> reqtypeWhitelist) {
        this.reqtypeWhitelist = reqtypeWhitelist;
    }

    /**
     * @return the methodsWhitelist
     */
    public Set<String> getMethodsWhitelist() {
        PropertiesConfiguration props = propertiesLoader();

        if (props != null) {
            Set<String> set = Utils.getProperty(props, "methodsWhitelist");
            if (set != null)
                this.setMethodsWhitelist(set);
        }

        return methodsWhitelist;
    }

    /**
     * @param methodsWhitelist the methodsWhitelist to set
     */
    public void setMethodsWhitelist(Set<String> methodsWhitelist) {
        this.methodsWhitelist = methodsWhitelist;
    }

    /**
     * @return the hostsWhitelist
     */
    public Set<String> getHostsWhitelist() {
        PropertiesConfiguration props = propertiesLoader();

        if (props != null) {
            Set<String> set = Utils.getProperty(props, "hostsWhitelist");
            if (set != null)
                this.setHostsWhitelist(set);
        }

        return hostsWhitelist;
    }

    /**
     * @param hostsWhitelist the hostsWhitelist to set
     */
    public void setHostsWhitelist(Set<String> hostsWhitelist) {
        this.hostsWhitelist = hostsWhitelist;
    }

    /**
     * @return the context
     */
    public ServletContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(ServletContext context) {
        this.context = context;
    }

    /**
     * @return the propertiesFilePath
     */
    public String getPropertiesFilePath() {
        return propertiesFilePath;
    }

    /**
     * @param propertiesFilePath the propertiesFilePath to set
     */
    public void setPropertiesFilePath(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
    }
    
    /**
	 * @return the defaultStreamByteSize
	 */
	public int getDefaultStreamByteSize() {
		return defaultStreamByteSize;
	}

	/**
	 * @param defaultStreamByteSize the defaultStreamByteSize to set
	 */
	public void setDefaultStreamByteSize(int defaultStreamByteSize) {
		this.defaultStreamByteSize = defaultStreamByteSize;
	}

}

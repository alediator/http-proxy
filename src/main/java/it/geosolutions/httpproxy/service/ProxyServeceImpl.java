/*
 *  Copyright (C) 2007 - 2013 GeoSolutions S.A.S.
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
package it.geosolutions.httpproxy.service;

import it.geosolutions.httpproxy.HTTPProxy;
import it.geosolutions.httpproxy.ProxyConfig;
import it.geosolutions.httpproxy.exception.ProxyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Proxy service implementation
 * 
 * @author <a href="mailto:aledt84@gmail.com">Alejandro Diaz Torres</a>
 *
 */
@Repository
public class ProxyServeceImpl implements IProxyService {
	
	private HTTPProxy proxy;

    /**
     * The proxy configuration.
     */
    private ProxyConfig proxyConfig;
    
    /**
     * Proxy properties autowired
     */
    @Autowired @Qualifier("proxyProperties")
    private PropertiesConfiguration proxyProperties;

    /**
     * Default constructor
     */
	public ProxyServeceImpl(){
		super();
		try {
	        proxyConfig = new ProxyConfig(proxyProperties);
	        proxy = new HTTPProxy(proxyConfig);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Obtain a proxy property
	 * 
	 * @param key of the property
	 * 
	 * @return property value in {@link #proxyProperties}
	 */
	protected String getProxyProperty(String key){
		return this.proxyProperties != null ? this.proxyProperties.getString(key) : null;
	}
	
	
	/**
     * Performs an HTTP request. Read <code>httpServletRequest</code> method. Default method is HTTP GET. 
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
	public void execute(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ProxyException {
		try {
			String method  = httpServletRequest.getMethod();
			if("GET".equals(method)){
					proxy.doGet(httpServletRequest, httpServletResponse);
			}
		} catch (Exception e) {
			//TODO: handle
			e.printStackTrace();
			throw new ProxyException();
		}

	}

    /**
     * Performs an HTTP GET request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
	public void doGet(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ProxyException {
		try {
			proxy.doGet(httpServletRequest, httpServletResponse);
		} catch (Exception e) {
			//TODO: handle
			e.printStackTrace();
			throw new ProxyException();
		}
	}

    /**
     * Performs an HTTP POST request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
	public void doPost(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ProxyException {
		try {
			proxy.doPost(httpServletRequest, httpServletResponse);
		} catch (Exception e) {
			//TODO: handle
			e.printStackTrace();
			throw new ProxyException();
		}
	}
	
    /**
     * Performs an HTTP PUT request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
	public void doPut(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ProxyException {
		try {
			proxy.doPut(httpServletRequest, httpServletResponse);
		} catch (Exception e) {
			//TODO: handle
			e.printStackTrace();
			throw new ProxyException();
		}
	}

    /**
     * Performs an HTTP DELETE request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
	public void doDelete(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ProxyException {
		try {
			proxy.doDelete(httpServletRequest, httpServletResponse);
		} catch (Exception e) {
			//TODO: handle
			e.printStackTrace();
			throw new ProxyException();
		}
	}

}

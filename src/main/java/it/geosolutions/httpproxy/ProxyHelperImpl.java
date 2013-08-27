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
package it.geosolutions.httpproxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Proxy helper implementation.
 * This implementation loads a PropertyConfiguration from Spring context to the proxy config 
 * 
 * @author <a href="mailto:aledt84@gmail.com">Alejandro Diaz Torres</a>
 *
 */
@Repository
public class ProxyHelperImpl implements IProxyHelper {
    
    /**
     * Proxy properties autowired
     */
    @Autowired @Qualifier("proxyProperties")
    private PropertiesConfiguration proxyProperties;

	/**
	 * Obtain a proxy property
	 * 
	 * @param key of the property
	 * 
	 * @return property value in {@link #proxyProperties}
	 */
	public String getProxyProperty(String key){
		return this.proxyProperties != null ? this.proxyProperties.getString(key) : null;
	}
	
	/**
	 * Initialize proxy
	 * 
	 * @param proxy to be initialized
	 */
	public void initProxy(IProxyService proxy) {
		this.initProxy(proxy, null);
	}
	
	/**
	 * Initialize proxy
	 * 
	 * @param proxy to be initialized
	 * @param context of the proxy
	 */
	public void initProxy(IProxyService proxy, ServletContext context) {
		if (proxyProperties != null) {
			proxy.setProxyConfig(new ProxyConfig(proxyProperties));
		} else if (context != null) {
			String proxyPropPath = context.getInitParameter("proxyPropPath");
			proxy.setProxyConfig(new ProxyConfig(context, proxyPropPath));
		}
	}
	
	/**
	 * Prepare a proxy method execution
	 * 
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param proxy
	 * 
	 * @return ProxyMethodConfig to execute the method
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public ProxyMethodConfig prepareProxyMethod(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, IProxyService proxy) throws IOException,
			ServletException {
    	URL url = null;
        String user = null, password = null;
        Map<?, ?> pars;
        String method = httpServletRequest.getMethod();
        
        if (HttpMethods.METHOD_GET.equals(method)){
        	// Obtain pars from parameter map
        	pars = httpServletRequest.getParameterMap();
        }else{
            //Parse the queryString to not read the request body calling getParameter from httpServletRequest
            // so the method can simply forward the request body
        	pars=  splitQuery(httpServletRequest.getQueryString());
        }

        // Obtain ProxyMethodConfig from pars
        for (Object  key : pars.keySet()) {
        	
            String value = (pars.get(key) instanceof String) ?
            			(String) pars.get(key) : ((String[]) pars.get(key))[0];

            if ("user".equals(key)) {
                user = value;
            } else if ("password".equals(key)) {
                password = value;
            } else if ("url".equals(key)) {
                url = new URL(value);
            }
        }

        if (url != null) {
        	// init and return the config
            proxy.onInit(httpServletRequest, httpServletResponse, url);
            return new ProxyMethodConfig(url, user, password, method);
        } else{
        	return null;
        }
	}

	/**
	 * Split a queryString with ','
	 * 
	 * @param query
	 * 
	 * @return Map with keys and values
	 * 
	 * @throws UnsupportedEncodingException
	 */
    private Map<String,String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

}

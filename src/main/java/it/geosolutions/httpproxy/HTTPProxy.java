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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * HTTPProxy class. Now delegated in IProxyService
 * 
 * @author Alessio Fabiani at alessio.fabiani@geo-solutions.it
 * @author Tobia Di Pisa at tobia.dipisa@geo-solutions.it
 * @author Simone Giannecchini, GeoSolutions SAS
 * @author Alejandro Diaz
 */
public class HTTPProxy extends HttpServlet{

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = -4770692886388850680L;
    
    @Autowired
    private IProxyService proxyService;

    /**
     * Performs an HTTP GET request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
		proxyService.execute(httpServletRequest, httpServletResponse);
    }

    /**
     * Performs an HTTP POST request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
    public void doPost(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException, ServletException {
		proxyService.execute(httpServletRequest, httpServletResponse);
    }

    /**
     * Performs an HTTP PUT request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
    public void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
		proxyService.execute(httpServletRequest, httpServletResponse);
    }

    /**
     * Performs an HTTP DELETE request
     * 
     * @param httpServletRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the client request to be proxied
     * @param httpServletResponse The {@link HttpServletResponse} object by which we can send a proxied response to the client
     */
    public void doDelete(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException, ServletException {
		proxyService.execute(httpServletRequest, httpServletResponse);
    }

}

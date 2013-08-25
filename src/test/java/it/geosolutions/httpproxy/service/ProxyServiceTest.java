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

import static org.junit.Assert.*;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Proxy service test
 * 
 * @author <a href="mailto:aledt84@gmail.com">Alejandro Diaz Torres</a>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:http-proxy-test-applicationContext.xml")
public class ProxyServiceTest {
	
	@Autowired
	private IProxyService proxy;
	
	/**
	 * Test IProxyService execute as HTTP GET
	 */
	@Test
	public void testExecuteGet(){
		try {
			// Generate mocked request and response
			MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/proxy/?"
	                + "url=http://demo1.geo-solutions.it/geoserver/wms?SERVICE=WMS&REQUEST=GetCapabilities&version=1.3.0");
			MockHttpServletResponse mockResponse = new MockHttpServletResponse();
			
			// Call proxy execute
			proxy.execute(mockRequest, mockResponse);
			
			// Assert the response
			assertNotNull(mockResponse);
			assertEquals(mockResponse.getStatus(), HttpStatus.SC_OK);
			assertNotNull(mockResponse.getOutputStream());
			assertNotNull(mockResponse.getContentType());
			assertTrue(mockResponse.getContentType().contains("text/xml"));
		} catch (Exception e) {
			fail("Exception executing proxy");
		}
	}

}

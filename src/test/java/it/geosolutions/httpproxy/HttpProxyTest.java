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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * HttpProxyTest class. Test Cases for the HTTPProxy servlet.
 * 
 * @author Tobia Di Pisa at tobia.dipisa@geo-solutions.it
 * @author Alejandro Diaz
 */
public class HttpProxyTest extends BaseHttpTest {

	/* Test URL parameters */
	private String DEFAULT_PROXY_URL = "http://localhost:8080/http_proxy/proxy/?url=";
	private String CORRECT_URL = "http://demo1.geo-solutions.it/geoserver/wms?SERVICE=WMS&REQUEST=GetCapabilities&version=1.1.1";
	private String FAKE_URL = "http://fakeServer/geoserver/wms?SERVICE=WMS&REQUEST=GetCapabilities&version=1.1.1";
	private String FAKE_REQUEST_TYPE_URL = "http://localhost:8080";
	private String DEFAULT_ENCODE = "UTF-8";
	private String RESPONSE_FORBIDDEN = "Forbidden";

	/**
	 * Test with a correct request
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDoCorrectGet() throws Exception {

		URL url = new URL(DEFAULT_PROXY_URL
				+ URLEncoder.encode(CORRECT_URL, DEFAULT_ENCODE));

		// Original response
		URL urlWithoutProxy = new URL(CORRECT_URL);
		HttpURLConnection conWithoutProxy = (HttpURLConnection) urlWithoutProxy
				.openConnection();
		String responseWithoutProxy = IOUtils.toString(conWithoutProxy
				.getInputStream());

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		String response = IOUtils.toString(con.getInputStream());

		assertNotNull(response);
		assertEquals(response, responseWithoutProxy);
		assertTrue(con.getRequestMethod().equals("GET"));
		assertTrue(con.getResponseCode() == 200);

		con.disconnect();
		conWithoutProxy.disconnect();
	}

	/**
	 * Test with a fake hostname
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDoFakeHostNameGet() throws Exception {

		URL url = new URL(DEFAULT_PROXY_URL
				+ URLEncoder.encode(FAKE_URL, DEFAULT_ENCODE));

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		String message = con.getResponseMessage();

		assertNotNull(message);
		if (!RESPONSE_FORBIDDEN.equals(message)) { // Apache Tomcat returns a
													// 'Forbidden' message and
													// the default html error
													// page with the explanation
			assertEquals(message,
					"Host Name fakeServer is not among the ones allowed for this proxy");
			// Jetty show the final message directly
		}

		assertTrue(con.getRequestMethod().equals("GET"));
		assertTrue(con.getResponseCode() == 403);

		con.disconnect();
	}

	/**
	 * Test with a fake request type
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDoFakeRequestTypeGet() throws Exception {

		URL url = new URL(DEFAULT_PROXY_URL
				+ URLEncoder.encode(FAKE_REQUEST_TYPE_URL, DEFAULT_ENCODE));

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		String message = con.getResponseMessage();

		assertNotNull(message);
		if (!RESPONSE_FORBIDDEN.equals(message)) { // Apache Tomcat returns a
													// 'Forbidden' message and
													// the default html error
													// page with the explanation
			assertEquals(message,
					"Request Type is not among the ones allowed for this proxy");
			// Jetty show the final message directly
		}

		assertTrue(con.getRequestMethod().equals("GET"));
		assertTrue(con.getResponseCode() == 403);

		con.disconnect();
	}

}
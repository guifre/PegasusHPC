/***********************************************
Copyright (C) 2013 Guifre Ruiz <guifre.ruiz@gmail.com>

This file is part of PegasusHPC https://pegasushpc.googlecode.com

PegasusHPC is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

PegasusHPC is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with PegasusHPC.  If not, see <http://www.gnu.org/licenses/>.

 **********************************************/
package org.pegasushpc.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.pegasushpc.utils.Parser;

/**
 * The Crawler class is in charge of the network I/O and of querying the
 * targeted host
 */
public class Crawler {

	private static Logger log = Logger.getLogger(Crawler.class);
	private Parser parser;
	private UserAgentProvider userAgent;
	private final int tout = 30000;

	/**
	 * Constructor of Crawler, sets the necessary attributes to work
	 * @param t
	 */
	public Crawler(Target t) {
		log.setLevel(org.apache.log4j.Level.ALL);
		parser = new Parser(t);
		userAgent = new UserAgentProvider();
	}

	/**
	 * Main method of the class, requests the URI, queries the parser for
	 * results and return those
	 * @param u
	 * @return
	 */
	@SuppressWarnings("finally")
	public Set<String> getNewUrls(String u) {
		Set<String> urls = new HashSet<String>();
		BufferedReader in = null;
		try {
			// log.debug("Targeting [" + u + "].");

			URL url = new URL(u);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(tout);
			conn.setRequestProperty("User-Agent", userAgent.getNewUserAgent());
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String str;

			while ((str = in.readLine()) != null) {
				urls.addAll(parser.getUrls(str, u));
			}

		} catch (MalformedURLException e) {
		} catch (IOException e) {
			if (e.getMessage().contains("503 for URL")) {
				log.debug(e.getMessage());
			}
			if (e.getMessage().contains("403 for URL")) {
				log.debug(e.getMessage());
			}
		} catch (Exception e) {
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
			return urls;
		}

	}
}

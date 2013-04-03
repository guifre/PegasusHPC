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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This class is aimed at providing different User Agent strings to the Crawler
 */
public class UserAgentProvider {
	
	private BufferedReader br;
	private final String backAgent = "Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US))";
	private static Logger log = Logger.getLogger(UserAgentProvider.class);

	/**
	 * Constructor of the UserAgentProvider class, sets is config
	 */
	public UserAgentProvider() {
		log.setLevel(org.apache.log4j.Level.ALL);
		openUserAgentFile();
	}

	/**
	 * main method of the class, returns a new user agent string each time
	 * @return
	 */
	public String getNewUserAgent() {
		String agent;
		try {
			agent = br.readLine();
			if (agent != null) {
				return agent;
			} else {
				return backAgent;
			}
		} catch (IOException e) {
			log.error("User Agent file not found [" + e.getLocalizedMessage() + "].");
			openUserAgentFile();
			return backAgent;
		}

	}

	/**
	 * Opens the file that contains the user agent strings
	 */
	private void openUserAgentFile() {
		try {
			br = new BufferedReader(new FileReader("./resources/userAgentStrings"));
		} catch (FileNotFoundException e) {
			log.error("User Agent file not found [" + e.getLocalizedMessage() + "].");
		}

	}
}

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
package org.pegasushpc.agents;

import java.util.Set;

import org.apache.log4j.Logger;
import org.pegasushpc.data.DataStore;
import org.pegasushpc.web.Crawler;
import org.pegasushpc.web.Target;

public class Worker implements Runnable {
	private static Logger log = Logger.getLogger(Worker.class);

	// exec info
	private Target target;

	// concurrency storage
	private DataStore sites;
	private Crawler crawler;

	/**
	 * Constructor of the Worker class, initializes its attributes
	 * @param s
	 * @param t
	 */
	public Worker(DataStore s, Target t) {
		initWrkr(t);
		initStrg(s);
	}
	
	/**
	 * Sets the config attributes to its values
	 * @param murls
	 */
	private void initWrkr(Target t) {
		this.target = t;
	}
	
	/**
	 * Sets the main attributes to its values
	 * @param s
	 */
	private void initStrg(DataStore s) {
		this.sites = s;
		this.crawler = new Crawler(target);
	}

	/**
	 * Main method of the Worker class, queries the crawler
	 * and stores the results
	 */
	public void run() {
		Thread.currentThread();
		while (!Thread.interrupted()) {
			sites.surffingReddit();
			String target = sites.next();
			sites.picoYPala();
			Set<String> newResults = crawler.getNewUrls(target);
			sites.addAll(newResults);
		}
		log.debug("Thread "+Thread.currentThread() + " got killed, bye");
	}
}

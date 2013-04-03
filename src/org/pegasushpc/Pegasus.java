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
package org.pegasushpc;

import java.net.URISyntaxException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.pegasushpc.agents.Dispatcher;
import org.pegasushpc.utils.Interface;
import org.pegasushpc.web.Target;

/**
 * Pegasus is the main class of PegasusHPC, it parses the arguments and instanciates the dispatcher that
 * will later init the analysis.
 */
public class Pegasus {
	private static Logger log = Logger.getLogger(Pegasus.class);
	private final String version = "v1.0 (beta)";
	private final String year = "2013";
	private final String license = "GPL v3.0";
	private final String desc = "PegasusHPC is a High Performance Crawler for fast web spidering.";
	private final String codebase = "http://pegasushpc.googlecode.com";
	private final String community = "http://groups.google.com/group/pegasushpc";
	
	private Interface intrfc;
	
	private static Target host = null;
	private static int workers = -1;
	private static int maxURLs = -1;
	private static boolean scope;

	/**
	 * Main method of Pegasus inits a new instance of it.
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Pegasus(args);
	}
	
	/**
	 * Constructor of the Pegasus class
	 * @param args
	 */
	public Pegasus(String[] args) {
		setLogger();
		showHeader();
		try {
			checkArgs(args);
		} catch (Exception e) { /* if the arguments were wrong we die */
			log.error(e.getMessage());
			harakiri();
		}
		new Dispatcher(host, workers, maxURLs);
	}

	/**
	 * Displays the PegasusHPC header
	 */
	private void showHeader() {
		if (intrfc == null) {
			intrfc = new Interface(version, year, license, desc, codebase, community);
		}
		log.info(intrfc.getHeader());
	}
	
	private void harakiri() {
		log.info(intrfc.getUsage());
		System.exit(0);
	}


	/**
	 * Check the command line arguments
	 * @param args
	 * @throws URISyntaxException
	 */
	private void checkArgs(String[] args) throws URISyntaxException   {
		int wholeHost = -1;
		String host = null;
		for (int i=0; i<args.length; i++) {

			if (args[i].equals("-t")) {
				if (args.length > i+1) {
					host = args[i+1];
				}			
			} else if (args[i].equals("-nt")) {
				if (args.length > i+1) {
					workers = Integer.valueOf(args[i+1]);
				}
			} else if (args[i].equals("-max")) {
				if (args.length > i+1) {
					maxURLs = Integer.valueOf(args[i+1]);
				}
			} else if (args[i].equals("-domainBased")) {
				wholeHost = 0;
			} else if (args[i].equals("-ipBased")) {
				wholeHost = 1;
			} else if (args[i].equals("-h")) {
				harakiri();
			}
		}
		createTarget(wholeHost, host);
		logArgs(args, wholeHost);
	}
	
	/**
	 * Creates the Target object
	 * @param wholeHost
	 * @param h
	 * @throws URISyntaxException
	 */
	public void createTarget(int wholeHost, String h) throws URISyntaxException {
		if (wholeHost == -1 || h == null) {
			throw new InvalidParameterException();
		} else if (wholeHost == 0) {
			host = new Target(h, false);
		} else if (wholeHost == 1) {
			host = new Target(h, true);
		} else {
			throw new InvalidParameterException();
		}
	}
	
	/**
	 * logs the arguments
	 * @param args
	 * @param wholeHost
	 */
	private void logArgs(String[] args, int wholeHost) {
		if (workers == -1 || host == null || args.length < 4 || wholeHost == -1) {
			throw new InvalidParameterException();
		} else if (maxURLs == -1 && scope) {
			log.info("Targeting ["+host.toUrl().toString()+"] with ["+workers+"]threads and crawling the whole server and targetting all subdomains");
			maxURLs = 2147483647;
		} else if (maxURLs == -1 && !scope) {
			log.info("Targeting ["+host.toUrl().toString()+"] with ["+workers+"]threads and crawling the whole server and only targetting one subdomain");
			maxURLs = 2147483647;
		} else if (maxURLs != -1 && scope) {
			log.info("Targeting ["+host.toUrl().toString()+"] with ["+workers+"]threads and stopping at["+maxURLs+"] results and targetting all subdomains");
		} else {
			log.info("Targeting ["+host.toUrl().toString()+"] with ["+workers+"]threads and stopping at["+maxURLs+"] results and only targetting one subdomain");
		}
	}
	
	/**
	 * sets the logger config
	 */
	private void setLogger() {
		PropertyConfigurator.configure("./resources/log4j.properties");
		log.setLevel(org.apache.log4j.Level.ALL);
	}
	
}

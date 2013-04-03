package org.pegasushpc.utils;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * The Network class provides a set of utils related to networking 
 */
public class Network {
	private static Logger log = Logger.getLogger(Network.class);


	/**
	 * Computes the hostname of a given URI
	 * @param host
	 * @return
	 */
	public static String getHostName(String host) {
		try {
			return InetAddress.getByName(host).getHostAddress();
		} catch (UnknownHostException e) {
			log.debug("Could not resolve the hostname ["+host+"], "+e.getMessage());
			return null;
		}

	}
	
	/**
	 * Computes the IP address of a given URI
	 * @param host
	 * @return
	 */
	public static String getIp(URI host) {
		try {
			return InetAddress.getByName(host.getHost()).getHostAddress();
		} catch (UnknownHostException e) {
			log.debug("Could not resolve the hostname ["+host+"], "+e.getMessage());
			return null;
		}

	}
	
	/**
	 * Computes the IP of a given URL
	 * @param host
	 * @return
	 */
	public static String getIp(URL host) {
		try {
			return InetAddress.getByName(host.getHost()).getHostAddress();
		} catch (UnknownHostException e) {
			log.debug("Could not resolve the hostname ["+host+"], "+e.getMessage());
			return null;
		}

	}
}

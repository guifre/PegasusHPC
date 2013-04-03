package org.pegasushpc.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pegasushpc.utils.Network;

public class Target {

	private static Logger log = Logger.getLogger(Target.class);

	private URI target = null;
	private boolean wholeHost;
	
	private List<String> attackSurface;
	private List<String> outOfScope;
	
	/**
	 * Constructor of the Target class, sets the attributes and prints the info
	 * @param t
	 * @param wholeHost
	 * @throws URISyntaxException
	 */
	public Target(String t, boolean wholeHost) throws URISyntaxException {
		
		target = new URI(t);
		this.wholeHost = wholeHost;
		if (wholeHost) {
			attackSurface = new ArrayList<String>();
			attackSurface.add(target.getHost());
			outOfScope = new ArrayList<String>();
		}
		
		log.info("Settings: authentication["+target.getUserInfo()+"] protocol[" + target.getScheme()+ "] host["
				+ target.getHost() + "] port[" + getPort() + "] path["
				+ getPath() + "] scanning the whole host["+wholeHost+"].");
	}
	
	/**
	 * Returns the port of the targeted host
	 * @return
	 */
	public int getPort() {
		
		if ( target.getPort() == -1) {
			return 80;
		} else {
			return target.getPort();
		}
	}
	
	/**
	 * Returns the port of the targeted host
	 * @return
	 */
	public String getPath() {
		
		if ( target.getPath() == null || target.getPath().equals("") ) {
			return "/";
		} else {
			return target.getPath();
		}
	}

	/**
	 * Returns whether the parameter host is targetable or not
	 * @param host
	 * @return
	 */
	public synchronized boolean isTargetable(String host) {
		URI newHost;
		
		try {
			newHost = new URI(host);
			//only target one domain
		} catch (URISyntaxException e) {
			return false;
		}
		if (!wholeHost) {
			//is same domain
			return isSameDomain(newHost);
		} else {
			return isSameHost(newHost);
		}
	}
	
	/**
	 * Returns whether the parameter host is the same as the targeted one
	 * @param newHost
	 * @return
	 */
	public boolean isSameHost(URI newHost) {
		if (inAttackSurface(newHost)) {
			return true;
		} else if(knownOutOfAttackSurfface(newHost)){
			return false;
		}
		if (isNewTarget(newHost)) {
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * Returns whether the parameter host is the same as the targeted one
	 * @param newHost
	 * @return
	 */
	public boolean isSameDomain(URI newHost) {
		if (target.getHost().equals(newHost.getHost())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns whether the parameter URI has the same ip as the target
	 * @param newHost
	 * @return
	 */
	public boolean isNewTarget(URI newHost) {
		if (Network.getIp(target).equals(Network.getIp(newHost))) {
			attackSurface.add(newHost.getHost());
			return true;
		}
		outOfScope.add(newHost.getHost());
		return false;
	}
	
	/**
	 * Returns whether the URI parameter is known out of scope
	 * @param newHost
	 * @return
	 */
	public boolean knownOutOfAttackSurfface(URI newHost) {
		for (String host : outOfScope) {
			if (newHost.getHost().equals(host)) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Returns whether the URI parameter is known to be in attack surface
	 * @param newHost
	 * @return
	 */
	public boolean inAttackSurface(URI newHost) {

		for (String host : attackSurface) {
			if (newHost.getHost().equals(host)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the targeted URI
	 * @return
	 */
	public URI toUrl() {
			return target;
		
	}
	
	/**
	 * Returns the targeted URI
	 * @return
	 */
	public URI getTarget() {
		return target;
	}
	
	/**
	 * Returns the attack surface
	 * @return
	 */
	public List<String> getAttackSurface() {
		return this.attackSurface;
	}
	
	/**
	 * Returns the URIs out of scope
	 * @return
	 */
	public List<String> getOutOfScope() {
		return this.outOfScope;
	}
}

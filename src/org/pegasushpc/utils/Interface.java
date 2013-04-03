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
package org.pegasushpc.utils;

/**
 * The Interface class is in charge of printing user-related info to the cmd
 */
public class Interface {
	private final String version;
	private final String year;
	private final String license;
	private final String desc;
	private final String codebase;
	private final String community;
	
	/**
	 * Constructor of the Interface class
	 * @param ver
	 * @param ye
	 * @param li
	 * @param des
	 * @param code
	 * @param comm
	 */
	public Interface(String ver, String ye, String li, String des, String code, String comm) {
		version = ver;
		year = ye;
		license = li;
		desc = des;
		codebase = code;
		community = comm;
	}
	
	/**
	 * Shows the cmd options to properly use pegasus
	 * @return
	 */
	public String getUsage() {

		return  "Usage: $java -Xmx4000m pegasus.jar -t {target} -nt {#threads} -max {#URIs} [-ipBased | -domainBased]\n\n" +
				"    -t {target} Complete URI of the targeted host i.e. http://google.com" +
				"\n    -nt {#threads} Number of threads to create. Suggested between 500-10000." +
				"\n    -max {#URIs} Number of unique URIs to crawl." +
				"\n    -[ipBased] to follow links to different domain of the same server."+
				"\n    -[domainBased] to target a specific host or path"
				+"\n\nUse -h for to print this menu";
	}
	
	/**
	 * Displays the pegasus header
	 * @return
	 */
	public String getHeader() {

		return "#####################################################################################################\n"
			 + "# " + desc + " - " + license + " - " + version + " - " + year + "  #\n"
			 + "#####################################################################################################\n\n"
			 + "Official Site\n" + codebase + "\n\nCommunity&Help\n" + community + "\n\n"
			 +"###########################v##########################################################################\n\n";		 
	}
}



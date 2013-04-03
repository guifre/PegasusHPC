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

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pegasushpc.web.Target;
/**
 * The Parser class parses the HTML of the target responses, seeks and
 * returns the new targeted URIs
 */
public class Parser {

	private final Target target;

	private Pattern patterns[];
	
	//html attributes to match in the body content
	private static String[] regexes = {
		"\\s*(?i)href\\s*=\\s*(\"([^\"](.*?)\")|'[^'](.*?)'|([^'\">\\s]+))",
		"\\s*(?i)action\\s*=\\s*(\"([^\"](.*?)\")|'[^'](.*?)'|([^'\">\\s]+))",
		"\\s*(?i)src\\s*=\\s*(\"([^\"](.*?)\")|'[^'](.*?)'|([^'\">\\s]+))" };

	
	/**
	 * Constructor of the Parser class, inits the attributes to work
	 * @param t
	 */
	public Parser(Target t) {
		this.target = t;
		patterns = new Pattern[3];
		for (int i = 0; i<3; i++) {
			patterns[i] = Pattern.compile(regexes[i]);
		}
	}
	
	/**
	 * Main method of the class, parses the HTML and returns the new URIs.
	 * @param code
	 * @param cTarget
	 * @return
	 */
	public Set<String> getUrls(String code, String cTarget) {
		Set<String> links = new HashSet<String>();
		for(Pattern p : patterns) {
			Matcher m = p.matcher(code);

			if (m.find()) {
				String newTarget = m.group(1).replace("\"","").replace("'", "");
				//log.info("Found ["+ newTarget +"] current target was ["+cTarget+"] and crawl target ["+target.getHost()+"].");
				try {
				URI tar = new URI(newTarget);
				
				if(tar.isAbsolute()) {
					if (target.isTargetable(newTarget)) {
						//log.info("Added Absolut target ["+ target.toString()+ "] mixed from ["+cTarget + "] and ["+newTarget+"]");
						links.add(newTarget);
					} else {
						//log.info("not targetable ["+newTarget+"]");
					}
				} else {
					String target = new URI(new URL(new URL(cTarget.toString()), newTarget).toString()).toString();
					//log.info("Added Relative target ["+ target.toString()+ "] mixed from ["+cTarget + "] and ["+newTarget+"]");
					links.add(target);
				}
				}catch(Exception e) {
				}
				
			}
		}
		return links;
	}


}

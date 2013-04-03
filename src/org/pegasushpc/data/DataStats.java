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
package org.pegasushpc.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.pegasushpc.web.Target;

/**
 * The DataStats class is in charge of storing the final results of each analysis
 */
public class DataStats {
	private static Logger log = Logger.getLogger(DataStats.class);
	private String ofile;

	/**
	 * Constructor of the DataStats class, triggers the rest of the methods
	 * @param target
	 * @param time
	 * @param queue
	 */
	public void generateStats(Target target, long time, DataStore queue) {
		ofile = "./out/" + target.getTarget().getHost() + "_results.out";
		
		showStats(time, queue);
		saveResults(queue, target, time);
		
		log.info("Finished all threads");
	}
	
	/**
	 * Shows the performance statics of the analysis
	 * @param time
	 * @param queue
	 */
	private void showStats(long time, DataStore queue) {
		log.info("Total analysis time [" + time / 1000 + "] seconds");
		log.info("Found and analyzed [" + queue.getResults().size() + "] unique pages");
		log.info("Found other [" + queue.getQueued() + "] unique pages not processed");
		log.info("Process ratio of " + queue.getResults().size() * 1000
				/ time + " URIs/second");
		log.info("Finding ratio of " + (queue.getResults().size()+queue.getQueued()) * 1000
				/ time + " URIs/second");

	}

	/**
	 * Shows the results of the analysis
	 * @param queue
	 * @param target
	 * @param time
	 * @return
	 */
	public boolean saveResults(DataStore queue, Target target, long time) {
		
		File file = new File(ofile);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}
		
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("#############################################\n");
			bw.write("Report for target [" + target.getTarget() + "]\n");
			bw.write("#############################################\n");

			bw.write("Stats: Took[" + time / 1000 + "] secs");
			bw.write(" to process[" + queue.getResults().size() + "] pages");
			bw.write(" and find other[" + queue.getQueued() + "] unique pages");
			bw.write(" crawling ration of [" + (queue.getQueued()+queue.getResults().size()) * 1000
					/ time + "] URLs per second.\n\n");
			bw.write("\n\n#############################################\n");
			bw.write("Hosts Found in Attack Surface:\n");
			bw.write("#############################################\n");
			synchronized (target) {
				for (String u : target.getAttackSurface()) {
					bw.write("" + u + "\n");
				}
			}
			bw.write("\n\n#############################################\n");
			bw.write("Hosts Found Out of Scope:\n");
			bw.write("#############################################\n");

			synchronized (target) {
				for (String u : target.getOutOfScope()) {
					bw.write("" + u + "\n");
				}
			}
			
			
			bw.write("\n\n#############################################\n");
			bw.write("List of Unique URLs:\n");
			bw.write("#############################################\n");
			for (String u : queue.getResults()) {
				bw.write("" + u + "\n");
			}
		
			
			
			bw.write("\n\n#############################################\n");
			bw.write("List of Queued URLs:\n");
			bw.write("#############################################\n");
			
			String tmpRes = (String) queue.getQueue().poll();
			while (tmpRes != null) {
				bw.write("" + tmpRes.toString() + "\n");
				tmpRes = (String) queue.getQueue().poll();

			}
			
			log.info("check out the results in ["+ofile+"].");

		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		} catch (Exception e) {
			log.error("could not process the results " + e.getLocalizedMessage());
			System.exit(0);
		} finally {
			try {
				System.out.println("Done");
				bw.close();
			} catch (IOException e) {
				log.error(e.getMessage());

				return false;
			}
		}
		return true;
	}

}

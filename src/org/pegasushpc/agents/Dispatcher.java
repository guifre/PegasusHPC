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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.pegasushpc.data.DataStats;
import org.pegasushpc.data.DataStore;
import org.pegasushpc.web.Target;

/**
 * The Dispatcher class creates the workers and controls its execution 
 */
public class Dispatcher {

	private static Logger log = Logger.getLogger(Dispatcher.class);

	/*config attrs*/
	private Target target;
	private final int threads;
	private final int maxURLs;

	/*main attr*/
	private DataStore queue;
	

	/**
	 * This is the constructor of the class
	 * @param target
	 * @param nworkers
	 * @param maxURLs
	 */
	public Dispatcher(Target target, int nworkers, int maxURLs) {
		this.target = target;
		this.threads = nworkers;
		this.maxURLs = maxURLs;
		this.queue = new DataStore(nworkers);
		
		long time = schedule();
		(new DataStats()).generateStats(target, time, queue);

		System.exit(0);
	}

	/**
	 * Determines the pooling time
	 */
	private void makeSomeCoffe() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		} catch(Exception e) {
			System.exit(0);
		}
	}
	
	
	/**
	 * Main method of the class, creates workers and checks its state
	 * @return
	 */
	private long schedule() {
		long startTime = System.currentTimeMillis();

		queue.add(target.toUrl().toString());

		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Runnable> list = new ArrayList<Runnable>();
		for (int i = 0; i < threads; i++) {
			Runnable worker = new Worker(queue, target);
			list.add(worker);
			executor.execute(worker);
		}
		
		log.info("[OK]Pool of " + threads + " threads successfully created, party gone wild");
		
		int old = 0;
		executor.shutdown();
		
		while (!executor.isTerminated()) {
			log.info("[OK]Current unique URIs["+queue.getResults().size() + "] max urls["+ this.maxURLs + "] idle threads ["+queue.areSurffingReddit()+ "] URIs queued ["+queue.getQueued()+"].");
			if (queue.getResults().size() >= this.maxURLs || (queue.areSurffingReddit() && queue.getQueued() == 0) && queue.getResults().size() == old) {
				log.info("Stopping!!!!");
				executor.shutdownNow();
				executor.shutdown();
				break;
			}			
			old = queue.getResults().size();
			makeSomeCoffe();
		}
		return System.currentTimeMillis() - startTime;	
	}
	




}

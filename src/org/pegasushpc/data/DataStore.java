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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The DataStore class is used to store temporary results
 */
public class DataStore {
	private Set<String> results;
	private Set<String> queue_checker;
	BlockingQueue<String> queue;
	private final int workers;
	private int surffingReddit;

	
	/**
	 * Constructor of the class, inits the necessary attributes
	 * @param nw
	 */
	public DataStore( int nw) {
		workers = nw;
		init();
	}
	
	/**
	 * inits some attributes
	 */
	private void init() {
		results = new HashSet<String>();
		queue_checker = new HashSet<String>();
		surffingReddit = 0;
		queue = new LinkedBlockingQueue<String>();
	}
	
	/**
	 * Adds an element to the queue
	 * @param site
	 */
	private void que(String site) {
		synchronized (queue_checker) {
			queue_checker.add(site);
		}
		try {
			queue.put(site);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Returns a set of new targets
	 * @return
	 */
	public synchronized List<String> nextSet() {
		List<String> res = new ArrayList<String>();

		if( queue.size() / workers<=0) {
			res.add(next());
			return res;
		}
		for (int i=0; i<queue.size()/workers;i++) {
			res.add(next());
		}
		return res;
	}
	
	/**
	 * Get next site to crawl. Can return null (if nothing to crawl)
	 * @throws InterruptedException 
	 */
	public String next() {
		String s=null;
		try {
			s = queue.take();
		} catch (InterruptedException e) {
		}
		synchronized (queue_checker) {
			queue_checker.remove(s);
		}
		synchronized (results) {
			results.add(s);
		}
		return s;
	}

	/**
	 * adds an element to the queue
	 * @param site
	 */
	public  void add(String site)  {
		if (!results.contains(site) && !queue_checker.contains(site)) {
				que(site);
		}
	}


	/**
	 * adds a set of elements to the queue
	 * @param sites
	 */
	public  void addAll(Set<String> sites) {
		for (String u : sites) {
			if (!results.contains(u) && !queue_checker.contains(u)) {
				que(u);
			}
		}
	}

	/**
	 * returns the elements queued
	 * @return
	 */
	public int getQueued() {
		return queue.size();
	}

	/**
	 * returns the results set
	 * @return
	 */
	public Set<String> getResults() {
		return results;
	}
	
	/**
	 * increases the idle threads
	 */
	public synchronized void surffingReddit() {
		this.surffingReddit++;
	}
	
	/**
	 * decreases the idle threads
	 */
	public synchronized void picoYPala() {
		this.surffingReddit--;
	}
	
	/**
	 * returns whether all threads are idle or not (stop condition)
	 * @return
	 */
	public synchronized boolean areSurffingReddit() {
		return this.surffingReddit >= workers;
	}
	
	/**
	 * returns the queue of targets
	 * @return
	 */
	public BlockingQueue<String> getQueue() {
		return this.queue;
	}

	/**
	 * returns a set of URIs to crawl
	 * @param a
	 * @return
	 */
	public  Set<String> nextSet(String a)  {	
			Set<String> urls = new HashSet<String>();
			if( queue.size() / workers<=0) {
				urls.add(next());
				return urls;
			}
			for (int i = 0; i < queue.size() / workers; i++) {
				urls.add(next());
			}
			return urls;
	}

	/**
	 * prints the results
	 */
	@Override
	public String toString() {
		String tmp="";
		for(String s : results) {
			tmp = tmp.concat(s).concat(", ");
		}
		tmp.substring(0, tmp.length()-2);
		return tmp;
	}

}

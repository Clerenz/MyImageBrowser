package de.clemensloos.imagebrowser.utils;


import java.util.Map;
import java.util.TreeMap;


public class ThreadPooler {

	private static volatile Map<Integer, Object> higPrioMap = new TreeMap<Integer, Object>();
	private static volatile Map<Integer, Object> lowPrioMap = new TreeMap<Integer, Object>();

	private static int maxNumThreads = 1;

	private static volatile int numThreads = 0;


	public static void aquire(int position, Object inst, boolean higPrio) {

		if (numThreads >= maxNumThreads) {
			if (higPrio) {
				higPrioMap.put(position, inst);
			}
			else {
				lowPrioMap.put(position, inst);
			}
			try {
				synchronized (inst) {
					inst.wait();
				}
			} catch (InterruptedException e) {
				// TODO
			}
		}

		numThreads++;

	}


	public static void release() {

		numThreads--;

		Object inst = higPrioMap.remove(higPrioMap.keySet().iterator().next());
		if (inst == null) {
			inst = lowPrioMap.remove(lowPrioMap.keySet().iterator().next());
		}
		if (inst != null) {
			synchronized (inst) {
				inst.notify();
			}

		}
	}

}

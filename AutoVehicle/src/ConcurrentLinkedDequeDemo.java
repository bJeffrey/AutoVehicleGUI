//package com.concretepage.util.concurrent;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ConcurrentLinkedDequeDemo {
	static ConcurrentLinkedDeque<String> linkedDeque=new ConcurrentLinkedDeque<String>();
	public static void main(String[] args) {
		int numThreads = 3;
		ExecutorService exService = Executors.newFixedThreadPool(numThreads);
		LinkedDequeProducerIMU elementAdd = new ConcurrentLinkedDequeDemo().new LinkedDequeProducerIMU();
		LinkedDequeConsumer elementGet = new ConcurrentLinkedDequeDemo().new LinkedDequeConsumer();
		LinkedDequeProducerTDOA elementAdd2 = new ConcurrentLinkedDequeDemo().new LinkedDequeProducerTDOA();
		exService.execute(elementAdd);
		exService.execute(elementAdd2);
		exService.execute(elementGet);
		exService.shutdown();
	}
	class LinkedDequeProducerIMU implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<5;i++){
					linkedDeque.add("Thread A: "+i);
			}
		}
	}
	
	class LinkedDequeConsumer implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<10;i++){
					String s= linkedDeque.poll();
					System.out.println("Element received is: "+s);
			}
		}
	}
	class LinkedDequeProducerTDOA implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<5;i++){
					linkedDeque.add("Thread B: "+i);
			}
		}
	}
}
 
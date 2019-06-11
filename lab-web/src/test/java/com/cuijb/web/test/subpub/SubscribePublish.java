package com.cuijb.web.test.subpub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SubscribePublish<M> {

	// 订阅器名称
	private String name;
	// 订阅器队列容量
	final int QUEUE_CAPACITY = 3;
	// 订阅器存储队列
	private BlockingQueue<Msg<M>> queue = new ArrayBlockingQueue<Msg<M>>(QUEUE_CAPACITY);
	// 订阅者
	private List<ISubcriber<M>> subcribers = new ArrayList<ISubcriber<M>>();

	public SubscribePublish(String name) {
		this.name = name;
	}

	public void publish(String publisher, M message, boolean isInstantMsg) {
		if (isInstantMsg) {
			update(publisher, message);
			return;
		}
		Msg<M> m = new Msg<M>(publisher, message);
		if (!queue.offer(m)) {
			update();
			try {
				queue.put(m);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void subcribe(ISubcriber<M> subcriber) {
		subcribers.add(subcriber);
	}

	public void unSubcribe(ISubcriber<M> subcriber) {
		subcribers.remove(subcriber);
	}

	public void update() {
		Msg<M> m = null;
		while ((m = queue.poll()) != null) {
			this.update(m.getPublisher(), m.getMsg());
		}
	}

	public void update(String publisher, M Msg) {
		for (ISubcriber<M> subcriber : subcribers) {
			subcriber.update(publisher, Msg);
		}
	}
}

class Msg<M> {
	private String publisher;
	private M m;

	public Msg(String publisher, M m) {
		this.publisher = publisher;
		this.m = m;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public M getMsg() {
		return m;
	}

	public void setMsg(M m) {
		this.m = m;
	}
}

package com.cuijb.web.test.subpub;

public class Publisher<M> implements IPublisher<M> {
	private String name;

	public Publisher(String name) {
		super();
		this.name = name;
	}

	@Override
	public void publish(SubscribePublish<M> subscribePublish, M message, boolean isInstantMsg) {
		subscribePublish.publish(this.name, message, isInstantMsg);
	}
}

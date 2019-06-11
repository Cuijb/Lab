package com.cuijb.web.test.subpub;

public interface IPublisher<M> {
	public void publish(SubscribePublish<M> subscribePublish, M message, boolean isInstantMsg);
}

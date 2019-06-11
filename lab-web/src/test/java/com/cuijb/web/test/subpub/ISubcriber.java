package com.cuijb.web.test.subpub;

public interface ISubcriber<M> {

	public void subcribe(SubscribePublish<M> subscribePublish);

	public void unSubcribe(SubscribePublish<M> subscribePublish);

	public void update(String publisher, M message);
}

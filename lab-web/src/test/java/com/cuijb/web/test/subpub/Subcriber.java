package com.cuijb.web.test.subpub;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Subcriber<M> implements ISubcriber<M> {
	public String name;

	public Subcriber(String name) {
		super();
		this.name = name;
	}

	@Override
	public void subcribe(SubscribePublish<M> subscribePublish) {
		subscribePublish.subcribe(this);
	}

	@Override
	public void unSubcribe(SubscribePublish<M> subscribePublish) {
		subscribePublish.unSubcribe(this);
	}

	@Override
	public void update(String publisher, M message) {
		log.info("{} 收到 {} 发来的消息: {}", name, publisher, message.toString());
	}

}

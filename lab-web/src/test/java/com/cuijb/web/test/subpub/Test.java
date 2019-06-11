package com.cuijb.web.test.subpub;

public class Test {

	@org.junit.Test
	public void aaa() {
		SubscribePublish<String> subscribePublish = new SubscribePublish<String>("订阅器");
		IPublisher<String> publisher1 = new Publisher<String>("发布者1");
		ISubcriber<String> subcriber1 = new Subcriber<String>("订阅者1");
		ISubcriber<String> subcriber2 = new Subcriber<String>("订阅者2");
		subcriber1.subcribe(subscribePublish);
		publisher1.publish(subscribePublish, "welcome", true);
		subcriber2.subcribe(subscribePublish);
		publisher1.publish(subscribePublish, "to", true);
		publisher1.publish(subscribePublish, "xx", false);
		publisher1.publish(subscribePublish, "yy", false);
		publisher1.publish(subscribePublish, "zz", false);
		publisher1.publish(subscribePublish, "aa", false);
		publisher1.publish(subscribePublish, "bb", false);
		publisher1.publish(subscribePublish, "cc", false);
		publisher1.publish(subscribePublish, "dd", false);
		publisher1.publish(subscribePublish, "ee", false);
		publisher1.publish(subscribePublish, "ff", false);
		publisher1.publish(subscribePublish, "hh", false);
		publisher1.publish(subscribePublish, "ii", false);
		publisher1.publish(subscribePublish, "jj", false);
		publisher1.publish(subscribePublish, "kk", false);
	}
}

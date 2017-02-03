package com.cuijb.web.test;

import com.google.gson.Gson;

public class BaseTest {
	public String toStr(Object obj) {
		return new Gson().toJson(obj);
	}

	public <T> T parse(String str, Class<T> clazz) {
		return new Gson().fromJson(str, clazz);
	}
}

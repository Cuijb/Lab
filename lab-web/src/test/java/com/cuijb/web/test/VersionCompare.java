package com.cuijb.web.test;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersionCompare extends BaseTest {

	@Test
	public void versionTest() {
		String oldVersion = "1.0.1";
		String newVersion = "1.0";
		System.out.println(
				"compare " + oldVersion + " <> " + newVersion + " :" + validateVersion(oldVersion, newVersion));
	}

	/**
	 * 主版本号.次版本号.修正版本号.编译版本号
	 * 
	 * @param currVersion
	 * @param newVersion
	 * @return
	 */
	private boolean validateVersion(String currVersion, String newVersion) {
		// 新版本号为空
		if (null == newVersion || "".equals(newVersion)) {
			return false;
		}
		// 版本号完全一致
		if (newVersion.equals(currVersion)) {
			return false;
		}

		// 两个版本号不同
		String[] oldVersionAry = currVersion.split("\\.");
		String[] newVersionAry = newVersion.split("\\.");
		int minLength = Math.min(oldVersionAry.length, newVersionAry.length);

		// 相同位置相等则比较下一位
		// 相同位置不等，则比较newPart是否大于oldPart
		for (int i = 0; i < minLength; i++) {
			Integer oldPart = Integer.valueOf(oldVersionAry[i]);
			Integer newPart = Integer.valueOf(newVersionAry[i]);
			if (oldPart == newPart) {
				continue;
			}
			return newPart > oldPart;
		}

		// 相同位置均相等，新版本号更长则为新
		return newVersionAry.length > oldVersionAry.length;
	}
}

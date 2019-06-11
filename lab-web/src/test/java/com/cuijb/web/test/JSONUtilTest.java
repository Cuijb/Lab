package com.cuijb.web.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.google.gson.Gson;

/**
 * <br>
 * Created by Cuijb on 2018/11/22<br>
 */
public class JSONUtilTest {
	private long count = 1000;

	@Test
	public void bfpInfo() {
		String bfpStr = null;
		long beginTime = System.currentTimeMillis();
		for (int k = 0; k < count; k++) {
			SymbolsFileInfo fileInfo = new SymbolsFileInfo();
			fileInfo.SignedBlockIndex = 1045;
			fileInfo.SymbolsFilePath = "/storage/emulated/0/lband_server/fe8aa1db-596b-4ef0-afce-ee4d94640db5/storage_emulated_0/resource/vod/1002/0/15/bfp.raptor";
			fileInfo.InfoFilePath = "/storage/emulated/0/lband_server/fe8aa1db-596b-4ef0-afce-ee4d94640db5/storage_emulated_0/resource/vod/1002/0/15/bfp.raptor.info";
			fileInfo.SymbolLen = 1084;
			fileInfo.FESILen = 4;
			fileInfo.ExtSymbolLen = 1088;
			fileInfo.BlockCount = 1046;
			fileInfo.BlockMaxSymbolCount = 1039;
			fileInfo.Completed = false;

			for (int i = 0; i < fileInfo.BlockCount; i++) {
				BlockNoteInfo blockInfo = new BlockNoteInfo();
				blockInfo.BlockIndex = i;
				blockInfo.BeginOffset = 1130436 * i;
				blockInfo.EndOffset = 1130436 * (i + 1) - 1;
				blockInfo.CurSymbolsCount = (int) Math.round(Math.random() * 1046);
				blockInfo.WritePos = blockInfo.CurSymbolsCount * 1088 + 4;
				blockInfo.MaxSymbolsCount = 1039;
				blockInfo.MinEsi = 2111;
				blockInfo.Completed = false;
				fileInfo.BlocksArray.add(blockInfo);
			}

			// long objTime = System.currentTimeMillis() - beginTime;
			// new Gson().toJson(fileInfo);
			bfpStr = new Gson().toJson(fileInfo);
			// long jsonTime = System.currentTimeMillis() - beginTime - objTime;
			// System.out.println("GSONUtil object to string - " + k + " -> " +
			// objTime + " : " + jsonTime);
			// if (k < 5) {
			// System.out.println(fileStr);
			// }
		}
		long toStrTime = System.currentTimeMillis() - beginTime;
		System.out.println("GSONUtil object to string, count:" + count + ", cost: " + toStrTime);
	}

	@Test
	public void jsonObj() {
		try {
			String bfpStr = null;
			long beginTime = System.currentTimeMillis();
			for (int k = 0; k < count; k++) {
				JSONObject fileJson = new JSONObject();
				fileJson.put("SignedBlockIndex", 1045);
				fileJson.put("SymbolsFilePath",
						"/storage/emulated/0/lband_server/fe8aa1db-596b-4ef0-afce-ee4d94640db5/storage_emulated_0/resource/vod/1002/0/15/bfp.raptor");
				fileJson.put("InfoFilePath",
						"/storage/emulated/0/lband_server/fe8aa1db-596b-4ef0-afce-ee4d94640db5/storage_emulated_0/resource/vod/1002/0/15/bfp.raptor.info");
				fileJson.put("SymbolLen", 1084);
				fileJson.put("FESILen", 4);
				fileJson.put("ExtSymbolLen", 1088);
				fileJson.put("BlockCount", 1046);
				fileJson.put("BlockMaxSymbolCount", 1039);
				fileJson.put("Completed", false);

				JSONArray jsonArray = new JSONArray();

				for (int i = 0; i < 1046; i++) {
					JSONObject blockJson = new JSONObject();
					blockJson.put("BlockIndex", i);
					blockJson.put("BeginOffset", 1130436 * i);
					blockJson.put("EndOffset", 1130436 * (i + 1) - 1);
					int symbolCount = (int) Math.round(Math.random() * 1046);
					blockJson.put("CurSymbolsCount", symbolCount);
					blockJson.put("WritePos", symbolCount * 1088 + 4);
					blockJson.put("MaxSymbolsCount", 1039);
					blockJson.put("MinEsi", 2111);
					blockJson.put("Completed", false);
					jsonArray.put(blockJson);
				}
				fileJson.put("BlocksArray", jsonArray);

				// long objTime = System.currentTimeMillis() - beginTime;
				// fileJson.toString();
				bfpStr = fileJson.toString();
				// long jsonTime = System.currentTimeMillis() - beginTime -
				// objTime;
				// System.out.println("GSONObj object to string - " + k + " ->
				// " + objTime + " : " + jsonTime);
				// if (k < 5) {
				// System.out.println(fileStr);
				// }
			}
			long toStrTime = System.currentTimeMillis() - beginTime;
			System.out.println("GSONObj object to string, count:" + count + ", cost: " + toStrTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

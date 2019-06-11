package com.cuijb.web.test;

import java.util.ArrayList;

/**
 * <br>
 * Created by Cuijb on 2018/11/22<br>
 */
public class SymbolsFileInfo {
	protected int SignedBlockIndex = -1;
	protected String SymbolsFilePath = "";
	protected String InfoFilePath = "";
	protected String BackupInfoFilePath = "";
	protected int SymbolLen = -1;
	protected int FESILen = -1;
	protected int ExtSymbolLen = -1;
	protected int BlockCount = -1;
	protected int BlockMaxSymbolCount = -1;
	protected boolean Completed = false;
	protected ArrayList<BlockNoteInfo> BlocksArray = new ArrayList<>();

	public int getSignedBlockIndex() {
		return SignedBlockIndex;
	}

	public void setSignedBlockIndex(int signedBlockIndex) {
		SignedBlockIndex = signedBlockIndex;
	}

	public String getSymbolsFilePath() {
		return SymbolsFilePath;
	}

	public void setSymbolsFilePath(String symbolsFilePath) {
		SymbolsFilePath = symbolsFilePath;
	}

	public String getInfoFilePath() {
		return InfoFilePath;
	}

	public void setInfoFilePath(String infoFilePath) {
		InfoFilePath = infoFilePath;
	}

	public String getBackupInfoFilePath() {
		return BackupInfoFilePath;
	}

	public void setBackupInfoFilePath(String backupInfoFilePath) {
		BackupInfoFilePath = backupInfoFilePath;
	}

	public int getSymbolLen() {
		return SymbolLen;
	}

	public void setSymbolLen(int symbolLen) {
		SymbolLen = symbolLen;
	}

	public int getFESILen() {
		return FESILen;
	}

	public void setFESILen(int FESILen) {
		this.FESILen = FESILen;
	}

	public int getExtSymbolLen() {
		return ExtSymbolLen;
	}

	public void setExtSymbolLen(int extSymbolLen) {
		ExtSymbolLen = extSymbolLen;
	}

	public int getBlockCount() {
		return BlockCount;
	}

	public void setBlockCount(int blockCount) {
		BlockCount = blockCount;
	}

	public int getBlockMaxSymbolCount() {
		return BlockMaxSymbolCount;
	}

	public void setBlockMaxSymbolCount(int blockMaxSymbolCount) {
		BlockMaxSymbolCount = blockMaxSymbolCount;
	}

	public boolean isCompleted() {
		return Completed;
	}

	public void setCompleted(boolean completed) {
		Completed = completed;
	}

	public ArrayList<BlockNoteInfo> getBlocksArray() {
		return BlocksArray;
	}

	public void setBlocksArray(ArrayList<BlockNoteInfo> blocksArray) {
		BlocksArray = blocksArray;
	}
}

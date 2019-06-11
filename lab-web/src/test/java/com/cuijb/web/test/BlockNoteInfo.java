package com.cuijb.web.test;

/**
 * <br>
 * Created by Cuijb on 2018/11/22<br>
 */
public class BlockNoteInfo {
	protected int BlockIndex = -1;
	protected long BeginOffset = -1;
	protected long EndOffset = -1;
	protected long WritePos = -1;
	protected long ReadPos = -1;
	protected long OrgRWPos = -1;
	protected int CurSymbolsCount = 0;
	protected int MaxSymbolsCount = 0;
	protected int MinEsi = -1;
	protected boolean Completed = false;

	public int getBlockIndex() {
		return BlockIndex;
	}

	public void setBlockIndex(int blockIndex) {
		BlockIndex = blockIndex;
	}

	public long getBeginOffset() {
		return BeginOffset;
	}

	public void setBeginOffset(long beginOffset) {
		BeginOffset = beginOffset;
	}

	public long getEndOffset() {
		return EndOffset;
	}

	public void setEndOffset(long endOffset) {
		EndOffset = endOffset;
	}

	public long getWritePos() {
		return WritePos;
	}

	public void setWritePos(long writePos) {
		WritePos = writePos;
	}

	public long getReadPos() {
		return ReadPos;
	}

	public void setReadPos(long readPos) {
		ReadPos = readPos;
	}

	public long getOrgRWPos() {
		return OrgRWPos;
	}

	public void setOrgRWPos(long orgRWPos) {
		OrgRWPos = orgRWPos;
	}

	public int getCurSymbolsCount() {
		return CurSymbolsCount;
	}

	public void setCurSymbolsCount(int curSymbolsCount) {
		CurSymbolsCount = curSymbolsCount;
	}

	public int getMaxSymbolsCount() {
		return MaxSymbolsCount;
	}

	public void setMaxSymbolsCount(int maxSymbolsCount) {
		MaxSymbolsCount = maxSymbolsCount;
	}

	public int getMinEsi() {
		return MinEsi;
	}

	public void setMinEsi(int minEsi) {
		MinEsi = minEsi;
	}

	public boolean isCompleted() {
		return Completed;
	}

	public void setCompleted(boolean completed) {
		Completed = completed;
	}
}

package com.frt.autodetection.serial;

/**
 * serial receive parser flow
 * 
 * @author frt
 * @link 
 * @Copyright BeiJing fusionreality Co.ltd
 */
public class SerialStream {
	private int start = 0;
	private int end = 0;
	private int offset = 1;
	private byte[] buffer;

	public SerialStream() {
		buffer = new byte[128];
	}

	public SerialStream(int capacity) {
		buffer = new byte[capacity];
	}

	public int getLength() {
		return end - start;
	}

	public void skip(int size) {
		start += size;
		if (start >= end) {
			start = end = 0;
		}
	}

	public void pack() {
		if (start > 0) {
			if (start >= end) {
				start = end = 0;
			} else {
				end -= start;
				System.arraycopy(buffer, 0, buffer, start, end);
				start = 0;
			}
		}
	}

	public void put(byte[] data) {
		put(data, data.length);
	}

	public void put(byte[] data, int length) {
		put(data, 0, length);
	}

	public void put(byte[] data, int start, int length) {
		pack();
		System.arraycopy(data, start, buffer, end, length);
		end += length;
	}

	public boolean hasNext() {
		return start < end;
	}

	public void getByte(byte[] data, int length) {
		System.arraycopy(buffer, start, data, 0, length);
		start += length;
	}

	public byte nextByte() {
		if (start >= end) {
			return 0;
		}
		byte data = buffer[start++];
		return data;
	}

	public short nextShort() {
		if (start >= end) {
			return 0;
		}
		int data = buffer[start++];
		data = data & 0xff;
		return (short) data;
	}

	public int nextInt() {
		if (start >= end) {
			return 0;
		}
		int data = buffer[start++];
		data = data & 0xff;
		return data;
	}

	public int nextInt2() {
		int val1 = nextInt();
		int val2 = nextInt();
		int val = ((val1 & 0xff) << 8) + (val2 & 0xff);
		return val;
	}

	public int getInt(int pos) {
		int position = start + pos - offset;
		if (position >= end) {
			return 0;
		}
		int data = buffer[position];
		data = data & 0xff;
		return data;
	}

	public int getInt2(int pos) {
		int val1 = getInt(pos);
		int val2 = getInt(pos + 1);
		int val = ((val1 & 0xff) << 8) + (val2 & 0xff);
		return val;
	}

	public int byte2Int(byte[] data, int pos) {
		int val = data[pos];
		val = val & 0xff;
		return val;
	}

	public int byte2Int2(byte[] data, int pos) {
		int val1 = byte2Int(data, pos);
		int val2 = byte2Int(data, pos + 1);
		int val = ((val1 & 0xff) << 8) + (val2 & 0xff);
		return val;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}

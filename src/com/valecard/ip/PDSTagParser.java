package com.valecard.ip;

import java.util.ArrayList;
import java.util.List;

public class PDSTagParser {
	public PDSTagParser() {
		// Nothing to do
	}
	
	public List<PDSTag> parse(String data) {
		List<PDSTag> list = new ArrayList<PDSTag>();
		
		int dataLen = data.length();
		int index = 0;
		byte[] dataBytes = data.getBytes();
		while (index < dataLen) {
			PDSTag tag = new PDSTag();
			tag.tag = new String(dataBytes, index, 4); index += 4;
			tag.len = Integer.parseInt(new String(dataBytes, index, 3), 10); index += 3;
			tag.value = new String(dataBytes, index, tag.len); index += tag.len;
			list.add(tag);
		}
		
		return list;
	}
}

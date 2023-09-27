package com.valecard.ip;

import java.io.InputStream;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;

public class ISO8583MessageMap {

	public static final int ASCII_MAP = 0;
	public static final int EBCDIC_MAP = 1;
	
	protected static String asciiMap = "/com/valecard/ip/mastercardClearingPkgAscii.xml";
	protected static String ebcdicMap = "/com/valecard/ip/mastercardClearingPkgEbcdic.xml";
	
	protected static ISO8583MessageMap map = null;
	protected ISO8583MessageMap() {
	}
	
	String isoMapFilename;
	public static ISO8583MessageMap getISO8583MessageMap(int mapType) {
		if (map == null) {
			map = new ISO8583MessageMap();
			map.setMap(mapType == ASCII_MAP ? asciiMap : ebcdicMap);
		}
		
		return map;
	}
		
	public InputStream getMap() {
		return this.getClass().getResourceAsStream(isoMapFilename);
	}
	
	public void setMap(String isoMapFilename) {
		this.isoMapFilename = isoMapFilename;
	}
	
	ISOPackager packager = null; 
	public ISOPackager getPackager() throws ISOException {
		if (packager == null)
			packager = new GenericPackager(getMap());
		
		return packager;
	}
}

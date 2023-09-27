package com.valecard.ip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class BinList {

	public class BinInfo {
		public String bin;
		public String countryCode;
		public String scheme;
		public String type;
		public String countryNumber;
		public String countryName;
		public String bankName;
		boolean binLookupResult;
	};
	
	HashMap<String, BinInfo> binInfoMap = new HashMap<String, BinInfo>();
	
	public BinList() {
	}
	
	protected void loadBinListMap() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("binlist.txt")));
		String line;
		while ((line = reader.readLine()) != null) {
			String [] tokens = line.split(",");
			BinInfo info = new BinInfo();
			info.bin = tokens[0];
			info.countryCode = tokens[1];
			info.binLookupResult = false;
			binInfoMap.put(tokens[0], info);
			
		}
		reader.close();
	}
	
	protected String getValueFromKey(JSONObject jsonObject, String key) throws Exception {
		return jsonObject.has(key) ? jsonObject.getString(key) : "NAO FORNECIDO";
	}
	
	protected void delay(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void processMap() throws Exception {
		StringBuilder urlString = new StringBuilder();
		
		boolean done = false;
		int iteration = 1;
		int countResponseOK = 0, countResponseNOK = 0;
		while (!done) {
			System.out.println("Preparando Iteração #" + iteration);
			System.out.println("Numero de Response  OK #" + countResponseOK);
			System.out.println("Numero de Response NOK #" + countResponseNOK);
			System.out.println("-----------------------------------------------------");
			countResponseNOK = 0;
			delay(5000);
			int lookupCount = 0;
			Iterator<String> iterator = binInfoMap.keySet().iterator();
			while (iterator.hasNext()) {
				String bin = iterator.next();
				BinInfo info = binInfoMap.get(bin);
				
				if (info.binLookupResult == false) {
					lookupCount++;
					urlString.delete(0, urlString.length());
					urlString.append("https://lookup.binlist.net/");
					urlString.append(bin);
					
					URL url;
					HttpsURLConnection con;
					url = new URL(urlString.toString());
					con = (HttpsURLConnection)url.openConnection();
					con.setRequestProperty("Accept-version", "3");
					
					int responseCode = con.getResponseCode();
					System.out.println("Response Code : " + responseCode);
		
					if (responseCode == 200) {
						countResponseOK++;
						BufferedReader in = new BufferedReader(
						        new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
			
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						System.out.println(response.toString());
						JSONObject root = new JSONObject(response.toString());
						info.scheme = getValueFromKey(root, "scheme");
						info.type = getValueFromKey(root, "type");
						
						JSONObject country = root.getJSONObject("country");
						info.countryNumber = getValueFromKey(country, "numeric");
						info.countryName = getValueFromKey(country, "name");
						
						if (root.has("bank")) {
							JSONObject bank = root.getJSONObject("bank");
							info.bankName = getValueFromKey(bank, "name");
						}
						else {
							info.bankName = "NAO FORNECIDO";
						}
						info.binLookupResult = true;
					}
					else {
						countResponseNOK++;
						info.binLookupResult = false;
					}
				}
			}
			if (lookupCount == 0) done = true;
			iteration++;
		}
		System.out.println("FIM!!!");
		System.out.println("Por favor, verifique o arquivo binresult.txt");
	}
	
	protected void generateResultFile() throws IOException {
		PrintStream out = new PrintStream(new File("binresult.txt"));
		Iterator<String> iterator = binInfoMap.keySet().iterator();
		StringBuilder builder = new StringBuilder();
		builder.append("Bin|ContryCode|Scheme|Type|CountryNumber|CountryName|BankName\n");
		while (iterator.hasNext()) {
			String bin = iterator.next();
			BinInfo info = binInfoMap.get(bin);
			
			builder.append(info.bin)
			.append("|").append(info.countryCode)
			.append("|").append(info.scheme)
			.append("|").append(info.type)
			.append("|").append(info.countryNumber)
			.append("|").append(info.countryName)
			.append("|").append(info.bankName)
			.append("\n");	
		}
		out.print(builder.toString());
		out.close();
	}
	
	public void process() throws Exception {
		loadBinListMap();
		processMap();
		generateResultFile();
	}
	
	public static void main(String[] args) throws Exception {
		BinList binList = new BinList();
		binList.process();
		
	}

}

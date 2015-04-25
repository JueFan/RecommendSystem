package org.juefan.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.juefan.IO.FileIO;

public class RatingData {
	
	public int userID;
	public int movieID;
	public int rate;
	public int data;
	
	
	public RatingData(String daString){
		String[] strings = daString.split("::");
		if(strings.length != 4)
			System.err.println("The Data's Not Comfort");
		userID = Integer.parseInt(strings[0]);
		movieID = Integer.parseInt(strings[1]);
		rate = Integer.parseInt(strings[2]);
		data = Integer.parseInt(strings[3]);	
	}
	
	public static void main(String[] args) {
		Map<Integer, List<RatingData>> map = new HashMap<Integer, List<RatingData>>();
		FileIO fileIO = new FileIO();
		fileIO.SetfileName(System.getProperty("user.dir") + "\\data\\input\\ml-1m\\ratings.dat");
		System.out.println();
		fileIO.FileRead();
		List<String> list = fileIO.cloneList();
		for(String s:list){
			RatingData data = new RatingData(s);
			if(map.containsKey(data.userID)){
				map.get(data.userID).add(data);
			}else {
				List<RatingData> tDatas = new ArrayList<RatingData>();
				tDatas.add(data);
				map.put(data.userID, tDatas);
			}
		}
		System.out.println(map.size());
	}

}

package org.juefan.alg.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.juefan.IO.FileIO;
import org.juefan.alg.UserCF;
import org.juefan.data.RatingData;
import org.juefan.eva.Evaluation;

public class TestUserCF {

	public int userID;
	public Set<Integer> TrainID = new HashSet<Integer>();
	public Set<Integer> TestID = new HashSet<Integer>();

	public static Map<Integer, Integer> map = new HashMap<Integer, Integer>();

	public static Map<Integer, Set<Integer>> ItemUserMap = new HashMap<Integer, Set<Integer>>();

	public static void main(String[] args) {
		Map<Integer,TestUserCF> map = new HashMap<Integer, TestUserCF>();
		Map<Integer, Set<Integer>> userMap = new HashMap<Integer, Set<Integer>>();
		FileIO fileIO = new FileIO();
		fileIO.SetfileName(System.getProperty("user.dir") + "\\data\\input\\ml-1m\\ratings.dat");
		fileIO.FileRead();
		List<String> list = fileIO.cloneList();
		for(String s:list){
			RatingData data = new RatingData(s);

			float rand = (float) Math.random();
			if(rand >= (float)7/8){

				if(TestUserCF.map.containsKey(data.movieID)){
					TestUserCF.ItemUserMap.get(data.movieID).add(data.userID);
					TestUserCF.map.put(data.movieID, TestUserCF.map.get(data.movieID) + 1);
				}
				else {
					Set<Integer> set = new HashSet<Integer>();
					set.add(data.userID);
					TestUserCF.ItemUserMap.put(data.movieID, set);
					TestUserCF.map.put(data.movieID, 1);
				}

				if(map.containsKey(data.userID)){
					map.get(data.userID).TestID.add(data.movieID);
				}else {
					TestUserCF userCF = new TestUserCF();
					userCF.TestID.add(data.movieID);
					map.put(data.userID, userCF);
				}
			}else if(rand >= (float)0/8){

				if(TestUserCF.map.containsKey(data.movieID)){
					TestUserCF.ItemUserMap.get(data.movieID).add(data.userID);
					TestUserCF.map.put(data.movieID, TestUserCF.map.get(data.movieID) + 1);
				}
				else {
					Set<Integer> set = new HashSet<Integer>();
					set.add(data.userID);
					TestUserCF.ItemUserMap.put(data.movieID, set);
					TestUserCF.map.put(data.movieID, 1);
				}

				if(map.containsKey(data.userID)){
					map.get(data.userID).TrainID.add(data.movieID);
				}else {
					TestUserCF userCF = new TestUserCF();
					userCF.TrainID.add(data.movieID);
					map.put(data.userID, userCF);
				}
			}
		}

		for(Integer key: map.keySet())
			userMap.put(key, map.get(key).TrainID);

		List<Integer> rand = new ArrayList<Integer>();
		Map<Integer, Float> hotMap = new HashMap<Integer, Float>();
		for(Integer item: TestUserCF.ItemUserMap.keySet()){
			hotMap.put(item, (float) TestUserCF.ItemUserMap.get(item).size());
			rand.add(item);
		}


		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm");//设置日期格式
		String dataString = "\\data\\output\\Result\\" + df.format(new Date()) + "_result.txt";
		UserCF userCF = new UserCF();
		userCF.K = 40;
		int TRAC = 0;
		for(int k = 0; k < 1; k++){
			userCF.K = userCF.K * 2;
			userCF.SysK = 10;
			Evaluation.ReSet();
			//userCF.getResysList(map.get(6038).TrainID, userMap);
			for(TestUserCF testUserCF: map.values()){
				//Set<Integer> randSet = new HashSet<Integer>();
				//for(int i = 0; i < userCF.SysK; i++)
				//randSet.add(rand.get((int)Math.floor(Math.random() * rand.size())));
				//Evaluation.setEvaluation(testUserCF.TestID, userCF.getSysK(testUserCF.TrainID, hotMap));
				//Evaluation.setEvaluation(testUserCF.TestID, randSet);
				Evaluation.setEvaluation(testUserCF.TestID, userCF.getResysList(testUserCF.TrainID, userMap));
				//System.out.println("测试集: " + testUserCF.TestID);
				if(TRAC++ % 100 == 0)
					System.out.println("准确率 = " + Evaluation.getPrecision() * 100 + "%\t\t召回率 = " + Evaluation.getRecall() * 100 + "%");
			}
			System.out.println("准确率 = " + Evaluation.getPrecision() * 100 + "%\t\t召回率 = " + Evaluation.getRecall() * 100 + "%");
			FileIO.FileWrite(System.getProperty("user.dir") + dataString, "===================使用算法 : " + userCF.toString()
					+ "=====================\n具体参数: "
					+"\nK = " + userCF.K
					+ "\nSysK = " + userCF.SysK
					+ "\n准确率 = " + Evaluation.getPrecision() * 100 + "%\n召回率 = " + Evaluation.getRecall() * 100 + "%\n", true);
		}

	}

}

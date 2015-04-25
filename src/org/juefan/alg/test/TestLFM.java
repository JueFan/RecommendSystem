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
import org.juefan.alg.LFM;
import org.juefan.data.RatingData;
import org.juefan.eva.Evaluation;

public class TestLFM {

	public static Set<Integer> user = new HashSet<Integer>();
	public static Set<Integer> item = new HashSet<Integer>();
	public static List<Integer> itemList = new ArrayList<Integer>();
	public static Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> randMap = new HashMap<Integer, Integer>();	//倾向选择热门且用户未评价的为负例

	/**用户项目训练数据*/
	public static Map<Integer, Map<Integer, Float>>  UserItemTrain = new HashMap<Integer, Map<Integer, Float>> ();
	/**用户项目测试数据*/
	public static Map<Integer, Map<Integer, Float>>  UserItemTest = new HashMap<Integer, Map<Integer, Float>> ();
	
	public static LFM lfm = new LFM();
		
	public static Map<Integer, Float> getFu(Map<Integer, Float> item){
		Map<Integer, Float> map = new HashMap<Integer, Float>();	
		while(map.size() < item.size()*4 && item.size() + map.size() < TestLFM.item.size() * 0.8){
			/**抑制热门方式*/
			/*int rand = (int) (Math.random() * randMap.size());
			if(!item.containsKey(randMap.get(rand))){
				map.put(randMap.get(rand), (float) 0);
			}*/
			/**同等对待方式*/
			int rand = (int) (Math.random() *  TestLFM.itemList.size());
			if(!item.containsKey( TestLFM.itemList.get(rand))){
				map.put( TestLFM.itemList.get(rand), (float) 0);
			}
		}
		return map;
	}

	/**将Map的key加载进Set类*/
	public static Set<Integer> MapToSet(Map<Integer, Float> item){
		Set<Integer> tSet = new HashSet<Integer>();
		for(int k: item.keySet())
			tSet.add(k);
		return tSet;
	}

	/**
	 * 测试入口
	 */
	public static void main(String[] args) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");  
		FileIO fileIO = new FileIO();
		fileIO.SetfileName(System.getProperty("user.dir") + "\\data\\input\\ml-1m\\ratings.dat");
		fileIO.FileRead();
		List<String> list = fileIO.cloneList();
		int num = 0;
		for(String s:list){
			RatingData data = new RatingData(s);
			float rand = (float) Math.random();
			if(rand >= (float)1/8){	//将数据随机分成训练数据和测试数据
				if(UserItemTrain.containsKey(data.userID)){
					UserItemTrain.get(data.userID).put(data.movieID, (float) 1);
				}else {
					Map<Integer, Float> tMap = new HashMap<Integer, Float>();
					tMap.put(data.movieID, (float) 1);
					UserItemTrain.put(data.userID, tMap);
				}
				//计算每个项目的热度
				if(map.containsKey(data.movieID)){
					map.put(data.movieID, map.get(data.movieID) + 1);
				}else {
					map.put(data.movieID, 1);
				}
				//构造项目分布映射
				randMap.put(num++, data.movieID);
				//收集用户列表和项目列表
				user.add(data.userID);
				item.add(data.movieID);
			}else {
				if(UserItemTest.containsKey(data.userID)){
					UserItemTest.get(data.userID).put(data.movieID, (float) 1);
				}else {
					Map<Integer, Float> tMap = new HashMap<Integer, Float>();
					tMap.put(data.movieID, (float) 1);
					UserItemTest.put(data.userID, tMap);
				}
			}		
		}
		

		System.out.println("正在构造罗盘赌");
		for(Integer item: TestLFM.item){
			itemList.add(item);
		}
		int Fu = 0;
		for(int user: UserItemTrain.keySet()){
			UserItemTrain.get(user).putAll(getFu(UserItemTrain.get(user)));
			if(++Fu % 1000 == 0)
				System.out.println("已构造 " + Fu +" 个负样本用户数据");
		}
		System.out.println("负样本生成完毕");

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm");//设置日期格式
		String dataString = "\\data\\output\\Result\\" + df.format(new Date()) + "_result.txt";
		LFM lfm = new LFM(user, item);
		for(int trac = 0; trac <= 20; trac++){
			LFM.LatentFactorModel(UserItemTrain);
			for(int user:UserItemTrain.keySet()){
				if(UserItemTest.containsKey(user)){
					Evaluation.setEvaluation(MapToSet(UserItemTest.get(user)), lfm.getResysList(user, UserItemTrain.get(user)));
				}
			}
			System.out.println("准确率 = " + Evaluation.getPrecision() * 100 + "%\t\t召回率 = " + Evaluation.getRecall() * 100 + "%\t\t覆盖率 = " + Evaluation.getCoverage()/item.size() * 100 + "%");
			FileIO.FileWrite(System.getProperty("user.dir") + dataString, "===================使用算法 : " + lfm.toString()
					+ "=====================\n具体参数: "			
					+ "\nlatent = " + LFM.latent
					+"\nalpha = " + LFM.alpha
					+"\nlambda = " + LFM.lambda
					+ "\n准确率 = " + Evaluation.getPrecision() * 100 + "%\t\t召回率 = " + Evaluation.getRecall() * 100 + "%\t\t覆盖率 = " + Evaluation.getCoverage()/item.size() * 100 + "%\n", true);
		}
	}

}

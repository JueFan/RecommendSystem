package org.juefan.alg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LFM {
	
	public static final int latent = 100;
	public static double alpha = 0.03;
	public static double lambda = 0.01;
	public static final int iteration = 1;
	public static final   int resys = 10;
	
	public static Map<Integer, List<Float>> UserMap = new HashMap<Integer, List<Float>>();
	public static Map<Integer, List<Float>> ItemMap = new HashMap<Integer, List<Float>>();
		
	public static compares compare = new compares();
	
	public  class State{
		public int TemID;
		public Set<Integer> set = new HashSet<Integer>();
		public float sim;

		/**用户集排序*/
		public State(Set<Integer> s, float s2){
			set.addAll(s);
			sim = s2;
		}

		/**Item排序*/
		public State(Integer i, float s){
			TemID = i;
			sim = s;
		}
	}

	public static class compares implements Comparator<Object>{
		@Override
		public int compare(Object o1, Object o2) {			
			State s1 = (State)o1;
			State s2 = (State)o2;
			return s1.sim < s2.sim ? 1:-1;
		}		
	}
	
	public  String toString(){
		return "LFM";
	}
	/**
	 * 加载用户与项目的集合并初始化隐含矩阵
	 * 注意隐含层的数值不能太大，建议在0.05左右
	 * @param user
	 * @param item
	 */
	public LFM(Set<Integer> user, Set<Integer> item){
		for(Integer u:user){
			List<Float> tList = new ArrayList<Float>();
			for(int i = 0; i < latent; i++)
				tList.add((float) ((float) Math.random() * 0.1));
			UserMap.put(u, tList);
		}
		for(Integer u:item){
			List<Float> tList = new ArrayList<Float>();
			for(int i = 0; i < latent; i++)
				tList.add((float) ((float) Math.random() *0.1));
			ItemMap.put(u, tList);
		}
	}
	public LFM() {
		// TODO Auto-generated constructor stub
	}
	/**
	 *  计算用户对某个物品的兴趣
	 * @param uLV	用户与隐含类的关系
	 * @param iLV	隐含类与物品的关系
	 * @return 返回用户对某个物品的兴趣
	 */
	public static float getPreference(List<Float> uLV, List<Float> iLV){
		float p = 0;
		for(int i = 0; i < latent; i++){
			p = p + uLV.get(i) * iLV.get(i);
		}
		return p;
	}
	
	/**预测评分差*/
	public static float Predict(float i1, float i2){
		return i1 - i2;
	}
	
	/**
	 * 迭代求解隐含层
	 * @param UserItem	
	 */
	public static void LatentFactorModel(Map<Integer, Map<Integer, Float>> UserItem){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm");//设置日期格式
		for(int i = 0; i < iteration; i++){	
			System.out.println( df.format(new Date()) + "\t第 " + (i + 1) + " 次迭代");
			for(int user: UserItem.keySet()){
				for(int item: UserItem.get(user).keySet()){
					float error = Predict(UserItem.get(user).get(item), 
							getPreference(UserMap.get(user), ItemMap.get(item)));
					for(int i1 = 0; i1 < latent; i1++){
						UserMap.get(user).set(i1, (float) (UserMap.get(user).get(i1) + alpha * 
								(ItemMap.get(item).get(i1) * error - lambda * UserMap.get(user).get(i1))));
						if (Float.isNaN(UserMap.get(user).get(i1) )) {
							System.out.println("真的能出现:" + UserMap.get(user));
						}
						ItemMap.get(item).set(i1, (float) (ItemMap.get(item).get(i1) + alpha * 
								(UserMap.get(user).get(i1) * error - lambda * ItemMap.get(item).get(i1))));
					}
				}
			}
			alpha = (float) (alpha * 0.9);
		}	
	}
	
	/**
	 * 获取用户的最终推荐列表
	 * @param map 项目的得分值表
	 * @return
	 */
	public Set<Integer> getResysK(Map<Integer, Float> map){
		List<State> tList = new ArrayList<State>();
		Set<Integer> set = new HashSet<Integer>();
		for(Integer key: map.keySet()){
			tList.add(new State(key,  map.get(key)));		
		}
		Collections.sort(tList, compare);
		for(int i = 0; i < tList.size() && i < resys; i++){
			set.add(tList.get(i).TemID);	
			//System.out.print(tList.get(i).TemID + ":" + tList.get(i).sim + "\t");
		}
		//System.out.println();
		return set;
	}
	
	/**
	 *  计算用户的推荐列表
	 * @param user	用户的ID
	 * @param item	用户的训练集
	 * @return	用户的推荐列表
	 */
	public Set<Integer> getResysList(int user, Map<Integer, Float> item){
		Map<Integer, Float> map = new HashMap<Integer, Float>();
		for(int i: ItemMap.keySet()){
			if(!item.containsKey(i))
				map.put(i, getPreference(UserMap.get(user), ItemMap.get(i)));
		}
		//System.out.println(user + "\t" + UserMap.get(user));
		return getResysK(map);
	}
}

package org.juefan.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.juefan.alg.test.TestUserCF;


public class UserCF {

	public int K = 0;
	public int SysK = 5;
	public static compares compare = new compares();

	public String toString(){
		return "UserCF";
	}

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
			return s1.sim < s2.sim ? 1:0;
		}		
	}

	/**
	 * 获取用户的最终推荐列表
	 * @param map 项目的得分值表
	 * @return
	 */
	public Set<Integer> getSysK(Map<Integer, Float> map, Map<Integer, Integer> itemMap){
		List<State> tList = new ArrayList<UserCF.State>();
		Set<Integer> set = new HashSet<Integer>();
		for(Integer key: map.keySet())
			tList.add(new State(key,  map.get(key)/*/itemMap.get(key)*/));
		Collections.sort(tList, compare);

		for(int i = 0; i < tList.size() && i < SysK; i++){
				//System.out.print("[ " +  tList.get(i).TemID + "\t" + tList.get(i).sim + " ]\t");
			set.add(tList.get(i).TemID);	
		}
		//System.out.println();
		return set;
	}

	/**
	 * 获取最热门的推荐列表
	 * @param map 项目的热度值表
	 * @return
	 */
	public Set<Integer> getSysK(Set<Integer> set, Map<Integer, Float> map){
		List<State> tList = new ArrayList<UserCF.State>();
		Set<Integer> set1 = new HashSet<Integer>();
		for(Integer key: map.keySet())
			if(!set.contains(key))
				tList.add(new State(key,  map.get(key)));
		Collections.sort(tList, compare);

		for(int i = 0; i < tList.size() && i < SysK; i++){
			//	System.out.println(tList.get(i).TemID + "\t" + tList.get(i).sim);
			set1.add(tList.get(i).TemID);	
		}
		return set1;
	}

	/**
	 * 用户的Jaccard相似度
	 * @param set1	原用户
	 * @param set2	待判断相似度用户
	 * @return
	 */
	public float Jaccard(Set<Integer> set1, Set<Integer> set2){
		int mix = 0;
		if(set1.size() < set2.size())
			for(int s:set1){
				if(set2.contains(s))
					mix++;
			}
		else {
			for(int s: set2)
				if(set1.contains(s))
					mix++;
		}
		return (float)mix/(set1.size() + set2.size() - mix);
	}

	/**
	 * 用户的Simirary相似度
	 * @param set1	原用户
	 * @param set2	待判断相似度用户
	 * @return
	 */
	public float Simirary(Set<Integer> set1, Set<Integer> set2){
		int mix = 0;
		if(set1.size() < set2.size())
			for(int s:set1){
				if(set2.contains(s))
					mix++;
			}
		else {
			for(int s: set2)
				if(set1.contains(s))
					mix++;
		}
		return (float) ((float)mix/Math.sqrt((set1.size() * set2.size())));
	}

	/**
	 * 获取用户的推荐列表
	 * @param user	原用户的浏览项目 
	 * @param userMap	所有用户的浏览项目
	 * @return
	 */
	public Set<Integer> getResysList(Set<Integer> user, Map<Integer, Set<Integer>> userMap){
		List<State> states = new ArrayList<UserCF.State>();
		List<State> stateList = new ArrayList<UserCF.State>();
		Map<Integer, Float> map = new HashMap<Integer, Float>();
		Set<Integer> userSet = new HashSet<Integer>();
		/**item在最近邻用户中出现的频数*/
		Map<Integer, Integer> itemMap = new HashMap<Integer, Integer>();

		for(Integer item: user)
			userSet.addAll(TestUserCF.ItemUserMap.get(item));

		for(Integer u: userSet){
			states.add(new State(userMap.get(u), Simirary(user, userMap.get(u))));
		}

		//System.out.println("原始用户: " + user);
		stateList = getKUserList(states);

		for(State state: stateList){
			for(Integer i: state.set){
				if(!user.contains(i)){
					if(map.containsKey(i)){
						itemMap.put(i, itemMap.get(i) + 1);
						map.put(i, map.get(i) + state.sim);
					}else {
						itemMap.put(i, 1);
						map.put(i, state.sim);
					}
				}
			}
		}
		//System.out.println(itemMap);
		return getSysK(map, itemMap);
	}

	/**
	 * 获取最近邻相似用户列表
	 * @param list	用户列表
	 * @return
	 */
	public List<State> getKUserList(List<State> list){
		List<State> list2 = new ArrayList<UserCF.State>();	
		Collections.sort(list, compare);
		for(int i = 1; i <= K && i <= list.size(); i++){
			//System.out.println(list.get(i).sim + "\t" + list.get(i).set);
			list2.add(list.get(i));
		}
		return list2;
	}

}

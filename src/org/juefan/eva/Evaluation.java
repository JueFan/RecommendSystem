package org.juefan.eva;

import java.util.HashSet;
import java.util.Set;

public class Evaluation {

	public static int Ru = 0;
	public static int Tu = 0;
	public static int Mix = 0;
	public static float Recall = 0;
	public static float Precision = 0;
	public static float Coverage = 0;
	public static  Set<Integer> coverSet = new HashSet<Integer>();

	public static void ReSet(){
		Ru = 0;
		Tu = 0;
		Mix = 0;
		Recall = 0;
		Precision = 0;
		Coverage = 0;
	}

	public static void setEvaluation(Set<Integer> user, Set<Integer> resys){
		coverSet.addAll(resys);
		if(user.size() < resys.size())
			Mix = Mix + getMix(user, resys);
		else {
			Mix = Mix + getMix(resys, user);
		}
		Ru = Ru + resys.size();
		Tu = Tu + user.size();
	}

	public static int getMix(Set<Integer> set1, Set<Integer> set2){
		int mix = 0;
		for(Integer s:set1)
			if(set2.contains(s))
				mix++;
		return mix;
	}

	public static float getRecall(){
		return (float)Mix/Tu;
	}

	public static float getPrecision(){
		return (float)Mix/Ru;
	}

	public static float getCoverage(){
		return (float) coverSet.size();
	}
}

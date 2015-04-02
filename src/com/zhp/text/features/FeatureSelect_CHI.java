package com.zhp.text.features;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zhp.text.model.FeatureStatsInfo;

/**
 *  Feature selection class
 * 
 */
public class FeatureSelect_CHI {
	private FeatureStatsInfo stats;
	// 按CHI值从大到小的顺序保存选择的特征
	private List<String> featureList = new LinkedList<>();
	// 保存各特征对应的CHI值
	private Map<String, Double> featureCHIMap = new HashMap<String, Double>();
	
	/**
	 * 构造函数
	 * 
	 * @param stats 语料统计信息
	 * 
	 * @return None
	 */
	public FeatureSelect_CHI(FeatureStatsInfo stats) {
		this.stats = stats;
	}
	
    /**
     * select feature
     * 
     * @param retainPercent
     * 
     * @return selected features and their CHI value
     */
	public Map<String, Double> selectFeatures(double retainPercent) {
		String feature;
		Double previusCHIValue;
		Double maxCHIValue = 0.0;
		Double tmpCHIValue;
		String classID;
		Map<String, Integer> classIDArtiCountOfWord;
		double maxValueOfCHI = Double.MIN_VALUE;
		double minValueOfCHI = Double.MAX_VALUE;
		int totalFeatureCount = stats.getWordInfo().size();
		int currFeatureCount = 0;
		int criticalCount = (int) (totalFeatureCount * retainPercent);
		for(Map.Entry<String, Map<String, Integer>> entry1: stats.getWordInfo().entrySet()) {
			feature = entry1.getKey();
			// 统计已处理的数量
			if(currFeatureCount % 10000 == 0) {
				System.out.println("已处理数量:" + currFeatureCount);
			}
			++currFeatureCount;
			
			classIDArtiCountOfWord = entry1.getValue();
			previusCHIValue = featureCHIMap.get(feature);
			if(previusCHIValue == null) {
				previusCHIValue = 0.0;
			}
			for(Map.Entry<String, Integer> entry2: classIDArtiCountOfWord.entrySet()) {
				classID = entry2.getKey();
				tmpCHIValue = calcCHIOfWord(feature, classID);
				if(maxCHIValue < tmpCHIValue) {
					maxCHIValue = tmpCHIValue;
				}
			}
			if(previusCHIValue > maxCHIValue) {
				maxCHIValue = previusCHIValue;
			}

			insertListByOrder(criticalCount, feature, maxCHIValue);
			
			if(maxValueOfCHI < maxCHIValue) {
				maxValueOfCHI = maxCHIValue;
			}
			if(minValueOfCHI > maxCHIValue) {
				minValueOfCHI = maxCHIValue;
			}
		}
		
		System.out.println("最大CHI值:" + maxValueOfCHI);
		System.out.println("最小CHI值:" + minValueOfCHI);
		
		return featureCHIMap;
	}
	
    /**
     * use binary search method to find inserting index, and then insert the feature in the position
     * 
     * @param criticalCount
     * @param feature
     * @return void
     */
	private void insertListByOrder(int criticalCount, String feature, double CHIValue) {
		int listLen = featureList.size();
		if(listLen <= 0) {
			featureCHIMap.put(feature, CHIValue);
			featureList.add(feature);
			return;
		}
		int insertIndex = -1;
		double header_CHI = featureCHIMap.get(featureList.get(0));
		double tail_CHI = featureCHIMap.get(featureList.get(listLen - 1));
		if(listLen >= criticalCount) {
			if(CHIValue > tail_CHI) {
				String tmpFeature = featureList.get(listLen - 1);
				featureCHIMap.remove(tmpFeature);
				featureList.remove(listLen - 1);
			} else {
				return;
			}
		}
		if(CHIValue > header_CHI) {
			featureCHIMap.put(feature, CHIValue);
			featureList.add(0, feature);
			return;
		}
		if(CHIValue < tail_CHI) {
			featureCHIMap.put(feature, CHIValue);
			featureList.add(listLen, feature);
			return;
		}
		
		insertIndex = insertIndex(0, listLen - 1, feature, CHIValue);
		
		featureCHIMap.put(feature, CHIValue);
		featureList.add(insertIndex, feature);
	}
	
    /**
     * use recursion to find the insertion position
     * 
     * @param header 
     * @param tail
     * @param feature
     * @return 
     */
	private int insertIndex(int header, int tail, String feature, double CHIValue) {
		int middle = (header + tail) / 2;
		double middle_CHI = featureCHIMap.get(featureList.get(middle));
		if(middle == header) {
			return header + 1;
		}
		if(CHIValue > middle_CHI) {
			return insertIndex(header, middle, feature, CHIValue);
		} else {
			return insertIndex(middle, tail, feature, CHIValue);
		}
	}
	
	/**
	 * 特征和类别的CHI值
	 * 
	 * @return CHI值
	 */
	public double calcCHIOfWord(String feature, String classid) {
		// CHI值
		double wordCHIValue = 0.0;
		// N表示语料中的文档总数
		double N = stats.getArtiCount();
		// A表示包含特征项w且属于类别c 的文档频数
		double A = 0.0;
		// B表示包含特征项w但不属于类别c 的文档频数
		double B = 0.0;
		// C表示属于类别c 但不包含特征项w的文档频数
		double C = 0.0;
		// D表示既不属于c 也不包含特征项w的文档频数
		double D = 0.0;
		
		Map<String, Integer> classIDArtiCountOfWord = stats.getWordInfo().get(feature);
		Integer classIDArtiCount = classIDArtiCountOfWord.get(classid);
		if(classIDArtiCount != null) {
			A = classIDArtiCount;
		}
		
		Integer wordArtiCount = stats.getArtiCountOfWord().get(feature);
		B = wordArtiCount - A;
		
		Integer artiCountOfClassid = stats.getArtiCountOfClassid().get(classid);
		Map<String, Integer> tmpArtiCoutnOfWord = stats.getClassIDInfo().get(classid);
		Integer artiCoutnOfWord = tmpArtiCoutnOfWord.get(feature);
		if(artiCoutnOfWord != null) {
			C = artiCountOfClassid - artiCoutnOfWord;
		} else {
			C = artiCountOfClassid;
		}
		
		D = N - artiCountOfClassid - wordArtiCount + A;

		if((A * D - B * C) <= 0) {
			return 0.0;
		}
		// 频度
		double FI = getFI(feature, classid);
		// 集中度
		double CI = getCI(feature, classid);
		// 分散度
		double DI = getDI(feature, classid);
		
		wordCHIValue = FI * CI * DI * N * Math.pow((A * D - B * C), 2) / ((A + C) * (B + D) * (A + B) * (C + D));
		
		return wordCHIValue;
	}
	
	/**
	 * 频度
	 * 
	 * @param wordName
	 * @param classId
	 * 
	 * @return 频度
	 */
	public double getFI(String feature, String classId) {
		double FI = 0.0;
		Integer wordCountFreq = 0;
		Map<String, Integer> tmpWordCountFreq = stats.getWordCountFreqOfClassid().get(classId);
		if(tmpWordCountFreq != null) {
			wordCountFreq = tmpWordCountFreq.get(feature);
			if(wordCountFreq == null) {
				return 0.0;
			}
		}
		Integer artiCountOfClassid = stats.getArtiCountOfClassid().get(classId);
		if(artiCountOfClassid != null) {
			FI = wordCountFreq * 1.0 /  artiCountOfClassid;
		}
		
		return FI;
	}
	
	/**
	 * 集中度
	 * 
	 * @param wordName
	 * @param classId
	 * 
	 * @return 集中度
	 */
	public double getCI(String feature, String classId) {
		double CI = 0.0;
		// 类中含特征的文档数
		Integer wordArtiCount = 0;
		Map<String, Integer> tmpWordArtiCount = stats.getClassIDInfo().get(classId);
		if(tmpWordArtiCount != null) {
			wordArtiCount = tmpWordArtiCount.get(classId);
			if(wordArtiCount == null) {
				return 0.0;
			}
		}
		// 包含特征的总文档数
		Integer artiCountOfWord = stats.getArtiCountOfWord().get(feature);
		if(artiCountOfWord != null) {
			CI = wordArtiCount * 1.0 / artiCountOfWord;
		}
		
		return CI;
	}
	
	/**
	 * 分散度
	 * 
	 * @param wordName
	 * @param classId
	 * 
	 * @return 分散度
	 */
	public double getDI(String feature, String classId) {
		double DI = 0.0;
		// 类中含特征的文档数
		Integer wordArtiCount = 0;
		Map<String, Integer> tmpWordArtiCount = stats.getClassIDInfo().get(classId);
		if(tmpWordArtiCount != null) {
			wordArtiCount = tmpWordArtiCount.get(feature);
			if(wordArtiCount == null) {
				return 0.0;
			}
		}
		// 属于类的总文档数
		Integer wordCountOfClassid = stats.getArtiCountOfClassid().get(classId);
		if(wordCountOfClassid != null) {
			DI = wordArtiCount * 1.0 / wordCountOfClassid;
		}
		return DI;
	}
}

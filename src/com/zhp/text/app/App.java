package com.zhp.text.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.zhp.text.features.FeatureSelect_CHI;
import com.zhp.text.model.FeatureStatsInfo;

/**
 *  Test class
 * 
 */
public class App {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		FeatureStatsInfo stats = FeatureStatsInfo.getFeatureStatsInfo("D:/sensiword/稿件数据/分类语料/Bayes/corpus.txt");
		FeatureSelect_CHI chi = new FeatureSelect_CHI(stats);
		Map<String, Double> features = chi.selectFeatures(0.2);
		System.out.println(features.size());
	}
}

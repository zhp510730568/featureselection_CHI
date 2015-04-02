package com.zhp.text.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Feature stat class
 * 
 */
public class FeatureStatsInfo {
	// 单词的类别分布信息
	private Map<String, Map<String, Integer>> wordInfo;
	// 类别所属文章分布信息
	private Map<String, Integer> artiCountOfClassid;
	// 类别所属单词频次分布
	private Map<String, Map<String, Integer>> wordCountFreqOfClassid;
	// 类别的单词分布信息
	private Map<String, Map<String, Integer>> classIDInfo;
	// 包含单词的文章分布信息
	private Map<String, Integer> artiCountOfWord;
	// 文章总数
	private int artiCount = 0;
	
	/**
	 * 默认构造函数
	 * 
	 * @return None
	 */
	public FeatureStatsInfo() {
		
	}
	
	/**
	 * 提取语料统计信息，每个文档占一样，类别号和文档内容使用\t隔开，文档内容使用空格分开
	 * 
	 * @param corpusPath 语料路径
	 * 
	 * @return FeatureStatsInfo对象
	 * @throws UnsupportedEncodingException, FileNotFoundException, IOException
	 */
	public static FeatureStatsInfo getFeatureStatsInfo(String corpusPath) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		FeatureStatsInfo corpusWordCHIInfo = new FeatureStatsInfo();
		
		Map<String, Map<String, Integer>> wordInfo = new HashMap<String, Map<String, Integer>>();
		Map<String, Integer> artiCountOfClassid = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> wordCountFreqOfClassid = new HashMap<String, Map<String, Integer>>();
		// 类别的单词分布信息
		Map<String, Map<String, Integer>> classIDInfo = new HashMap<String, Map<String, Integer>>();
		// 包含单词的文章分布信息
		Map<String, Integer> artiCountOfWord = new HashMap<String, Integer>();
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(corpusPath)),"UTF-8"))){
			String line = "";
			String[] artiInfoArr = null;
			int artiCount = 0;
			// 保存文章包含的单词数
			int wordCount = 0;
			String[] wordArr = null;
			String classId = "";
			String wordName = "";
			Map<String, Integer> wordClassInfo = null;
			Map<String, Integer> classIDWordInfo = null;
			// 保存文档出现的单词
			Set<String> artiWordBag = new HashSet<String>();
			while((line = br.readLine()) != null) {
				artiCount++;
				artiInfoArr = line.split("\t", 2);
				if(artiInfoArr.length == 2) {
					wordArr = artiInfoArr[1].split(" ");
					classId = artiInfoArr[0];
					// 统计指定类别号的文章数量
					if(artiCountOfClassid.containsKey(classId)) {
						artiCountOfClassid.put(classId, artiCountOfClassid.get(classId) + 1);
					} else {
						artiCountOfClassid.put(classId, 1);
					}
					// 单词数
					wordCount = wordArr.length;
					// 清空文章词袋
					artiWordBag.clear();
					for(int index = 0; index < wordCount; index++) {
						wordName = wordArr[index];
						// 统计类别所属词频次信息
						if(wordCountFreqOfClassid.containsKey(classId)) {
							Map<String, Integer> tmpWordCountFreq = wordCountFreqOfClassid.get(classId);
							if(tmpWordCountFreq.containsKey(wordName)) {
								tmpWordCountFreq.put(wordName, tmpWordCountFreq.get(wordName) + 1);
							} else {
								tmpWordCountFreq.put(wordName, 1);
							}
						} else {
							Map<String, Integer> tmpWordCountFreq = new HashMap<String, Integer>();
							tmpWordCountFreq.put(wordName, 1);
							wordCountFreqOfClassid.put(classId, tmpWordCountFreq);
						}
						
						if(!artiWordBag.contains(wordName)) {
							artiWordBag.add(wordName);
						} else {
							continue;
						}
						
						if(wordInfo.containsKey(wordName)) {
							wordClassInfo = wordInfo.get(wordName);
							if(wordClassInfo.containsKey(classId)) {
								wordClassInfo.put(classId, wordClassInfo.get(classId) + 1);
							} else {
								wordClassInfo.put(classId, 1);
							}
						} else {
							wordClassInfo = new HashMap<String, Integer>();
							wordClassInfo.put(classId, 1);
							wordInfo.put(wordName, wordClassInfo);
						}
						// 统计包含单词的文章分布信息
						if(classIDInfo.containsKey(classId)) {
							classIDWordInfo = classIDInfo.get(classId);
							if(classIDWordInfo.containsKey(wordName)) {
								classIDWordInfo.put(wordName, classIDWordInfo.get(wordName) + 1);
							} else {
								classIDWordInfo.put(wordName, 1);
							}
						} else {
							classIDWordInfo = new HashMap<String, Integer>();
							classIDWordInfo.put(wordName, 1);
							classIDInfo.put(classId, classIDWordInfo);
						}
						// 包含单词的文章分布信息
						if(artiCountOfWord.containsKey(wordName)) {
							int count = artiCountOfWord.get(wordName) + 1;
							artiCountOfWord.put(wordName, count);
						} else {
							artiCountOfWord.put(wordName, 1);
						}
					}
				}
			}
			
			corpusWordCHIInfo.setArtiCount(artiCount);
			corpusWordCHIInfo.setWordInfo(wordInfo);
			corpusWordCHIInfo.setArtiCountOfClassid(artiCountOfClassid);
			corpusWordCHIInfo.setWordCountFreqOfClassid(wordCountFreqOfClassid);
			corpusWordCHIInfo.setClassIDInfo(classIDInfo);
			corpusWordCHIInfo.setArtiCountOfWord(artiCountOfWord);
			
			return corpusWordCHIInfo;
		}
	}
	
	/**
	 * 获取属性artiCount
	 * 
	 * @return 值
	 */
	public int getArtiCount() {
		return artiCount;
	}
	
	/**
	 * 设置属性artiCount
	 * 
	 * @return void
	 */
	public void setArtiCount(int artiCount) {
		this.artiCount = artiCount;
	}

	/**
	 * 获取属性wordInfo
	 * 
	 * @return 值
	 */
	public Map<String, Map<String, Integer>> getWordInfo() {
		return wordInfo;
	}
	
	/**
	 * 设置属性wordInfo
	 * 
	 * @return void
	 */
	public void setWordInfo(Map<String, Map<String, Integer>> wordInfo) {
		this.wordInfo = wordInfo;
	}
	
	/**
	 * 获取属性artiCountOfClassid
	 * 
	 * @return 值
	 */
	public Map<String, Integer> getArtiCountOfClassid() {
		return artiCountOfClassid;
	}
	
	/**
	 * 设置属性artiCountOfClassid
	 * 
	 * @return void
	 */
	public void setArtiCountOfClassid(Map<String, Integer> artiCountOfClassid) {
		this.artiCountOfClassid = artiCountOfClassid;
	}
	
	/**
	 * 获取属性wordCountFreqOfClassid
	 * 
	 * @return 值
	 */
	public Map<String, Map<String, Integer>> getWordCountFreqOfClassid() {
		return wordCountFreqOfClassid;
	}

	/**
	 * 设置属性wordCountFreqOfClassid
	 * 
	 * @return void
	 */
	public void setWordCountFreqOfClassid(Map<String, Map<String, Integer>> wordCountFreqOfClassid) {
		this.wordCountFreqOfClassid = wordCountFreqOfClassid;
	}
	
	/**
	 * 获取属性classIDInfo
	 * 
	 * @return 值
	 */
	public Map<String, Map<String, Integer>> getClassIDInfo() {
		return classIDInfo;
	}

	/**
	 * 设置属性classIDInfo
	 * 
	 * @return void
	 */
	public void setClassIDInfo(Map<String, Map<String, Integer>> classIDInfo) {
		this.classIDInfo = classIDInfo;
	}

	/**
	 * 获取属性artiCountOfWord
	 * 
	 * @return 值
	 */
	public Map<String, Integer> getArtiCountOfWord() {
		return artiCountOfWord;
	}

	/**
	 * 设置属性artiCountOfWord
	 * 
	 * @return void
	 */
	public void setArtiCountOfWord(Map<String, Integer> artiCountOfWord) {
		this.artiCountOfWord = artiCountOfWord;
	}
}

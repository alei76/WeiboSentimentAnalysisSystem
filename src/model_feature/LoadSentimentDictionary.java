package model_feature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Load Sentiment dictionaries. The dictionary format is each line contains only one word or phrase
 * @author xiaolei
 */
public class LoadSentimentDictionary {
	/**
	 * Load dictionary for given path
	 * @param path the path of dictionary file
	 * @return dictionary
	 * @throws IOException
	 */
	public static HashSet<String> loadDic(String path)throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(path));
		HashSet<String> set=new HashSet<String>();
		String line;
		while((line=reader.readLine())!=null){
			if(line.trim().length()!=0)
				set.add(line.trim());
		}
		reader.close();
		return set;
	}
	
	/**
	 * @return HowNet Negative Words
	 * @throws IOException
	 */
	public static HashSet<String> getHowNetNegativeWords() throws IOException{
		return loadDic("./resource/dic/HowNet/HowNet_Negative.txt");
	}
	
	/**
	 * @return HowNet Positive Words
	 * @throws IOException
	 */
	public static HashSet<String> getHowNetPositiveWords() throws IOException{
		return loadDic("./resource/dic/HowNet/HowNet_Negative.txt");
	}
	
	/**
	 * @return upset words
	 * @throws IOException
	 */
	public static HashSet<String> getUpsetWords() throws IOException{
		return loadDic("./resource/dic/upset.txt");
	}
	
	/**
	 * @return suicide words
	 * @throws IOException
	 */
	public static HashSet<String> getSuicideWords() throws IOException{
		return loadDic("./resource/dic/suicide.txt");
	}
	
	/**
	 * @return suicide BigramWords
	 * @throws IOException
	 */
	public static HashSet<String[]> getBigramWords() throws IOException{
		HashSet<String> set=loadDic("./resource/dic/Bigram.txt");
		HashSet<String[]> dic=new HashSet<String[]>();
		for(String str:set){
			str=str.split("\t")[1];
			String[] info=str.split(",");
			dic.add(info);
		}
		return null;
	}
	
	/**
	 * @return suicide TigramWords
	 * @throws IOException
	 */
	public static HashSet<String[]> getTigramWords() throws IOException{
		HashSet<String> set=loadDic("./resource/dic/Trigram.txt");
		HashSet<String[]> dic=new HashSet<String[]>();
		for(String str:set){
			str=str.split("\t")[1];
			String[] info=str.split(",");
			dic.add(info);
		}
		return null;
	}
}
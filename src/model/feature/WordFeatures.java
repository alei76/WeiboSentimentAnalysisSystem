package model.feature;

import io.BasicReader;
import io.GetAllWeiboPosts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Dictionary.LoadSentimentDictionary;
import model.svm.LibSvmUtils;
/**
 * Trying to use both unigram and bigram
 * @author xiaolei
 */
public class WordFeatures {
	public static List<HashSet<String>> dictionaries=new ArrayList<HashSet<String>>();
	public static HashMap<Integer,List<Double>> featureMap=new HashMap<Integer,List<Double>>();
	//load dictionaries from local files
	static{
		//load all dictionaries from ./resource/ folder
		try {
			dictionaries.add(LoadSentimentDictionary.getSuicideWords());
			dictionaries.add(LoadSentimentDictionary.getUpsetWords());
			dictionaries.add(LoadSentimentDictionary.getHowNetNegativeWords());
			dictionaries.add(LoadSentimentDictionary.getHowNetPositiveWords());
			dictionaries.add(LoadSentimentDictionary.getBigramWords());
			dictionaries.add(LoadSentimentDictionary.getTigramWords());
			
			List<String> list=BasicReader.basicRead("./resource/tempTrainData.txt");
			
			//Remove all words that does not contains in Training corpus
			for(int i=0;i<dictionaries.size();i++){
				if(i>3)
					break;
				HashSet<String> dic=dictionaries.get(i);
				List<String> d=new ArrayList<String>(dic);
				for(String word:d){
					boolean flag=true;
					for(String str:list){
						if(str.contains(word.trim())){
							flag=true;
							break;
						}
						else
							flag=false;
					}
					if(flag==false)
						dic.remove(word);
				}
				System.out.println(d.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getTrainContents(){
		List<String> contents=new ArrayList<String>();
		//load all posts' contents
		GetAllWeiboPosts all=new GetAllWeiboPosts("./resource/Segmentedall.txt");
				
		for(int i=0;i<all.getList().size();i++){
			contents.add(all.getList().get(i).getContent());
		}
		return contents;
	}
	
	/**
	 * Constructing feature matrix
	 */
	public static HashMap<Integer,List<Double>> GetFeatures(List<String> contents){		
		double ratio=10;
		double suicidePolarity=2,upsetPolarity=1.0;
		for(int i=0;i<contents.size();i++){
			//negative VS. positive ratio, negativeCount is the one to count negative word number
//			double ratio=0.0,positiveCount=0.0,negativeCount=0.0;
			
			List<Double> list=new ArrayList<Double>();
			//Set value for each dictionary
			for(int m=0;m<dictionaries.size();m++){
				HashSet<String> dic=dictionaries.get(m);
				if(m==0)
					for(String word:dic){
						if(contents.get(i).contains(word)){
//							list.add(28.0);
							list.add(ratio*suicidePolarity);
//							negativeCount++;
						}
						else
							list.add(0.0);
					}
				else if(m==1)
					for(String word:dic){
						if(contents.get(i).contains(word)){
							list.add(ratio*upsetPolarity);
//							negativeCount++;
						}
						else
							list.add(0.0);
					}
				else if(m==2)
					for(String word:dic){
						if(contents.get(i).contains(word)){
							list.add(ratio);
//							negativeCount++;
						}
						else
							list.add(0.0);
					}
				else if(m==3)
					for(String word:dic){
						if(contents.get(i).contains(word)){
							list.add(ratio/2);
//							positiveCount++;
						}
						else
							list.add(0.0);
					}
				else if(m==4)//BiGram Dictionary
					for(String str:dic){
						String[] words=str.split(",");
						boolean flag=true;
						for(String word:words){
							if(!contents.get(i).contains(word)){
								flag=false;
								break;
							}
						}
						if(flag){
							list.add(ratio*suicidePolarity);
//							negativeCount++;
						}
						else
							list.add(0.0);
					}
				else if(m==5)//TriGram Dictionary
					for(String str:dic){
						String[] words=str.split(",");
						boolean flag=true;
						for(String word:words){
							if(!contents.get(i).contains(word)){
								flag=false;
								break;
							}
						}
						if(flag){
							list.add(ratio*suicidePolarity);
//							negativeCount++;
						}
						else
							list.add(0.0);
					}
			}
//			if(positiveCount>negativeCount)
//				ratio=-10;
//			else
//				ratio=10;
			
//			list.add(ratio);
			featureMap.put(i, list);
		}
		System.out.println("Words Feature Sizes: "+featureMap.get(0).size());		
		dictionaries.clear();
		return featureMap;
	}
	
	//delete all columns where contains all 0
	public static List<Integer> FilterList(
			HashMap<Integer, List<Integer>> featureMap) {
		// TODO Auto-generated method stub
		Set<Integer> keys=featureMap.keySet();
		List<List<Integer>>list=new ArrayList<List<Integer>>();
		List<Integer> filter=new ArrayList<Integer>();
		for(int i=0;i<featureMap.get(0).size();i++){
			list.add(new ArrayList<Integer>());
		}
		
		for(int key:keys){
			List<Integer> row=featureMap.get(key);
			for(int i=0;i<row.size();i++){
				list.get(i).add(row.get(i));
			}
		}
		
		for(int i=0;i<list.size();i++){
			List<Integer> column=list.get(i);
			boolean flag=true;
			for(int feature:column){
				if(feature==1){
					flag=false;
					break;
				}
			}
			
			if(flag==false){
				continue;
			}else{
				filter.add(i);
			}
		}
		
		return filter;
	}
	
	/**
	 * compute weight for each dictionary
	 * @return
	 */
	public static double DicWeightComputation(HashSet<String> dic){
		//initial weight
		double weight=10.0;

		
		return weight;
	}
	
	/**
	 * Formatted features for SVM
	 * @return
	 * @throws IOException 
	 */
	public static List<String> FormatFeaturesForSVM(boolean Write2File,int label) throws IOException{
		return FeatureCombinerAndWriter.FormatFeaturesForSVM(featureMap, Write2File,label);
	}
	
	/**
	 * Formatted features for Weka
	 * @return
	 * @throws IOException 
	 */
	public static List<String> FormatFeaturesForWeka(boolean Write2File,int label) throws IOException{
		return FeatureCombinerAndWriter.FormatFeaturesForWeka(featureMap, Write2File,label);
	}
	
	/**
	 * Test
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		FormatFeaturesForSVM(true,664);
		System.out.println(LibSvmUtils.CrossValidattion(10, "./resource/UnigramFeaturesSVM.txt"));
	}
}
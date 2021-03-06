package model.index;

import io.GetAllWeiboPosts;
import io.WriterUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.utils.Regex;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import entity.OneWeibo;

public class CosineSimilarity {
	IndexReader reader=null;
	Directory indexDir=null;
	IndexSearcher searcher=null;
	Query q=null;
	Analyzer analyzer=null;
	TopDocs docs;
	
	double allScore[];
	double countScore=0.0;
	String[] contents=new String[10];
	double queryScore=0.0;

	GetStopWords gsw;
	Document[] docsReader;
	
	public CosineSimilarity(){
		try {
			gsw=new GetStopWords();
			indexDir=FSDirectory.open(new File("./index"));
			reader=DirectoryReader.open(indexDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searcher=new IndexSearcher(reader);
		Set<String> filter=new HashSet<String>(gsw.getWords());
		analyzer=new SmartChineseAnalyzer(Version.LUCENE_4_9,new CharArraySet(Version.LUCENE_4_9,filter,false));
//		analyzer=new AnsjAnalysis(filter,false);
	}
	
	public double search(String query) throws ParseException{
		allScore=new double[10];
		countScore=0;
		queryScore=0;
		
		q=new QueryParser(Version.LUCENE_4_9, "content", analyzer).parse(query);
		
		//get top 10 docs
		try {
			docs=searcher.search(q,10);
			//read comments
			docsReader=new Document[10];
			
			for(int i=0;i<docs.scoreDocs.length-1;i++){
				docsReader[i]=searcher.doc(docs.scoreDocs[i].doc);
//				System.out.println(docs.scoreDocs[i]);
			}
			ScoreDoc[] scoreDoc=docs.scoreDocs;
			
			for(int i=0;i<docs.scoreDocs.length-1;i++){
				String temp=scoreDoc[i].toString();				
				temp=Regex.extractMotion(temp);
				double score=0;
				if(temp.contains("."))
					score= Double.valueOf(temp);
				else
					score=scoreDoc[i].score;
				allScore[i]=score;
//				System.out.println(score);
				
				countScore=countScore+score;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Calculate Scores for input query
//		System.out.println("countScore"+countScore);
		for(int m=0;m<docs.scoreDocs.length-1;m++){
//			System.out.println(allScore[m]);
			queryScore=queryScore+allScore[m]*(allScore[m]/countScore);
		}
//		System.out.println(queryScore);
		return queryScore;
	}
	
	public void close() throws Exception{
		reader.close();
		indexDir.close();
	}
	
	/**
	 * get top 10 query-related comments
	 * @return top 10 query-related comments
	 */
	public String[] getComments(){
		return this.contents;
	}
	
	/**
	 * get top 10 query-related comments' score
	 * @return top 10 query-related comments' score
	 */
	public double[] getScores(){
		return this.allScore;
	}
	/**
	 * get query's score
	 * @return query's score
	 */
	public double getQueryScore(){
		return this.queryScore;
	}
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		File file=new File("C:\\Users\\Administrator\\Desktop\\tempNoneALL.txt");
		CosineSimilarity c=new CosineSimilarity();
		GetAllWeiboPosts all=new GetAllWeiboPosts("C:\\Users\\Administrator\\Desktop\\tempTrainData.txt");
		List<Double> pids=new ArrayList<Double>();
		for(OneWeibo post:all.getList()){
			pids.add(Double.parseDouble(post.getPid()));
		}
		int count=0;
		List<String> list=new ArrayList<String>();
		
			BufferedReader reader=new BufferedReader(new FileReader(file));
			String line=new String();
			line=reader.readLine();
			while((line=reader.readLine())!=null){
				String content;
				double pid;
				try{
					content=line.split("\t")[1].trim();
					if(content.length()<2)
						continue;
					pid=Double.parseDouble(line.split("\t")[0]);
				}catch(Exception e){
					continue;
				}
//				System.out.println(line);
//				content=content.trim();
//				content=content.concat(" ");
//				content=content.replaceAll("\\[([^\\]]*)\\]", "");
//				content=content.replace("?", "");
//				
//				content=content.replace("~", "");
//				content=content.replace(":", "");
//				content=content.replace("-", "");
//				content=content.replace("^", "");
//				content=content.replace("!", "");			
//				System.out.println(line);
				
				try{
					double score=c.search(content);
					if(score<=0.25&&score>0.1&&!pids.contains(pid)){
						System.out.println(score+"\t"+content);
						list.add(line+"\t0");
						int t=Integer.parseInt(line.split("\t")[3]);
						if(t==1)
							count++;
					}
					
//					}else if(score<0.2){
//						list1.add(score+"\t"+file.getName()+"\t"+pid+"\t"+line);
//					}
				}catch(Exception e){
//					e.printStackTrace();
				}				
			}
			reader.close();
			System.out.println(count);
		try {
			c.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(list.size());
		WriterUtils.write2file(list, "test.txt");
	}
}
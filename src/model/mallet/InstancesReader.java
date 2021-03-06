package model.mallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.CharSequenceLexer;

/**
 * Read file data from given file, return data as Instances list to trainer
 * @author xiaolei
 */
public class InstancesReader {
	/**
	 * Read instances from CSV file. The delimiter is whitespace.
	 * Thus we use CharSequenceLexer.LEX_NONWHITESPACE_TOGETHER.
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public static InstanceList getInstances(String filepath) throws IOException{
		ArrayList<Pipe> pipeList=new ArrayList<Pipe>();
		pipeList.add( new CharSequenceLowercase() );
//        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new CharSequence2TokenSequence(CharSequenceLexer.LEX_NONWHITESPACE_TOGETHER) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("./resource/stopwords.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );
        
        InstanceList instances=new InstanceList(new SerialPipes(pipeList));
        Reader fileReader=new InputStreamReader(new FileInputStream(new File(filepath)),"UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data, label, name fields
        
        return instances;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		InstanceList list=InstancesReader.getInstances("./resource/LDATrainData.csv");
		int count=0;
		for(Instance i:list){
			System.out.println(i.getName());
//			System.out.println(i.getData());
			System.out.println(i.getTarget());
			System.out.println(i.getTargetAlphabet());
			if(Integer.parseInt(i.getTarget().toString())==1){
				count++;
//				System.out.println(i.getData());
			}
			break;
		}
		System.out.println(count);
	}
}
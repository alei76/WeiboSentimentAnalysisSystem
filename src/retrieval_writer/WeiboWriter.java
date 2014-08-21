package retrieval_writer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import retrieval_extractor.OneWeibo;
import retrieval_extractor.ParseProvinceXMLFile;
import retrieval_extractor.XMLParser;
import weibo4j.model.Tag;
import weibo4j.model.User;

/**
 * Write posts to File with given path, and write user's profile to DataBase
 * @author xiaolei
 */
public class WeiboWriter {	
	/**
	 * Write Data to File, the file name is better to be named as "uid.txt"
	 * @param file path and name to be written
	 * @return true is successful, false is fail
	 */
	public static boolean WritePosts2File(String uid,List<OneWeibo> weiboList,String path){
		StringBuilder buffer=new StringBuilder();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n");
		buffer.append("<posts "+"uid=\""+uid+"\" "+"count=\""+weiboList.size()+"\">"+"\n");
		for(OneWeibo post:weiboList){
			if(post.getType()==0){
				buffer.append("<post type=\""+post.getType()+"\">"+"\n");
				buffer.append("<content>"+post.getContent()+"</content>"+"\n");
				buffer.append("<date>"+post.getDate()+"</date>"+"\n");
				buffer.append("</post>"+"\n");
			}else{
				buffer.append("<post type=\""+post.getType()+"\">"+"\n");
				buffer.append("<content>"+post.getContent()+"</content>"+"\n");
				buffer.append("<forwardReason>"+post.getForwardReason()+"</forwardReason>"+"\n");
				buffer.append("<fromWho>"+post.getFromWho()+"</fromWho>"+"\n");
				buffer.append("<forwardChain>"+post.getForwardChain()+"</forwardChain>"+"\n");
				buffer.append("<date>"+post.getDate()+"</date>"+"\n");
				buffer.append("</post>"+"\n");
			}
		}
		buffer.append("</posts>");
		
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(path+uid+".txt"));
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Write the data to File as CSV File
	 * @param uid
	 * @param weiboList
	 * @param path
	 * @throws IOException
	 */
	public static void Write2CSVFile(String uid,List<OneWeibo> weiboList,String path)throws IOException{
		StringBuilder sb=new StringBuilder();
		sb.append("");
	}
	
	/**
	 * Write the data to File as Excel 2003 File
	 * @param uid
	 * @param weiboList
	 * @param path
	 * @throws IOException
	 */
	public static void Write2ExcelFile(String uid,List<OneWeibo> weiboList,String path)throws IOException{
		BufferedOutputStream stream=new BufferedOutputStream(new FileOutputStream(path));
		HSSFWorkbook excel=new HSSFWorkbook();
		
		HSSFSheet sheet=excel.createSheet();
		excel.setSheetName(0, uid);
		//Set Column Names
		HSSFRow columnNames=sheet.createRow(0);
		HSSFCell columnName=columnNames.createCell(0);
		columnName.setCellValue("");
		columnName=columnNames.createCell(1);
		columnName.setCellValue("");
		columnName=columnNames.createCell(2);
		columnName.setCellValue("");
		columnName=columnNames.createCell(3);
		columnName.setCellValue("");
		columnName=columnNames.createCell(4);
		columnName.setCellValue("");
		columnName=columnNames.createCell(5);
		columnName.setCellValue("");
		
		for(int rownum=1;rownum<weiboList.size();rownum++){
			HSSFRow row=sheet.createRow(rownum);
			//Set row cells
			HSSFCell cell=row.createCell(0);
			cell.setCellValue("");
			cell=columnNames.createCell(1);
			cell.setCellValue("");
			cell=columnNames.createCell(2);
			cell.setCellValue("");
			cell=columnNames.createCell(3);
			cell.setCellValue("");
			cell=columnNames.createCell(4);
			cell.setCellValue("");
			cell=columnNames.createCell(5);
			cell.setCellValue("");
		}
		
		excel.write(stream);
	}
	
	/**
	 * Write data to database
	 * @param u SinaWeibo User class
	 * @param tags SinaWeibo Tag class
	 */
	public static void WriteUserProfile2MySQL(User u,List<Tag> tags){
		ConnDB conn=new ConnDB("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306?DatabaseName=posts", "root", "root");
		StringBuilder buffer=new StringBuilder();
		for(Tag t:tags){
			buffer.append(t.getValue()+" ");
		}
		conn.executeQuery("insert into weibo.profile values("+u.getId()+","+u.getGender()+","+u.getUserDomain()+","+u.getScreenName()+","
				+getLocation(u.getProvince(),u.getCity())+","+u.getDescription()+","+u.getFollowersCount()+","+u.getFriendsCount()+","
				+buffer.toString().trim()+")");
		conn.close();
	}
	
	/**
	 * Write Data directly to file
	 * @param list the list of data
	 * @param filename define the file name
	 * @throws IOException
	 */
	public static void write2file(List<String>list,String filename) throws IOException{
		BufferedWriter writer=new BufferedWriter(new FileWriter("./resource/"+filename));
		for(String str:list){
			writer.append(str+"\n");
		}
		writer.flush();
		writer.close();
	}
	
	/**
	 * Parse the location of SinaWeibo user
	 * @param province
	 * @param city
	 * @return
	 */
	private static String getLocation(int province, int city) {
		// TODO Auto-generated method stub
		try{
			XMLParser p=new XMLParser("province","provinces.xml");
			ParseProvinceXMLFile parser=new ParseProvinceXMLFile(p.getList());
			return parser.SearchAllName(province, city);
		}catch(Exception e){
			return "null";
		}
	}
	

}
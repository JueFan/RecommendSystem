package org.juefan.IO;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileIO {
	private String fileName;
	private List<String> fileList;
	public static List<String> fileNameList = new ArrayList<String>(); 
	public FileIO(){
		fileName = new String();
		fileList = new ArrayList<String>();
	}

	/**设置要读取的文件的文件路径*/
	public void SetfileName(String fileString){
		this.fileName = fileString;
	}

	/**将fileList数组复制出来
	 * 并将原有的fileList清空*/
	public List<String> cloneList(){
		List<String> tmpList = new ArrayList<String>();
		tmpList.addAll(fileList);
		fileList.clear();
		return tmpList;
	}

	/**
	 * 扫描实例对象的fileName文件
	 * 将fileName文件内的内容按行存储进fileList数组中
	 */
	public void FileRead(){
		System.out.println("进入文件读取程序......");
		try{
			Scanner fileScanner = new Scanner(new File(fileName));
			if(new File( fileName).exists()){
				System.out.println("正在读取文件......");
				while(fileScanner.hasNextLine()){
					fileList.add(fileScanner.nextLine());
				}
				System.out.println("读取文件成功!");
			}
			System.out.println("文件共 " + fileList.size() + " 行");
		}catch (Exception e) {
			System.out.println("文件不存在!");
		}
	}
	/** 
	 * @param path 文件路径 
	 * @param suffix 后缀名 
	 * @param isdepth 是否遍历子目录 
	 * @return 满足要求的文件路径名列表
	 */ 
	public static List<String> getListFiles(String path, String suffix, boolean isdepth) { 
		File file = new File(path); 
		return FileIO.listFile(file ,suffix, isdepth); 
	} 
	
	public static List<String> getListFiles(String path, String suffix) { 
		File file = new File(path); 
		return FileIO.listFile(file ,suffix); 
	} 
	
	public static List<String> listFile(File f, String suffix) { 
		return FileIO.listFile( f,  suffix, true);
	}
	
	/** 
	 * 读取目录及子目录下指定文件名的路径 并放到一个数组里面返回遍历 
	 * @author juefan_c 
	 */ 
	public static List<String> listFile(File f, String suffix, boolean isdepth) { 
		//是目录，同时需要遍历子目录 
		if (f.isDirectory() && isdepth == true) { 
			//listFiles()是返回目录下的文件路径集合
			File[] t = f.listFiles(); 
			for (int i = 0; i < t.length; i++) {
				listFile(t[i], suffix, isdepth); 
			} 
		} 
		else { 
			String filePath = f.getAbsolutePath(); 
			if(suffix =="" || suffix == null) { 
				fileNameList.add(filePath); 
			} 
			else { 
				//最后一个.(即后缀名前面的.)的索引
				int begIndex = filePath.lastIndexOf("."); 
				//System.out.println("索引为 :"+begIndex);
				String tempsuffix = ""; 
				//防止是文件但却没有后缀名结束的文件
				if(begIndex != -1){  
					//tempsuffix取文件的后缀
					tempsuffix = filePath.substring(begIndex + 1, filePath.length()); 
				} 
				if(tempsuffix.equals(suffix)) { 
					fileNameList.add(filePath); 
				} 
			} 
		} 
		return fileNameList; 
	} 
	/**
	 * 将指定内容写入指定文件中
	 * 以追加的方式写入
	 * @param fileWriter  文件路径
	 * @param context  存储内容
	 * @param bool 是否追加写入
	 */
	public static void FileWrite(String fileName, String context, boolean bool){
		try{
			@SuppressWarnings("resource")
			FileWriter fileWriter = new FileWriter(fileName, bool);
			fileWriter.write(context);
			fileWriter.flush();
		}catch (Exception e) {
		}
	}
	
	/**
	 * 将指定内容写入指定文件中
	 * 以追加的方式写入
	 * @param fileWriter  文件路径
	 * @param context  存储内容
	 */
	public static void FileWrite(String fileName, String context){
		try{
			@SuppressWarnings("resource")
			FileWriter fileWriter = new FileWriter(fileName + ".txt", true);
			fileWriter.write(context);
			fileWriter.flush();
		}catch (Exception e) {
		}
	}
	
	public static void main(String[] args) {
		FileIO readFile = new FileIO();
		readFile.SetfileName(args[0]);
		List<String> list = new ArrayList<String>();
		list = FileIO.getListFiles(args[0], args[1]);
		for(String s:list)
			System.out.println(s);
	}

}

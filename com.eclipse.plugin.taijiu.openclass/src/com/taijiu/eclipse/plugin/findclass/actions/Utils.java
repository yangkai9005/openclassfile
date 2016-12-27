package com.taijiu.eclipse.plugin.findclass.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	private static final List<String> filterFileName;
	static{
		filterFileName = new ArrayList<String>();
		filterFileName.add("src");
		filterFileName.add(".settings");
		filterFileName.add(".metadata");
	}
	public static String getClassFilePath(String projectPath,String filePath){
		File projectFile = new File(projectPath);
		File[] listFile = projectFile.listFiles();
		String findFilePath = null;
		if(listFile != null){
			for(File file : listFile){
				if(file.isDirectory() && !filterFileName.contains(file.getName())){
					File classPath = new File(file.getAbsolutePath()+filePath);
					if(!classPath.exists()){
						findFilePath = getClassFilePath(file.getAbsolutePath(), filePath);
						if(findFilePath != null){
							return findFilePath;
						}
					}else{
						return file.getAbsolutePath();
					}
				}
			}
		}
		return findFilePath;
	}
}

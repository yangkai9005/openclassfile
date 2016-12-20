package com.taijiu.eclipse.plugin.findclass.actions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;



public class FindClass implements IObjectActionDelegate {

	private Shell shell;
	private String classFilePath = "";
	private static final String TITLE = "Class File Dialog";
	private static final String READ_ME = "readme.txt";
	
	/**
	 * Constructor for Action1.
	 */
	public FindClass() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if("".equals(classFilePath)){
			MessageDialog.openError(shell, TITLE, "请选择java文件!");
			return;
		}
		String command = "explorer.exe /SELECT,";
        command = command + classFilePath;
        System.out.println(classFilePath);
        try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NULL);
		dialog.setFilterPath(classFilePath);
        String path = dialog.open();
        if (path != null) {
        	if(MessageDialog.openConfirm(shell, TITLE, "是否将 \r\n"+classFilePath+"     \r\n导出到 \r\n"+path)){
        		fileCopy(classFilePath, path);
        		addReadMe(path, classFileAbsPath);
        	}
        	
        }*/
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		classFilePath = "";
		try {
			if(!selection.isEmpty() && selection instanceof TreeSelection){
				TreeSelection treeSelection = (TreeSelection)selection;
				Object firstEle = treeSelection.getFirstElement();
				if(firstEle != null && firstEle instanceof CompilationUnit){
					CompilationUnit firstUnit = (CompilationUnit)firstEle;
					IResource resouce = firstUnit.getResource();
					String projectPath = resouce.getProject().getLocation().makeAbsolute().toOSString();
					IPackageDeclaration[] packages = firstUnit.getPackageDeclarations();
					if(packages!= null && packages.length > 0){
						String javaName = firstUnit.getElementName();
						String className = firstUnit.getElementName();
						String packagePath = File.separator + packages[0].getElementName().replace(".", File.separator);
						if(javaName.endsWith(".java")){
							className = javaName.replaceAll(".java", ".class");
						}
						String classPath = getClassFilePath(projectPath, packagePath+File.separator+className);
						classFilePath = classPath + packagePath + File.separator + className;
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	private static String getClassFilePath(String projectPath,String filePath){
		File projectFile = new File(projectPath);
		File[] listFile = projectFile.listFiles();
		String findFilePath = null;
		if(listFile != null){
			for(File file : listFile){
				if(file.isDirectory() && !file.getName().endsWith("src")){
					File classPath = new File(file.getAbsolutePath()+filePath);
					if(!classPath.exists()){
						findFilePath = getClassFilePath(file.getAbsolutePath(), filePath);
					}else{
						return file.getAbsolutePath();
					}
				}
			}
		}
		return findFilePath;
	}
	
	
	/*private void fileCopy(String from,String to){
		BufferedOutputStream bw = null;
		BufferedInputStream br = null;
		File fromFile = new File(from);
		File toFile = new File(to + File.separator + fromFile.getName());
		try {
			if(!toFile.exists()){
				toFile.createNewFile();
			}
			br = new BufferedInputStream(new FileInputStream(fromFile));
			bw = new BufferedOutputStream(new FileOutputStream(toFile));
			byte[] b = new byte[1024];
			while(br.read(b) != -1 ){
				bw.write(b);
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(br != null){
					br.close();
				}
				if(bw != null){
					bw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	
	/*private void addReadMe(String path,String content){
		BufferedWriter bw = null;
		File readMe = new File(path+File.separator+READ_ME);
		try {
			if(!readMe.exists()){
				readMe.createNewFile();
			}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(readMe, true)));
			bw.write(content+System.getProperty("line.separator"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
}

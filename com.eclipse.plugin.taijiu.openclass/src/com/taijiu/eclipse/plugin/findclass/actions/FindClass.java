package com.taijiu.eclipse.plugin.findclass.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;



public class FindClass implements IObjectActionDelegate {

	private Shell shell;
	private String classFilePath = "";
	private static final String TITLE = "Class File Dialog";
	private static final String READ_ME = "readme.txt";
	private static final List<String> filterFileName;
	static{
		filterFileName = new ArrayList<String>();
		filterFileName.add("src");
		filterFileName.add(".settings");
		filterFileName.add(".metadata");
	}
	
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
			MessageDialog.openError(shell, TITLE, "��ѡ��java�ļ�!");
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

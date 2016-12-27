package com.taijiu.eclipse.plugin.findclass.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.io.Files;



public class FindFile implements IObjectActionDelegate {

	private Shell shell;
	private String filePath = "";
	private String destPath = "";
	private static final String TITLE = "File Dialog";
	private static final String ERROR = "请选择文件";
	private static final String COPY_ID = "com.taijiu.eclipse.plugin.findclass.copy";
	private static final String OPEN_ID = "com.taijiu.eclipse.plugin.findclass.open";
	private static final String[] BUILD_PATHS = new String[]{"target\\classes","target\\test-classes","build\\classes"}; 
	
	/**
	 * Constructor for Action1.
	 */
	public FindFile() {
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
		if("".equals(filePath)){
			MessageDialog.openError(shell, TITLE, ERROR);
			return;
		}
		if(COPY_ID.equals(action.getId())){
			copyFile();
		}
		if(OPEN_ID.equals(action.getId())){
			openFile();
		}
	}
	
	private void openFile(){
		String command = "explorer.exe /SELECT,";
        command = command + filePath;
        try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void copyFile(){
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NULL);
		dialog.setFilterPath(filePath);
        String path = dialog.open();
        if (path != null) {
        	if(MessageDialog.openConfirm(shell, TITLE, "是否将 \r\n"+filePath+"     \r\n复制到\r\n"+path)){
        		try {
        			File fromFile = new File(filePath);
        			File toFile = new File(path + File.separator +  destPath + fromFile.getName());
        			if(!toFile.exists()){
        				Files.createParentDirs(toFile);
        				toFile.createNewFile();
        			}
					Files.copy(fromFile, toFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        }
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		filePath = "";
		destPath = "";
		if(!selection.isEmpty() && selection instanceof TreeSelection){
			TreeSelection treeSelection = (TreeSelection)selection;
			Object firstEle = treeSelection.getFirstElement();
			if(firstEle != null && firstEle instanceof org.eclipse.core.internal.resources.File){
				org.eclipse.core.internal.resources.File selectFile = (org.eclipse.core.internal.resources.File)firstEle;
				String eleName = selectFile.getName();
				if(eleName.endsWith(".java")){
					eleName = eleName.replaceAll(".java", ".class");
				}
				String classPath = Utils.getClassFilePath(selectFile.getProject().getLocation().makeAbsolute().toOSString(), File.separator + eleName);
				filePath = classPath + File.separator + eleName;
				String pkgPath = getDestPath(classPath);
				destPath = selectFile.getProject().getName() + pkgPath + File.separator;
			}
		}
	}
	private static String getDestPath(String filePath){
		String tempPath = filePath;
		for(String str : BUILD_PATHS){
			if(tempPath.contains(str)){
				tempPath = tempPath.substring(tempPath.indexOf(str)+str.length());
			}
		}
		return tempPath;
	}
	
}

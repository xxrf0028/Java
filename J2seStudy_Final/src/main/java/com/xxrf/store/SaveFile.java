package com.xxrf.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.xxrf.main.StartCAD;
import com.xxrf.panel.PaintPanel;
import com.xxrf.shape.ShapeSet;

public class SaveFile extends JFileChooser{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5334498346398326009L;
	
	private StartCAD cad;
	private PaintPanel paintPanel;
	
	public SaveFile(StartCAD cad,PaintPanel paintPanel){
		this.cad = cad;
		this.paintPanel = paintPanel;
	}
	public void save(){
		File file = null;			
		int recieve = this.showSaveDialog(cad);
		if(recieve == APPROVE_OPTION){
			file = this.getSelectedFile();
			String fileName=file.getPath();
			if (file!=null&&file.exists())
			{
			int n = JOptionPane.showConfirmDialog(this, "是否要覆盖文件?","是!",JOptionPane.YES_NO_OPTION);
			if(n==JOptionPane.NO_OPTION){
				return;
			}
			}
			try{
			    FileOutputStream fileOut = null;
			    if(fileName.indexOf(".cad")!=-1){
			    	fileOut = new FileOutputStream(fileName);
			    }else{
			    	fileOut = new FileOutputStream(fileName+".cad");
			    }
			    ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);  
			    int e = paintPanel.current.size();
			    objectOut.writeInt(e);
			    for(int j=0;j<e;j++){
				    ShapeSet t=paintPanel.current.get(j);
				    //System.out.println(t);
				    objectOut.writeObject(t);
			    }
//			    paintPanel.current.clear();
//			    paintPanel.history.clear();
			    objectOut.close();         
			}catch (IOException e){   
				e.printStackTrace();    
			}  
			
		}
	}

}

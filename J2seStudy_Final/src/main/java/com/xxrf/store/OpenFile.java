package com.xxrf.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.xxrf.main.StartCAD;
import com.xxrf.panel.PaintPanel;
import com.xxrf.shape.ShapeSet;

public class OpenFile extends JFileChooser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6799430527091390381L;
	
	private StartCAD cad;
	private PaintPanel paintPanel;
	FileInputStream in=null;
	ObjectInputStream objectIn=null;
	
	public OpenFile(StartCAD cad,PaintPanel paintPanel){
		this.cad = cad;
		this.paintPanel = paintPanel;
	}
	
	public void open()throws Exception {
		File file = null;			
		int recieve = this.showOpenDialog(cad);
		if(recieve == APPROVE_OPTION){
			file = this.getSelectedFile();
			String fileName=file.getPath();
			try{
				paintPanel.current.clear();
			    in=new FileInputStream(fileName);  
			    objectIn= new ObjectInputStream(in);
			    int i=objectIn.readInt();
			    ShapeSet tt; 
			    for(int j=0;j<i;j++){
			    	tt = (ShapeSet) objectIn.readObject(); 
			    	paintPanel.current.add(tt);
			    }
			    if(i>0){
			    	paintPanel.history.add(paintPanel.copyCurrent());
			    	paintPanel.totalIndex = paintPanel.history.size()-1;
			    }
			    paintPanel.repaint();
			    objectIn.close();         
			 }catch (Exception e){   
				JOptionPane.showMessageDialog(this, "打开文件错误，可能是打开的文件格式不符（要求为.cad后缀文件）");   
			 }    
		}
	}
}

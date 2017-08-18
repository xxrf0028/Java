package com.xxrf.panel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.xxrf.command.Command;
import com.xxrf.main.StartCAD;
import com.xxrf.shape.Circle;
import com.xxrf.shape.Ellipse;
import com.xxrf.shape.Line;
import com.xxrf.shape.Rectangle;
import com.xxrf.shape.ShapeSet;
import com.xxrf.shape.Text;
import com.xxrf.store.Myfilter;
import com.xxrf.store.OpenFile;
import com.xxrf.store.SaveFile;


public class PaintPanel extends JPanel implements MouseListener,MouseMotionListener,KeyListener{

	private static final long serialVersionUID = -2175955416572421359L;
	
	//鼠标的press和release的Point
	private Point startPoint;
	private Point endPoint;
	
	//undo和redo的存取器
	public ArrayList<ShapeSet> current = new ArrayList<ShapeSet>();
	public ArrayList<ArrayList<ShapeSet>> history = new ArrayList<ArrayList<ShapeSet>>();
	
	//工具箱中工具的接口
	private int command = Command.SELECT;
	
	//画笔的颜色
	private Color color = Color.black;
	
	private String text = "";
	
	//图形总记录的下标
	public int totalIndex = -1;
	
	//所选图形下标
	private int index = -1;
	
	//改变大小的图形的下标
	private int changeIndex = -1;
	
	//用来消除一次显示两个图形的hotPoints
	private boolean change = false;
	
	//是否开始画新的图形
	private boolean isPressed = false;
	
	private StartCAD cad;
	
	public PaintPanel(StartCAD cad){
		this.cad = cad;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
	}
	
	/**
	 * 刷新面板 
	 */
	public void paint(Graphics g){
		Dimension size = getSize();
		int width = size.width;
		int height = size.height;
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		//不是选定操作时,所有已有图形改为UNSELECTED状态
		if(this.command != Command.SELECT){
			for(int i = 0;i<current.size();i++){
				current.get(i).setState(ShapeSet.UNSELECTED);
			}
		}
		//画出以前的图形
		ShapeSet htrShape = null;
		for(int i = 0; i<current.size();i++){
			htrShape = current.get(i);
			htrShape.draw(g,true);
		}
		if(isPressed){
			switch(this.command){
			case Command.LINE :
				new Line(this.startPoint,this.endPoint,this.color).draw(g,true);
				break;
			case Command.RECTANGLE :
				new Rectangle(this.startPoint,this.endPoint,this.color).draw(g,true);
				break;
			case Command.ELLIPSE :
				new Ellipse(this.startPoint,this.endPoint,this.color).draw(g,true);
				break;
			case Command.CIRCLE :
				new Circle(this.startPoint,this.endPoint,this.color).draw(g,true);
				break;
			case Command.TEXT :
				new Text(this.text,this.startPoint,this.endPoint,this.color).draw(g,true);
				break;
			case Command.SELECT :
				ShapeSet changeShape = this.getChangSizeShape();
				if(changeShape !=null){
					changeShape.draw(g,true);
					current.get(changeIndex).setState(ShapeSet.SELECTED);
					current.get(changeIndex).draw(g,true);
					//原始图形颜色变灰
					ShapeSet old =current.get(index).catchShape();
					old.setState(ShapeSet.SELECTED);
					old.setColor(Color.gray);
					old.draw(g,false);
				}else{
					ShapeSet selectedShape = whenSelect();
					if(selectedShape!=null){
						selectedShape.draw(g,true);
						current.get(index).setState(ShapeSet.SELECTED);
						current.get(index).draw(g,true);
						//原始图形颜色变灰
						ShapeSet old =current.get(index).catchShape();
						old.setState(ShapeSet.SELECTED);
						old.setColor(Color.gray);
						old.draw(g,false);
					}
				}
				break;
			}
			//这里是用来消除多重选择
			for(int i = 0;i<current.size();i++){
				current.get(i).setState(ShapeSet.UNSELECTED);
			}
		}
	}
	

	public void mouseClicked(MouseEvent e) {
		if(this.command==Command.SELECT){
			this.showHotZoom(e.getPoint());
		}
	}

	public void mouseEntered(MouseEvent e) {
		this.requestFocus();
		if(this.command!=Command.SELECT){
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}else{
			setCursor(Cursor.getDefaultCursor());
		}
	}

	public void mouseExited(MouseEvent e) {
	}
	
	/**
	 * 得到鼠标press时的startPoint
	 */
	public void mousePressed(MouseEvent e) {
		this.startPoint = e.getPoint();
		this.isPressed = true;
	}
	
	/**
	 * 得到鼠标release时的endPoint
	 */
	public void mouseReleased(MouseEvent e) {
		this.endPoint = e.getPoint();
		this.isPressed = false;
		switch(this.command){
		case Command.LINE :
			current.add(new Line(this.startPoint,this.endPoint,this.color));
			removeTail();
			history.add(copyCurrent());
			break;
		case Command.RECTANGLE :
			current.add(new Rectangle(this.startPoint,this.endPoint,this.color));
			removeTail();
			history.add(copyCurrent());
			break;
		case Command.ELLIPSE :
			current.add(new Ellipse(this.startPoint,this.endPoint,this.color));
			removeTail();
			history.add(copyCurrent());
			break;
		case Command.CIRCLE :
			current.add(new Circle(this.startPoint,this.endPoint,this.color));
			removeTail();
			history.add(copyCurrent());
			break;
		case Command.TEXT :
			current.add(new Text(this.text,this.startPoint,this.endPoint,this.color));
			removeTail();
			history.add(copyCurrent());
			break;
		case Command.SELECT :
			ShapeSet changeShape = this.getChangSizeShape();
			if(changeShape !=null){
				current.add(changeShape);
				current.remove(changeIndex);
				removeTail();
				history.add(copyCurrent());
				changeIndex = current.size()-1;;
			}else{
				ShapeSet shape = whenSelect();
				if(shape!=null){
					current.add(shape);
					current.remove(index);
					removeTail();
					history.add(copyCurrent());
					changeIndex = current.size()-1;
				}
			}
			break;
		}
		totalIndex = history.size()-1;
		this.repaint();
	}
	
	/**
	 * 得到鼠标拖动时的endPoint
	 */
	public void mouseDragged(MouseEvent e) {
		this.endPoint = e.getPoint();
		this.repaint();
		
	}
	
	/**
	 * 显示图形HotPoints
	 */
	public void mouseMoved(MouseEvent e) {
		if(this.command==Command.SELECT){
			if(this.changeSize(e.getPoint())<=-1){
			this.showHotPoints(e.getPoint());
			}
		}
	}


	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor(){
		return color;
	}


	public void setCommand(int command) {
		this.command = command;
	}
	
	public int getCommand(){
		return command;
	}

	public ArrayList<ShapeSet> getCurrent() {
		return current;
	}

	public void setCurrent(ArrayList<ShapeSet> current) {
		this.current = current;
	}

	public void setHistory(ArrayList<ArrayList<ShapeSet>> history) {
		this.history = history;
	}
	
	/**
	 * @return	true 在记录current和history中都为空! 
	 *          反之则false
	 */
	public boolean isRecordNull(){
		boolean bl = false;
		if(this.current.size()==0){
			this.clearRecord();
			bl = true;
		}
		return bl;
	}
	
	/**
	 * 清空记录current和history
	 */
	public void clearRecord(){
		this.changeIndex = -1;
		this.current.clear();
		this.history.clear();
	}
	
	/**
	 * 新建画板
	 */
	public void newPaintPanel(){
		if(!this.isRecordNull()){
			int replay =JOptionPane.showConfirmDialog(this, "是否要保存对现有图片的修改?");
			switch(replay){
			case JOptionPane.YES_OPTION :
				this.savePaint();
			case JOptionPane.NO_OPTION :
				this.clearRecord();
				this.repaint();
				break;
			case JOptionPane.CANCEL_OPTION :
				break;
			}
		}
	}
	
	/**
	 * 保存图片
	 */
	public void savePaint(){
		SaveFile save = new SaveFile(cad,this);
		save.addChoosableFileFilter(new Myfilter());
		save.save();
		repaint();
		
	}
	
	/**
	 * 退出画图系统
	 */
	public void exitPaint(){
		if(!this.isRecordNull()){
			int replay =JOptionPane.showConfirmDialog(this, "是否要保存对现有图片的修改?");
			switch(replay){
			case JOptionPane.YES_OPTION :
				this.savePaint();
			case JOptionPane.NO_OPTION :
				System.exit(0);
				break;
			case JOptionPane.CANCEL_OPTION :
				break;
			}
		}else{
			System.exit(0);
		}
	}
	
	/**
	 * 打开图片文件
	 */
	public void openImg(){
		if(!this.isRecordNull()){
			int replay =JOptionPane.showConfirmDialog(this, "是否要保存对现有图片的修改?");
			switch(replay){
			case JOptionPane.YES_OPTION :
				this.savePaint();
			case JOptionPane.NO_OPTION :
				this.clearRecord();
				OpenFile open = new OpenFile(cad,this);
				open.addChoosableFileFilter(new Myfilter());
				try{
					open.open();
				}catch(Exception e){
					e.getStackTrace();
				}
				
				break;
			case JOptionPane.CANCEL_OPTION :
				break;
			}
		}else{
			OpenFile open = new OpenFile(cad,this);
			open.addChoosableFileFilter(new Myfilter());
			try{
				open.open();
			}catch(Exception e){
				e.getStackTrace();
			}
		}
	}
	
	/**
	 * 显示图形HotPoints的方法
	 * @param mousePoint
	 */
	private int showHotPoints(Point mousePoint){
		int crtIndex = getIndex(mousePoint);
		if(crtIndex!=-1&&!change){
			current.get(crtIndex).drawHotPoints(this.getGraphics());
		}else{
			repaint();
		}
		return crtIndex;
	}
	
	/**
	 * @param mousePoint
	 * @return	选中图标在current中的下标,若无选中图形则return -1
	 */
	private int getIndex(Point mousePoint){
		for(int i=0;i<current.size();i++){	//通过循环到得要显示HotPoints的单个图形
			if( current.get(i).isHotPoint(mousePoint)){
				this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				if(index != i){
					index = i;
					change = true; 
				}else{
					change = false;
				}
				return i;
			}
		}
		setCursor(Cursor.getDefaultCursor());
		return -1;
	}
	
	/**
	 * 显示图形HotZoom的方法
	 * @param mousePoint
	 */
	private void showHotZoom(Point mousePoint){
		for(int i = 0;i<current.size();i++){
			current.get(i).setState(ShapeSet.UNSELECTED);
		}
		
		int index = getIndex(mousePoint);
		if(index!=-1){
			current.get(index).setState(ShapeSet.SELECTED);
		}
		changeIndex = index;
		repaint();
	}
	
	/**
	 * @return 到得选择图形的复本
	 */
	private ShapeSet whenSelect(){
		index = getIndex(startPoint);
		ShapeSet self = null;
		if(index != -1){
			ShapeSet currentShape = current.get(index);
			self = currentShape.catchShape();
			int addX = this.endPoint.x - this.startPoint.x;
			int addY = this.endPoint.y - this.startPoint.y;
			Point newStart = new Point(currentShape.getStartPoint().x+addX,currentShape.getStartPoint().y+addY);
			Point newEnd = new Point(currentShape.getEndPoint().x+addX,currentShape.getEndPoint().y+addY);
			self.setStartPoint(newStart);
			self.setEndPoint(newEnd);
			if(self.equals(currentShape)||endPoint.equals(startPoint)){
				return null;
			}
			self.setState(ShapeSet.SELECTED);
//			self.setRealShape();//重新定义RealShape,来消除原始实例不死现象!
		}
		return self;
	}
	
	/**
	 * 去除history的废旧记录
	 */
	private void removeTail(){
		if(totalIndex<history.size()){
			for(int i = totalIndex+1;i<history.size();){
				history.remove(i);
			}
		}
	}
	
	/**
	 * @return current的复本
	 */
	public ArrayList<ShapeSet> copyCurrent(){
		ArrayList<ShapeSet> al = new ArrayList<ShapeSet>();
		for(int i = 0;i<current.size();i++){
			current.get(i);
			al.add(current.get(i).catchShape());
			al.get(i).setState(ShapeSet.UNSELECTED);
		}
		return al;
	}
	
	/**
	 * @return 要改变大小的图形
	 */
	private ShapeSet getChangSizeShape(){
		ShapeSet shape = null;
		int zoomIndex = -1;
		if(changeIndex !=-1&&(zoomIndex = this.changeSize(startPoint))>-1){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			shape = current.get(changeIndex).catchShape();
			int addX = this.endPoint.x - this.startPoint.x;
			int addY = this.endPoint.y - this.startPoint.y;
			Point start = shape.getStartPoint();
			Point end = shape.getEndPoint();
			if(shape instanceof Circle){
				end = this.endPoint;
			}else if(shape instanceof Line){
				switch(zoomIndex){
				case 0:
					start = this.endPoint;
					break;
				case 1:
					end = this.endPoint;
					break;
				}
			}else{
				switch(zoomIndex){
				case 0:
					start = new Point(shape.getStartPoint().x+addX,shape.getStartPoint().y+addY);
					break;
				case 1:
					start = new Point(shape.getStartPoint().x,shape.getStartPoint().y+addY);
					break;
				case 2:
					start = new Point(shape.getStartPoint().x,shape.getStartPoint().y+addY);
					end = new Point(shape.getEndPoint().x+addX,shape.getEndPoint().y);
					break;
				case 3:
					end = new Point(shape.getEndPoint().x+addX,shape.getEndPoint().y);
					break;
				case 4:
					end = new Point(shape.getEndPoint().x+addX,shape.getEndPoint().y+addY);
					break;
				case 5:
					end = new Point(shape.getEndPoint().x,shape.getEndPoint().y+addY);
					break;
				case 6:
					start = new Point(shape.getStartPoint().x+addX,shape.getStartPoint().y);
					end = new Point(shape.getEndPoint().x,shape.getEndPoint().y+addY);
					break;
				case 7:
					start = new Point(shape.getStartPoint().x+addX,shape.getStartPoint().y);
					break;
				}
			}
			
			shape.setStartPoint(start);
			shape.setEndPoint(end);
			if(endPoint.equals(startPoint)){
				return null;
			}
			shape.setState(ShapeSet.SELECTED);
//			shape.setRealShape();//重新定义RealShape,来消除原始实例不死现象!
		}else{
			this.setCursor(Cursor.getDefaultCursor());
		}
		return shape;
	}
	
	/**
	 * 改变所选定图形的颜色
	 */
	public void changeColor(){
		ShapeSet shape = null;
		if(changeIndex !=-1){
			shape = current.get(changeIndex).catchShape();
			shape.setColor(getColor());
			current.remove(changeIndex);
			current.add(shape);
			removeTail();
			history.add(copyCurrent());
			totalIndex = history.size()-1;
			changeIndex = current.size()-1;
			current.get(changeIndex).setState(ShapeSet.SELECTED);
			repaint();
		}
	}
	
	/**
	 * 
	 * @param mousePoint
	 * @return 要改变的图形在current中的下标
	 */
	private int changeSize(Point mousePoint){
		int i;
			if( changeIndex>-1&& (i =current.get(changeIndex).inHotZoom(mousePoint))>-1){
				this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				return i;
			}
		
		setCursor(Cursor.getDefaultCursor());
		return -1;
	}
	
	/**
	 * 删除图形方法
	 *
	 */
	public void deleteShape(){
		if(changeIndex>-1){
			current.remove(changeIndex);
			removeTail();
			history.add(copyCurrent());
			changeIndex = -1;
			totalIndex = history.size()-1;
			repaint();
		}
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_DELETE&&this.command==Command.SELECT){
			this.deleteShape();
		}
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}

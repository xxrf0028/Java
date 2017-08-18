package com.xxrf.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.xxrf.command.Command;
import com.xxrf.panel.ColorPanel;
import com.xxrf.panel.PaintPanel;
import com.xxrf.panel.ShapeButton;

public class StartCAD extends JFrame{

	private static final long serialVersionUID = 1L;

	private PaintPanel paintPanel;
	private ColorPanel colorPanel;
	
	public StartCAD(){
		this.setTitle("CAD");
		this.setSize(800, 460);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initMenu();
		initPanel();
		initDraw();
	}
	
	public static void main(String[] args) {
		StartCAD cad = new StartCAD();
		cad.setVisible(true);
	}
	
	private void initPanel(){
		this.paintPanel = new PaintPanel(this);
		getContentPane().add(paintPanel, BorderLayout.CENTER);
		paintPanel.setPreferredSize(new Dimension(441, 385));
	}
	
	private void initDraw(){
		//选择栏目
		JToolBar drawToolBar = new JToolBar();
		getContentPane().add(drawToolBar, BorderLayout.EAST);
		drawToolBar.setPreferredSize(new Dimension(80, 600));
		drawToolBar.setOrientation(SwingConstants.VERTICAL);
		//
		ButtonGroup toolbuttonGroup = new ButtonGroup();
		//
		ShapeButton selectButton = new ShapeButton();
		drawToolBar.add(selectButton);
		toolbuttonGroup.add(selectButton);
		selectButton.setText("选择");
		selectButton.setSelected(true);
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectButtonActionPerformed(evt);
			}
		});
		//
		ShapeButton lineButton = new ShapeButton();
		drawToolBar.add(lineButton);
		toolbuttonGroup.add(lineButton);
		lineButton.setText("直线");
		lineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				lineButtonActionPerformed(evt);
			}
		});
		//
		ShapeButton rectButton = new ShapeButton();
		drawToolBar.add(rectButton);
		toolbuttonGroup.add(rectButton);
		rectButton.setText("矩形");
		rectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rectButtonActionPerformed(evt);
			}
		});
		//
		ShapeButton circleButton = new ShapeButton();
		drawToolBar.add(circleButton);
		toolbuttonGroup.add(circleButton);
		circleButton.setText("圆形");
		circleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				circleButtonActionPerformed(evt);
			}
		});
		//
		ShapeButton textButton = new ShapeButton();
		drawToolBar.add(textButton);
		toolbuttonGroup.add(textButton);
		textButton.setText("文本");
		textButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				textButtonActionPerformed(evt);
			}
		});
		//
		this.colorPanel = new ColorPanel();
		drawToolBar.add(colorPanel);
		colorPanel.setPreferredSize(new java.awt.Dimension(58, 337));
		colorPanel.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				colorPanelPropertyChange(evt);
			}
		});
	}
	
	/**
	 * 获得画笔颜色
	 * 
	 * @param evt
	 */
	private void colorPanelPropertyChange(PropertyChangeEvent evt) {
		this.paintPanel.setColor(this.colorPanel.getForwardColor());
		if(this.paintPanel.getCommand()==Command.SELECT){
			this.paintPanel.changeColor();
		}
	}

	private void initMenu(){
		//操作菜单
		JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(400,25));
		setJMenuBar(menuBar);
		//
		JMenu fileMenu = new JMenu();
		menuBar.add(fileMenu);
		fileMenu.setText("文件");
		fileMenu.setPreferredSize(new Dimension(55, 20));
		//
		JMenuItem newMenuItem = new JMenuItem();
		fileMenu.add(newMenuItem);
		newMenuItem.setText("新建");
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newMenuItemActionPerformed(evt);
			}
		});
		JMenuItem openMenuItem = new JMenuItem();
		fileMenu.add(openMenuItem);
		openMenuItem.setText("打开");
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openMenuItemActionPerformed(evt);
			}
		});
		JMenuItem saveMenuItem = new JMenuItem();
		fileMenu.add(saveMenuItem);
		saveMenuItem.setText("保存");
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveMenuItemActionPerformed(evt);
			}
		});
		JMenuItem exitMenuItem = new JMenuItem();
		fileMenu.add(exitMenuItem);
		exitMenuItem.setText("退出");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
	}
	
	private void newMenuItemActionPerformed(ActionEvent evt) {
		this.paintPanel.newPaintPanel();
	}
	
	private void openMenuItemActionPerformed(ActionEvent evt) {
		this.paintPanel.openImg();
	}
	
	private void saveMenuItemActionPerformed(ActionEvent evt) {
		this.paintPanel.savePaint();
	}
	
	private void exitMenuItemActionPerformed(ActionEvent evt) {
		this.paintPanel.exitPaint();
	}
	
	private void selectButtonActionPerformed(ActionEvent evt) {
		this.paintPanel.setCommand(Command.SELECT);
		this.paintPanel.repaint();
	}
	
	private void lineButtonActionPerformed(ActionEvent evt) {
		this.paintPanel.setCommand(Command.LINE);
		this.paintPanel.repaint();
	}
	
	private void rectButtonActionPerformed(ActionEvent evt) {
		this.paintPanel.setCommand(Command.RECTANGLE);
		this.paintPanel.repaint();
	}
	
	private void circleButtonActionPerformed(ActionEvent evt) {
		this.paintPanel.setCommand(Command.CIRCLE);
		this.paintPanel.repaint();
	}
	
	private void textButtonActionPerformed(ActionEvent evt) {
		String inputValue = JOptionPane.showInputDialog("请输入文本"); 
		this.paintPanel.setCommand(Command.TEXT);
		this.paintPanel.setText(inputValue);
		this.paintPanel.repaint();
	}
}

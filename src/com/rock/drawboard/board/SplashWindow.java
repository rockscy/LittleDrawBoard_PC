package com.rock.drawboard.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
软件作者： 熊锡君，时守刚
软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/
public class SplashWindow extends JWindow {
	/**
	 * 构造函数
	 * @param filename	欢迎屏幕所用的图片
	 * @param frame		欢迎屏幕所属的窗体
	 * @param waitTime	欢迎屏幕显示的事件
	 */
	public SplashWindow(String filename, JFrame frame, int waitTime) {
		super(frame);
		
		// 建立一个标签，标签中显示图片。
		JLabel label = new JLabel(new ImageIcon(filename));
		// 将标签放在欢迎屏幕中间
		getContentPane().add(label, BorderLayout.CENTER);
		pack();
		// 获取屏幕的分辨率大小
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// 获取标签大小
		Dimension labelSize = label.getPreferredSize();
		// 将欢迎屏幕放在屏幕中间
		setLocation(screenSize.width / 2 - (labelSize.width / 2),
				screenSize.height / 2 - (labelSize.height / 2));
		// 增加一个鼠标事件处理器，如果用户用鼠标点击了欢迎屏幕，则关闭。
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				dispose();
			
			}
		});
		
		final int pause = waitTime;
		/**
		 * Swing线程在同一时刻仅能被一个线程所访问。一般来说，这个线程是事件派发线程（event-dispatching thread）。 
		 * 如果需要从事件处理（event-handling）或绘制代码以外的地方访问UI，
		 * 那么可以使用SwingUtilities类的invokeLater()或invokeAndWait()方法。
		 */
		// 关闭欢迎屏幕的线程
		final Runnable closerRunner = new Runnable() {
			public void run() {
				setVisible(false);
				dispose();
				
			}
		};
		// 等待关闭欢迎屏幕的线程
		Runnable waitRunner = new Runnable() {
			public void run() {
				try {
					// 当显示了waitTime后，尝试关闭欢迎屏幕
					Thread.sleep(pause);
					SwingUtilities.invokeAndWait(closerRunner);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		setVisible(true);
		//启动等待关闭欢迎屏幕的线程
		Thread splashThread = new Thread(waitRunner, "SplashThread");
		splashThread.start();
	}	
	
}
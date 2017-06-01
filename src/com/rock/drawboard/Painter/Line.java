package com.rock.drawboard.Painter;

/*
软件作者： 熊锡君，时守刚
软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/
import com.rock.drawboard.board.MyCanvas;

import java.awt.Color;
import java.lang.Math;
import java.awt.Graphics;
import java.awt.*;


/**
 * 画线
 */
public class Line extends Shape {
	// 线的起点位置，x、y坐标
	private int x1;
	private int y1;
	
	// 线的终点位置，x、y坐标
	private int x2;
	private int y2;

	public Line(){
		super(Color.BLACK,Color.BLACK,1);
	}
	//private ;
	
	// 构造方法
	public Line(Color ColorPen,Color ColorBrush,int LineWide,
			int x1, int y1, int x2, int y2) {
		super(ColorPen,ColorBrush,LineWide);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
//	得到直线的矩形边界
	public void GetRect(Point point1,Point point2)
	{
		point1.x = Math.min(x1,x2);		
		point2.x = Math.max(x1,x2);		
		point1.y = Math.min(y1,y2);		
		point2.y = Math.max(y1,y2);
		
	}
//	判断是否被点选中的函数
	public boolean IsPoint(int x,int y,float j1)
	{
		float xx,x1=0,x2=0,y1=0,y2=0;
		Point point1 = new Point();
		Point point2 = new Point();
		GetRect(point1,point2);
		x1 = point1.x;
		y1 = point1.y;
		x2 = point2.x;
		y2 = point2.y;
		if(!(x >= x1-j1 && x <= x2+j1 && y >= y1-j1 && y <= y2+j1))
			return false;
		xx = super.PointLine(x,y,this.x1,this.y1,this.x2,this.y2);
		if(xx < j1)
			return true;
		return false;
		
	}
	
	
	
	
	// 画直线
	public void draw(Graphics g,int m_DrawMode,Color bgColor) {
		Graphics2D g2d = (Graphics2D) g;
		// 设置画笔颜色
		g2d.setColor(super.m_ColorPen);
		g2d.setStroke(MyCanvas.STROKES[super.m_LineWide]);
		// drawLine方法画线
	
		if(m_DrawMode == 0)   //正常画图
		{
			g2d.drawLine(this.x1,this.y1,this.x2,this.y2);
	
		
		}
		else{   //选中时特殊显示
			Stroke thindashed = new BasicStroke(2.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL,1.0f,
					new float[]{8.0f,3.0f,2.0f,3.0f},
					0.0f);
			g2d.setColor(bgColor);
			g2d.drawLine(this.x1,this.y1,this.x2,this.y2);
			g2d.setStroke(thindashed);
			g2d.setColor(super.m_ColorPen);
			g2d.drawLine(this.x1,this.y1,this.x2,this.y2);
		}
	}
}
package com.rock.drawboard.Painter;
/*
软件作者： 熊锡君，时守刚
软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/

import com.rock.drawboard.board.MyCanvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;


// 画矩形
public class Rectangle extends Shape {
	// 矩形左上角位置的坐标，x、y值
	private int x;
	private int y;
	
	// 矩形的长和宽
	private int width;
	private int height;
	private boolean fill = false;   //是否实心
	

	// 构造方法
	public Rectangle(Color ColorPen,Color ColorBrush,int LineWide,
			int x, int y, int width, int height ,boolean Fill) {
		super(ColorPen,ColorBrush,LineWide);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		fill = Fill;
	}
//	得到矩形的矩形边界
	public void GetRect(Point point1,Point point2)
	{
		point1.x = this.x;
		point1.y = this.y;
		point2.x = this.x + this.width;
		point2.y = this.y + this.height;
	}
//	判断是否被点选中的函数
	public boolean IsPoint(int x,int y,float j1)
	{
		float xx,x1=0,y1=0,x2=0,y2=0;
	
		Point point1 = new Point();
		Point point2 = new Point();
		GetRect(point1,point2);
		x1 = point1.x;
		y1 = point1.y;
		x2 = point2.x;
		y2 = point2.y;
		if(!fill)
		{
			xx = super.PointLine(x,y,this.x,this.y+this.height,this.x,this.y );
			if(xx < j1)
				return true;
			xx = super.PointLine(x,y,this.x,this.y + this.height,this.x+this.width,this.y);
			if(xx < j1)
				return true;
			xx = super.PointLine(x,y,this.x,this.y + this.height,this.x+this.width,this.y + this.height);
			if(xx < j1)
				return true;
			xx = super.PointLine(x,y,this.x+this.width,this.y,this.x+this.width,this.y + this.height);
			if(xx < j1)
				return true;

		}
		else
		{
			if((x >= x1-j1 && x <= x2+j1 && y >= y1-j1 && y <= y2+j1))
				return true;
		}
		return false;
		
	}
	 //判断是矩形还是区域
	public boolean IsRectangle()
	{
		return !fill;
	}
	
	
	
	// 画矩形
	public void draw(Graphics g,int m_DrawMode,Color bgColor) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(Color.white);
		g2d.setColor(super.m_ColorPen);
		g2d.setStroke(MyCanvas.STROKES[super.m_LineWide]);
	
		if(m_DrawMode == 0)   //正常画图)
		{
			if(fill == false){
				g2d.drawRect(this.x, this.y,this.width,this.height);
			}
			else
			{
				g2d.drawRect(this.x, this.y,this.width,this.height);
				g2d.setColor(super.m_ColorBrush);
				g2d.fillRect(this.x, this.y,this.width,this.height);
			}
		}
		else  //选中时特殊显示
		{
			Stroke thindashed = new BasicStroke(2.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL,1.0f,
					new float[]{8.0f,3.0f,2.0f,3.0f},
					0.0f);
			g2d.setStroke(thindashed);
			g.setColor(Color.RED);
			if(fill == false){
				g2d.drawRect(this.x, this.y,this.width,this.height);
			}
			else
			{
				g2d.drawRect(this.x, this.y,this.width,this.height);
				g2d.setColor(Color.RED);
				g2d.fillRect(this.x, this.y,this.width,this.height);
			}
		}
	}
}


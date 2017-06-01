package com.rock.drawboard.Painter;


import com.rock.drawboard.board.MyCanvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;



/**
 * 画圆
 */
public class Circle extends Shape {
	// 圆心位置，x、y坐标
	private int x;
	private int y;
	// 圆的半径
	private int radius;
	private boolean fill= false;   //是否实心

	// 构造方法
	public Circle(Color ColorPen,Color ColorBrush,int LineWide,
			int x, int y, int radius, boolean Fill) {
		super(ColorPen,ColorBrush,LineWide);
		this.x = x;
		this.y = y;
		this.radius = radius;
		fill = Fill;
	}

//	判断圆是否被选中
	public boolean IsPoint(int x,int y,float j1)
	{
		float xx;
		xx = DisPoint(x,y,this.x,this.y);
		if(!fill)
		{
			if(xx > this.radius - j1 && xx < this.radius + j1)
				return true;
		}
		else
		{
			if(xx <= this.radius)
				return true;
		}
		return false;
	}
//	判断是圆还是区域
	boolean IsCircle()
	{
		return !fill;
	}
	

	
	
	
	// 画圆
	public void draw(Graphics g,int m_DrawMode,Color bgColor) {
		Graphics2D g2d = (Graphics2D) g;
		// 设置画笔颜色
//		if(b_Delete==true)
	//		return;
		g2d.setColor(super.m_ColorPen);
		g2d.setStroke(MyCanvas.STROKES[super.m_LineWide]);
		// drawArc画弧，当弧的宽度和高度一样，而且弧度从0到360度时，便是圆了。

		if(m_DrawMode == 0)   //正常画图)
		{
			if(fill == false){
				g.drawArc(this.x, this.y, this.radius, this.radius, 0, 360);
			}
			else{
				System.out.println("Draw Circle: " + fill);
				g2d.drawArc(this.x, this.y, this.radius, this.radius, 0, 360);
				g2d.setColor(super.m_ColorBrush);
				g2d.fillArc(this.x, this.y, this.radius, this.radius, 0, 360);
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
			g2d.setColor(Color.RED);
			if(fill == false){
				g2d.drawArc(this.x, this.y, this.radius, this.radius, 0, 360);
			}
			else{
				g2d.drawArc(this.x, this.y, this.radius, this.radius, 0, 360);
				g2d.setColor(Color.RED);
				g2d.fillArc(this.x, this.y, this.radius, this.radius, 0, 360);
			}
		}
		
	}
}
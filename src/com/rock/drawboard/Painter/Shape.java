package com.rock.drawboard.Painter;
import java.awt.*;
import java.lang.Math;
import java.io.Serializable;
/*
软件作者： 熊锡君，时守刚
软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/

public class Shape implements Serializable{
	Color  m_ColorPen;   //笔色
	Color m_ColorBrush; //刷颜色
	int m_LineWide;   //线宽
	
	
	public  boolean IsPoint(int x,int y,float j1){return false;}
//	 画图，每个自定义图形都必须实现该接口。
	public void draw(Graphics g,int m_DrawMode,Color bgColor){}    //m_DrawMode = 0 默认画图，m_DrawMode ＝ 1 选中画图
//	计算两点距离sqrt
	public float DisPoint(int x1,int y1,int x2,int y2)
	{
		return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)); 
	}
//	点到直线距离
	public float PointLine(int xx,int yy,int x1,int y1,int x2,int y2)  
	{
		float a,b,c,ang1,ang2,ang;
		//计算三条边的距离
		a = DisPoint(x1,y1,xx,yy);
		if(a == 0.0) return (float) 0.0;
		b = DisPoint(x2,y2,xx,yy);
		if(b == 0.0) return (float) 0.0;
		c = DisPoint(x1,y1,x2,y2);
		if(c == 0.0) return a;
		if(a < b)
		{
			if(y1 == y2)
			{
				if(x1 < x2)
					ang1 = 0;
				else 
					ang1 = (float)Math.PI;
			}
			else 
			{
				ang1 = (float)Math.acos((x2 - x1) / c);
				if(y1 > y2)
					ang1 = (float)Math.PI * 2 - ang1;
			}
			ang2 = (float)Math.acos((xx - x1) / a);
			if(y1 > yy) ang2 = (float)Math.PI * 2 - ang2;
			ang = ang2 - ang1;
			if(ang < 0) ang = -ang;
			if(ang > Math.PI)ang = (float)Math.PI *2 - ang;
			if(ang > Math.PI/2) return a;
			else
				return (a * (float)Math.sin(ang));
		}
		else
		{
			if(y1 == y2)
			{
				if(x1 < x2)
					ang1 = (float)Math.PI;
				else
					ang1 =0;
			}
			else
			{
				ang1 = (float)Math.acos((x1 -x2)/c);
				if(y2 > y1) ang1 = (float)Math.PI * 2 - ang1;
			}
			ang2 = (float)Math.acos((xx - x2)/b);
			if(y2 > yy) ang2= (float)Math.PI * 2 - ang2;
			ang = ang2 - ang1;
			if(ang < 0) ang = -ang;
			if(ang > Math.PI)ang = (float)Math.PI *2 - ang;
			if(ang > Math.PI/2) return b;
			else
				return (b * (float)Math.sin(ang));
		}
		
	}



	public Shape(){
		
	}
	
	public Shape(Color colorPen, Color colorBrush, int lineWide) {
		m_ColorPen = colorPen;
		m_ColorBrush = colorBrush;
		m_LineWide = lineWide;
	
	}
	
	
}
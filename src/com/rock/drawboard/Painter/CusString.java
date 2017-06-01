package com.rock.drawboard.Painter;

/*
 软件作者： 熊锡君，时守刚
 软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/

import java.io.Serializable;
import java.awt.*;
import java.awt.geom.*;
import java.awt.datatransfer.*;


/**
 * CusString类，实现Serializable,CusShape接口
 */
public class CusString extends Shape{
  public String aString;
  public int x,y;
  private Font aFont=new Font("",Font.BOLD,10);
  private Color col=Color.black;
  /**
   * 构造函数
   * @param aString String
   * @param x int
   * @param y int
   */
  public CusString(String aString, int x, int y)
  {
	super();
    this.aString=aString;
    this.x=x;
    this.y=y;
  }
  /**
   * 构造函数
   * @param aString String
   * @param x int
   * @param y int
   * @param aFont Font
   * @param col Color
   */
  public CusString(String aString, int x, int y, Font aFont, Color col)
  {
    this.aString=aString;
    this.x=x;
    this.y=y;
    this.aFont=aFont;
    this.col=col;
  }
  /**
   * 设置字体
   * @param f Font
   */
  public void setFont(Font f)
  {
    this.aFont=f;
  }
  /**
   * 得到字体
   * @return Font
   */
  public Font getFont()
  {
    return aFont;
  }
  /**
   * 判断是否与x,y,w,h构成的矩形相交
   * @param x double
   * @param y double
   * @param w double
   * @param h double
   * @return boolean
   */
  public boolean intersects(double x,double y,double w,double h)
  {
    Rectangle2D rec2D=new Rectangle2D.Float(this.x,this.y,aFont.getSize()*aString.length()/2,aFont.getSize());
    return rec2D.intersects(x,y,w,h);
  }
  /**
   * 得到边界
   * @return Rectangle
   */
  public java.awt.Rectangle getBounds()
  {
    return new java.awt.Rectangle(this.x,this.y,aFont.getSize()*aString.length()/2,aFont.getSize());
  }
  /**
   * 设置位置
   * @param x int
   * @param y int
   */
  public void setLocation(int x,int y)
  {
    this.x=x;
    this.y=y;
  }
  /**
   * 设置颜色
   * @param col Color
   */
  public void setColor(Color col)
  {
    this.col=col;
  }
  
  
  public boolean IsPoint(int x,int y,float j1)
	{
	  java.awt.Rectangle rect = this.getBounds();
	  int x1 = 0,y1 = 0,x2 = 0,y2 = 0;
	  x1 = this.x;
	  y1 = this.y;
	  x2 = x1 + rect.width;
	  y2 = y1 + rect.height;
	  if((x >= x1-j1 && x <= x2+j1 && y >= y1-j1 && y <= y2+j1))
			return true;
	  return false;
	}
  /**
   * 得到颜色
   * @return Color
   */
  public Color getColor()
  {
    return col;
  }
  /**
   * 绘制方法
   * @param g Graphics
   */
  public void draw(Graphics g,int m_DrawMode,Color bgColor)
  {
	  Graphics2D g2d=(Graphics2D)g;
//		  if(super.b_Delete==true)
	//		  return;
		  g2d.setColor(col);
		  g2d.setFont(aFont);
		  g2d.drawString(aString,x,y+aFont.getSize());
		
	 if(m_DrawMode != 0)
	  {
		  Stroke thindashed = new BasicStroke(2.0f,
				  BasicStroke.CAP_BUTT,
				  BasicStroke.JOIN_BEVEL,1.0f,
				  new float[]{8.0f,3.0f,2.0f,3.0f},
				  0.0f);
		  g2d.setStroke(thindashed);
		  java.awt.Rectangle rect = this.getBounds();
		  g2d.drawRect(this.x,this.y,rect.width,rect.height);
		  
	  }
  }
  /**
   * 设置是否填充，无意义
   * @param isFill boolean
   */
  public void setIsFill(boolean isFill)
  {
  }
  /**
   * 得到是否填充，无意义
   * @return boolean
   */
  public boolean getIsFill()
  {
    return false;
  }
  /**
   * 设置字体
   * @param aStroke BasicStroke
   */
  public void setStroke(BasicStroke aStroke)
  {

  }
  
}

package com.rock.drawboard.board;



import com.rock.drawboard.Painter.*;
import com.rock.drawboard.Painter.Command;
import com.rock.drawboard.Painter.Rectangle;
import com.rock.drawboard.Painter.Shape;
import com.rock.drawboard.module.*;
import com.rock.drawboard.socket.ServerAction;


import javax.swing.*;
import java.awt.*;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MyCanvas extends JPanel implements MouseMotionListener, MouseListener {

	public float blc=1;  //绘图时的起点坐标呵比例尺
	Dimension size = getSize();
	int m_wScreen=828,m_hScreen=426;  // 当前视图的高度and宽度
    Point ZoomPoint = new Point();
    PaintBoard pd = null;
	// 记录鼠标点击的起始位置，x、y坐标
	int beginX = 0;
	int beginY = 0;
	// 记录鼠标点击的当前位置，x、y坐标
	int currentX = 0;
	int currentY = 0;
	int PastX  = 0;
	int PastY = 0;
	//记录橡皮擦和清除
	int eraser = 0;
	int clear = 0;
    //是否填充
	boolean fill = false;
	// 表示当前鼠标是否被按下
	boolean bMousePressing = false;
	Pencil pencil = null;
	JTextArea statusTextArea = null;

	private ArrayList Shapes = new ArrayList(), DelShapes = new ArrayList();
	BufferedImage offScreenImg = null;
	Graphics2D offScreenG = null;
	
	// 记录当前的命令，是画圆、画线、还是画矩形
	private int command = Command.PENCIL;
	// 画笔的颜色
	private Color fgColor = Color.BLACK;
	private Color bgColor = Color.WHITE;
	private Color brushColor = Color.BLACK;

    public static final Stroke[] STROKES = new Stroke[] {
        new BasicStroke(5.0f),
        new BasicStroke(8.0f),
        new BasicStroke(10.0f),
        new BasicStroke(15f),
        new BasicStroke(20f)
    };

    public static final int[] ERASER_STROKES = new int[] {
       15,20,30,50,100
    };

    private int strokeIndex = 0, eraserIndex = 0;
    private int n_GraphSelect = 0;     //选中图形元素的总数

    //存放选中图形位置
   private  int[] GraphSelect = new int[200];


	//设置画板当前的绘图命令
		public String getCommandString(int command){
			switch(command){
			case Command.LINE:
				return "Line";
				
			case Command.CIRCLE:
				return "Circle";
				
			case Command.PENCIL:
				return "Pencil";
				
			case Command.RECTANGLE:
				return "Rectangle";
				
			case Command.ERASER:
				return "ERASER";
			case Command.SELECT:
				return "SELECT";
			case Command.DELETE:
				return "DELETE";
			case Command.UNSELECTED:
				return "UNSELECTED";
			case Command.TEXTINSERT:
				return "TEXTINSERT";
				
			}	
			return "none";
		}


//  实现对所有图形元素判断是否被选中
   public int  PointSelect(int x,int y,float j1)
    {

    	int i = 0;
    	Shape shape = null;
    	for(; i < Shapes.size();i++)
    	{
    		shape = (Shape)Shapes.get(i);
    		if(shape.IsPoint(x,y,j1))
    		{
    			return i;
    		}

    	}
    	return -1;
    }
// 存储选中图形元素
   public boolean AddSelectList(int Index)
   {
   	for(int i = 0; i < n_GraphSelect; i++)
   	{
   		if(Index == GraphSelect[i])
   			return false;
   	}
   	
   	GraphSelect[n_GraphSelect++] = Index;
   	return true;
   }
// 绘制图形元素特殊显示
   public void DrawGraph(Graphics g,int Index)
   {


	   if(Index < Shapes.size()){
		   	Shape shape =(Shape) Shapes.get(Index);
		   	shape.draw(g,1,bgColor);
	   }
   }
    //设置画刷颜色
   public void setBrushColor(Color c){
	   brushColor = c;
   }
   
   //删除指定编号的已绘图形
   public void Delete(int Index)
   {

	   if(Index < Shapes.size()){
		   Shape shape =(Shape) Shapes.get(Index);
		   Shapes.remove(Index);
		   DelShapes.add(shape);
	   }
   }
   
   
   //保存图像为JPG格式
   public void SaveFile(String fileName) throws Exception {
	   ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
	   out.writeObject(Shapes);    //写存储图形对象的数组
	   out.writeObject(this.bgColor);  //写背景颜色
	   out.close();
   }
   
 //  打开jwd格式文件

 public void OpenFile(String fileName) throws Exception{
	DelShapes.clear();  //清空存储删除图形对象的数组
 	ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
 	Shapes =(ArrayList) (in.readObject()); //读存储图形对象的数组
 	this.bgColor = (Color)(in.readObject());//读背景颜色
 	in.close();
 	repaint();
   }
	/**
	 * 构造方法
	 */
	public MyCanvas(JTextArea statusTextArea, PaintBoard pd) {
		this.pd = pd;
        strokeIndex = 0;
        eraserIndex = 0;
        this.statusTextArea = statusTextArea;
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        setForeground(Color.black);
        setBackground(Color.white);
       setPreferredSize(new Dimension(900,400));
		// 添加事件处理器
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

	}

	   public void setStrokeIndex(int i) {
	        if (i < 0 || i > 4)
	            throw new IllegalArgumentException("Invaild Weight Specified!");
	        strokeIndex = i;
	    }
	    
	    public void setEraserIndex(int i) {
	        if (i < 0 || i > 4)
	            throw new IllegalArgumentException("Invaild Size Specified!");
	        eraserIndex = i;
	    }
	    
	    public void clearBoard() {
		isClear = true;
	    	Shapes.clear();
	    	DelShapes.clear();
	    	n_GraphSelect = 0;
	    	repaint();
	    }
	
	/**
	 * 撤销操作，取消刚刚画的图形。
	 */
	private boolean isClear;
	public void undo() {
		isClear = true;
		if (Shapes.size() > 0) {

			// 将vShapes中的最后画的一个图形取出
			Object shape = Shapes.remove(Shapes.size() - 1);
			// 放入到vDelShapes中
			DelShapes.add(shape);
			// 重画画板

			repaint();
		}
	}
	/**
	 * 恢复操作，取消刚刚撤销的图形
	 */
	public void redo() {
		isClear = true;
		if (DelShapes.size() > 0) {
			// 将vDelShapes中最后一个被删除的图形取出
			Object shape = DelShapes.remove(DelShapes.size() - 1);
			// 放到vShapes中
			Shapes.add(shape);
			// 重画画板
			repaint();
		}
	}
	/**
	 * 设置命令，画线、画圆、画矩形
	 * @param command
	 */
	public void setCommand(int command) {
		this.command = command;

	}
	public void setCommandToAndroid(int command) {
		this.command = command;
	}
	/**
	 * 设置画笔颜色
	 * @param color
	 */
	public void setForegroundColor(Color color) {
		this.fgColor = color;
	}
	
	/**
	 * 设置背景颜色
	 * @param color
	 */
	public void setBackgroundColor(Color color) {
		this.bgColor = color;
	}
	public   Color getBackgroundColor() {
		return bgColor;
	}
	
	public void setn_GraphSelect(int n) {
		n_GraphSelect = n;
		Graphics g=getGraphics();
		paint(g);
	}
	
	public void setSelectDel() {
		for(int i = 0; i < n_GraphSelect; i++)
			Delete(GraphSelect[i]);
//		System.out.println("n_GraphSelect:" +canvases[canvas_control].n_GraphSelect);
		n_GraphSelect = 0;
		Graphics g=getGraphics();
		paint(g);
	}
	/**
	 * 在java中repaint()是通过两个步骤来实现刷新功能的，首先它调用public void update()来刷新屏幕，
	 * 其次再调用paint(Graphcis g)来重画屏幕，这就容易造成闪烁，特别是一些需要重画背景的程序，
	 * 如果下一桢图象可以完全覆盖上一桢图象的话，便可以重写update函数如下来消除闪烁： 
	 * public void update(Graphics g){ paint(g) }同样调用repaint()重画屏幕。
	 * 或者直接重写不调用repaint，而用Graphics g=getGraphics(); paint(g)；来实现重画屏幕。
	 */
	/**
	 * 重写update方法以消除闪烁。
	 */

	/**
	 * 画画板
	 */
	public void paint(Graphics g1) {
		super.paint(g1);
		Graphics2D g = (Graphics2D) g1;
		/** 画画板上的图形 **/
		int i = 0;
		Shape  shape = null;
		for(i = 0; i < Shapes.size(); i++)
		{
			shape = (Shape) Shapes.get(i);
			shape.draw(g, 0,bgColor);
		}
		for(i = 0; i < n_GraphSelect; i++)
	   	{
	   		DrawGraph(g,GraphSelect[i]);
	   	}

		// 如果当前鼠标还没有松开
		if (bMousePressing) {
			// 设置画笔颜色
			g.setColor(fgColor);
			switch (command) {
			case Command.LINE:
				// 画线，在鼠标起始位置和当前位置之间画一条直线
				g.drawLine(beginX, beginY, currentX, currentY);
				break;
			case Command.PENCIL:
				// 画笔
				pencil.draw(g, 0,bgColor);
				break;
			case Command.RECTANGLE:

					if (currentX < beginX) {
					// 如果鼠标当前位置在起始位置的左上方，则以鼠标当前位置为矩形的左上角位置。
						if (currentY < beginY) {
						g.drawRect(currentX, currentY, beginX - currentX, beginY
								- currentY);
						} else {
						// 如果鼠标当前位置在起始位置的左下方，
						// 则以当前位置的横坐标和起始位置的纵坐标为矩形的左上角位置
						g.drawRect(currentX, beginY, beginX - currentX, currentY
								- beginY);
						}
					} else {
					// 如果鼠标当前位置在起始位置的右上方，
					// 则以鼠标起始位置的横坐标和当前的纵坐标为矩形的左上角位置
						if (currentY < beginY) {
							g.drawRect(beginX, currentY, currentX - beginX, beginY
								- currentY);
						} else {
						// 如果鼠标当前位置在起始位置的右下方，则以起始位置为矩形的左上角位置
							g.drawRect(beginX, beginY, currentX - beginX, currentY - beginY);
						}
					}

				/*int width=Math.abs(beginX-currentX);//横坐标距离
				int height=Math.abs(beginY-currentY);//纵坐标距离
				int startX=trueStart(beginX, currentX, width);
				int startY=trueStart(beginY, currentX, height);
				Rectangle2D.Float rectangle = new Rectangle2D.Float(startX,startY,width,height);
				g.setPaint(Color.black);
				g.draw(rectangle);*/
				break;
			case Command.CIRCLE:
				// 画圆，获取半径。半径等于a*a + b*b的平方根
					int radius = (int) Math.sqrt((beginX - currentX)
							* (beginX - currentX) + (beginY - currentY)
							* (beginY - currentY));
				if(fill == false)
				{
				// 画圆弧
					g.drawArc(beginX - radius / 2, beginY - radius / 2, radius, radius, 0, 360);
				}
				else{
					g.fillArc(beginX - radius / 2, beginY - radius / 2, radius, radius, 0, 360);
				}


				break;
			case Command.ERASER:
				int erasewidth = ERASER_STROKES[eraserIndex];
				g.setColor(bgColor);
				g.fillRect(currentX,currentY,erasewidth,erasewidth);
				Eraser erase = new Eraser(fgColor,brushColor,eraserIndex,currentX,currentY);
				Shapes.add(erase);

			}//End switch(command)
		}
isClear = false;
	}
	private int trueStart(int start,int end,int distant){
		if(end-start<0){
			start-=distant;
		}
		return start;
	}
	// MouseListener接口定义的方法，处理鼠标单击事件。
	public void mouseClicked(MouseEvent e) {
		actionClicked(e.getX(),e.getY());
	}
	public void actionClicked(int x,int y) {
		switch(command){
			case Command.SELECT:
				currentX =  x;
				currentY =  y;
				float j1 = 8;
				int pb = -1;
				pb = PointSelect(currentX,currentY,j1);
				if(pb != -1)
				{
					AddSelectList(pb);
				}
				break;
			case Command.TEXTINSERT:
				Point aPoint = new Point(x, y);
				Font f = new Font("", 0, 10);
				TextInput ti = new TextInput(pd, true, null);
				if (ti.showDialog(pd, null, f) == 0) {
					CusString aCusShape = new CusString(ti.str, (int) aPoint.getX(),
							(int) aPoint.getY());
					aCusShape.setFont(ti.aFont);
					aCusShape.setColor(this.fgColor);
					Shapes.add(aCusShape);
					Graphics g=getGraphics();
					paint(g);
				}
				break;
		}
	}
	// MouseListener接口定义的方法，处理鼠标按下事件。
	public void mousePressed(MouseEvent e) {
		ServerAction.sendData(new DataPackage(DataPackage.DataType.POINT,new com.rock.drawboard.module.Point(e.getX(),e.getY(),PaintBoard.ACTION_DOWN)));
		actionDown(e.getX(),e.getY());
	}
	public void actionDown(int x,int y) {
		// 设置鼠标起始位置
		beginX = x;
		beginY = y;
		statusTextArea.setText("The position of cursor:  x=" + currentX + ", y=" + currentY + "\n The current draw tool: " + getCommandString(command));
		if(command == Command.PENCIL){
			pencil = new Pencil(this.fgColor, this.bgColor,this.strokeIndex,  x, y);
		}
		// 设置bMousePressing为真，表示鼠标按下了。
		bMousePressing = true;
	}
	//MouseListener接口定义的方法，处理鼠标松开事件。
	public void mouseReleased(MouseEvent e) {
		ServerAction.sendData(new DataPackage(DataPackage.DataType.POINT,new com.rock.drawboard.module.Point(e.getX(),e.getY(),PaintBoard.ACTION_UP)));
		actionUp(e.getX(),e.getY());
	}
	public void actionUp(int x,int y) {
		// 获取鼠标当前位置
		currentX = x;
		currentY = y;
		statusTextArea.setText("The position of cursor:  x=" + currentX + ", y=" + currentY + "\n The current draw tool: " + getCommandString(command));
		// 设置鼠标已经松开了。
		bMousePressing = false;

		// 松开鼠标时，将刚刚画的图形保存到vShapes中
		switch (command) {
			case Command.LINE:
				// 新建图形

				Line line = new Line(fgColor,brushColor,strokeIndex,beginX,beginY,currentX,currentY);
				Shapes.add(line);
				break;
			case Command.PENCIL:

				pencil.setPoints(x, y);
				Shapes.add(pencil);
				pencil = null;
				break;
			case Command.RECTANGLE:
				Rectangle rectangle = null;
				if (currentX < beginX) {
					if (currentY < beginY) {

						rectangle = new Rectangle(fgColor,brushColor,strokeIndex,beginX,beginY,beginX - currentX,beginY - currentY, fill);
					} else {

						rectangle = new Rectangle(fgColor,brushColor,strokeIndex,currentX,beginY,beginX - currentX,currentY - beginY, fill);
					}
				} else {
					if (currentY < beginY) {

						rectangle = new Rectangle(fgColor,brushColor,strokeIndex,beginX,currentY,currentX - beginX,beginY - currentY, fill);
					} else {

						rectangle = new Rectangle(fgColor,brushColor,strokeIndex,beginX,beginY,currentX - beginX,currentY - beginY, fill);
					}
				}
				Shapes.add(rectangle);
				break;
			case Command.CIRCLE:
				int radius = (int) Math.sqrt((beginX - currentX)
						* (beginX - currentX) + (beginY - currentY) * (beginY - currentY));

				Circle circle = new Circle(fgColor,brushColor,strokeIndex,beginX - radius / 2,beginY - radius / 2, radius, fill);

				Shapes.add(circle);
				break;
		} //End switch(command)

		repaint();
	}
	//MouseListener接口定义的方法，处理鼠标移入事件。
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}
	
	//MouseListener接口定义的方法，处理鼠标移出事件。
	public void mouseExited(MouseEvent e) {
		// do nothing
	}
	
	//MouseListener接口定义的方法，处理鼠标按住不放拖动事件。
	public void mouseDragged(MouseEvent e) {
		ServerAction.sendData(new DataPackage(DataPackage.DataType.POINT,new com.rock.drawboard.module.Point(e.getX(),e.getY(),PaintBoard.ACTION_MOVE)));
		actionMove(e.getX(),e.getY());
	}
	public void actionMove(int x,int y) {
		// 按住鼠标拖动时，不断的获取鼠标当前位置，并重画画板
		PastX = currentX;
		PastY = currentY;
		currentX = x;
		currentY = y;
		statusTextArea.setText("The position of cursor:  x=" + currentX + ", y=" + currentY + "\n The current draw tool: " + getCommandString(command));
		if(command == Command.PENCIL){
			pencil.setPoints(x, y);
		}
		Graphics g=getGraphics();
		//paint(g);
		repaint();
	}
	  public void SaveJpg() {
		  	size = getSize();
		  	m_wScreen = size.width;
			m_hScreen = size.height;
		  	offScreenImg = new BufferedImage(m_wScreen, m_hScreen, BufferedImage.TYPE_INT_RGB);  
	        offScreenG = offScreenImg.createGraphics();
	        offScreenG.setColor(this.bgColor);
		    offScreenG.fillRect(0, 0, this.getWidth(), this.getHeight());
		    int i = 0;
		    Shape shape = null;
		    for (;i < Shapes.size();i++)
		    {
		    	shape = (Shape)Shapes.get(i);
		    	shape.draw(offScreenG,0, bgColor);
		    }
		  
		  }
	  
	  
	  
	
	//MouseListener接口定义的方法，处理鼠标移动事件。
	public void mouseMoved(MouseEvent e) {
		// do nothing
	}




}

package com.rock.drawboard.board;

import com.rock.drawboard.Painter.Command;
import com.rock.drawboard.Painter.ImageToJpeg;
import com.rock.drawboard.module.*;
import com.rock.drawboard.module.Point;
import com.rock.drawboard.socket.ServerAction;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;


public class PaintBoard extends JFrame implements ServerAction.OnEventListener {

	private final ServerAction serviceAction;
	private JFileChooser filechooser1;
    private JFileChooser filechooser2;
	
	/********菜单栏、菜单和菜单项*********/
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("文件");
	JMenu editMenu = new JMenu("编辑");	
	JMenu imageMenu = new JMenu("图像");
	JMenu viewMenu = new JMenu("查看");
	JMenu filterMenu = new JMenu("滤镜");
	JMenu helpMenu = new JMenu("帮助");
	JMenuItem newMenuItem = new JMenuItem("新建");
	JMenuItem openMenuItem = new JMenuItem("打开");
	JMenuItem saveMenuItem = new JMenuItem("保存");
	JMenuItem saveAsMenuItem = new JMenuItem("另存为JPG");
	JMenuItem closeMenuItem = new JMenuItem("关闭");
	JMenuItem exitMenuItem = new JMenuItem("退出");
	
	JMenuItem undoMenuItem = new JMenuItem("撤销");
	JMenuItem redoMenuItem = new JMenuItem("恢复");
	JMenuItem cutMenuItem = new JMenuItem("剪切");
	JMenuItem copyMenuItem = new JMenuItem("复制");
	JMenuItem pasteMenuItem = new JMenuItem("粘贴");
	JMenuItem clearMenuItem = new JMenuItem("清除");
	JMenuItem selectAllMenuItem = new JMenuItem("全选");
	
	JMenuItem lineMenuItem = new JMenuItem("直线");
	JMenuItem circleMenuItem = new JMenuItem("圆");
	JMenuItem ovalMenuItem = new JMenuItem("椭圆");
	JMenuItem undodMenuItem = new JMenuItem("圆弧");
	JMenuItem rectangleMenuItem = new JMenuItem("矩形");
	JMenuItem eraserMenuItem = new JMenuItem("橡皮");
	JMenuItem selectedMenuItem = new JMenuItem("选中");
	JMenuItem unSelectedMenuItem = new JMenuItem("撤消选中");
	JMenuItem deletedMenuItem = new JMenuItem("删除");
	JMenuItem textInsertyMenuItem = new JMenuItem("文本插入");


	JMenuItem viewToolBarMenuItem = new JMenuItem("工具箱");
	JMenuItem viewColorPaletteMenuItem = new JMenuItem("调色板");
	JMenuItem viewStatusBarMenuItem = new JMenuItem("状态栏");
	JMenuItem stopFlashMenuItem = new JMenuItem("停止Flash");
	JMenuItem startFlashMenuItem = new JMenuItem("开始Flash");
		
	JMenuItem reverseColorFilterMenuItem = new JMenuItem("反色");
	JMenuItem rotateFilterMenuItem = new JMenuItem("旋转");
	JMenuItem blurFilterMenuItem = new JMenuItem("模糊");
		
	JMenuItem helpMenuItem = new JMenuItem("Help");
	
	/********画布****/
	//用多个画布存放多个图形文件，放在JScrollPane里
	//而JScrollPane放在JTabbedPane中，这就是一个多页面的布局
	
	//多个画布，每个画布显示一个图形
	MyCanvas[] canvases = new MyCanvas[10];
	//显示鼠标位置和当前绘制图形的状态栏
	JTextArea statusTextArea = new JTextArea();
	JScrollPane[] canvasScrollPanes = new JScrollPane[10];
	JScrollPane statusScrollPane;
	//多个文本域放在不同tab里
	JTabbedPane canvasTabbedPane = new JTabbedPane();
	//画布属性
	int x = -1, y = -1, eraser, clear;	
	Color pencilColor;

	
	
	/**********工具栏以及工具栏上的按钮**********/
	JToolBar editToolBar = new JToolBar();
	GridBagConstraints gridBagConstraints  = new GridBagConstraints();;
	JPanel ctrlPanel = new JPanel();
	JPanel imageToolBar = new JPanel();
	JPanel colorPanel = new JPanel();
	JPanel sizePanel = new JPanel();
	JPanel savePanel = new JPanel();
	JLabel copyleft = new JLabel();
	JPanel mediumPanel1 = new JPanel();
	JPanel brColorPanel = new JPanel(); 
	
    ButtonGroup toolsGroup = new ButtonGroup();
    JButton fgButton = new JButton();
    JButton bgButton = new JButton();
    JButton brButton = new JButton();
    JComboBox weightCombo = new JComboBox();
    JComboBox eraserCombo = new JComboBox();
    JRadioButton filledButton = new JRadioButton("Fill",false);
    JRadioButton emptyButton = new JRadioButton("Empty",true);
    JPanel fillPanel = new JPanel();
    boolean fill = false;
  
  
	JButton openButton = new JButton(new ImageIcon(loadImage("image/open.gif")));
	JButton newButton = new JButton(new ImageIcon(loadImage("image/new.gif")));
	JButton saveButton = new JButton(new ImageIcon(loadImage("image/save.gif")));
	JButton helpButton = new JButton(new ImageIcon(loadImage("image/help.gif")));
	JButton exitButton = new JButton(new ImageIcon(loadImage("image/close.gif")));
	JButton copyButton = new JButton(new ImageIcon(loadImage("image/copy.gif")));
	JButton cutButton = new JButton(new ImageIcon(loadImage("image/cut.gif")));
	JButton pasteButton = new JButton(new ImageIcon(loadImage("image/paste.gif")));

	
	JToggleButton eraserButton = new JToggleButton(new ImageIcon(loadImage("image/EraserTool.png")));



	JToggleButton lineButton = new JToggleButton(new ImageIcon(loadImage("image/LineTool.png")));
    JToggleButton circleButton = new JToggleButton(new ImageIcon(loadImage("image/CircleTool.png")));     
    JToggleButton rectangleButton = new JToggleButton(new ImageIcon(loadImage("image/RectangleTool.gif")));
    JToggleButton pencilButton = new JToggleButton(new ImageIcon(loadImage("image/PencilTool.gif")));
    JToggleButton selectedButton = new JToggleButton(new ImageIcon(loadImage("image/SelectAreaTool.png")));
	JToggleButton unSelectedButton = new JToggleButton(new ImageIcon(loadImage("image/4.GIF")));
    JToggleButton deletedButton = new JToggleButton(new ImageIcon(loadImage("image/3.GIF")));
    

	
	//该文本域显示当前光标在当前 画板中的坐标及画图状态
    JTextArea showStatus = new JTextArea();
	//对话框窗体，程序中所有对话框都显示在该窗体中
    JFrame dialogFrame = new JFrame();
    
	/*******组件之间的分隔栏******/
	JSplitPane leftCenterSplitPane;
	JSplitPane toolFlashSplitPane;
	JSplitPane tabbedStatusSplitPane;
	
	/**********文件选择、存储相关********/
	//文件过滤器
	Filter fileFilter = new Filter();
	//文件选择器
	//FileChooser fileChooser = new FileChooser();
	// 文件读写控制，0表示文件选择器读文件，1文件选择器标示写文件
	int fileChooser_control = 0;
	FileWriter fileWriter;
	
	// tabbedPane中tab页的当前数量
	int tb = 1;
	int find_control = 0;
	//画板的控制器，指向当前操作的画板
	int canvas_control = 0;
	//当前画板的图形
	Image currentImageInCanvas;

	//标志文件是否为新建的，如果是新建的文件，为true
	boolean[] newFileFlags = new boolean[10];
	//存放打开文件所在的目录
	String[] directory = new String[10];
	
    
	/********用于显示Flash的控制器****/
	JLabel flashLabel = new JLabel(new ImageIcon(loadImage("image/Juggler0.gif")));
	Timer timer = new Timer(100, new Act_timer());
	int timerControl = 0;
	
	
	/********帮助相关****/
	Font font = new Font("Courier", Font.TRUETYPE_FONT, 14);
	JTextArea helpTextArea = new JTextArea();
	JFrame helpFrame = new JFrame("Help");
	
	public PaintBoard(){
		super("画板");
		SplashWindow splash = new SplashWindow("jtable.gif", this, 1000);
		//为窗体添加键盘事件处理器
		//下面这一行非常重要，表示窗体能够接受焦点。
		//如果没有这一句，按键盘会无效。
		this.setFocusable(true);
		this.addKeyListener(new MyKeyListener());
		
		//为窗体添加窗口事件处理器
		this.addWindowListener(new WindowListener());
		
		//初始化
		init();
		//setLocation(200, 200);
		this.setBounds(140, 140, 1200, 700);
		setResizable(false);
		setVisible(true);
		pack();
		//初始时启动动画
		timer.start();
		canvases[canvas_control].setCommand(Command.PENCIL);
		serviceAction = new ServerAction();
		serviceAction.start();
		serviceAction.setOnEventListener(this);
	}
	
	private void init(){
		/*******初始化画板、目录*********/
		for(int i=0; i<10; i++){
			newFileFlags[i] = true;
			//设置画板的背景和前景颜色
			canvases[i] = new MyCanvas(statusTextArea, this);
			canvases[i].setBackground(Color.WHITE);
			canvases[i].setForeground(Color.BLACK);
			// 设置初始的命令为画线，将被选中的命令按钮的前景色用红色标示
			lineButton.setForeground(Color.red);
			canvases[canvas_control].setCommand(Command.PENCIL);
			// 为文本域键盘设置事件处理器
			canvases[i].addKeyListener(new MyKeyListener());
			canvasScrollPanes[i] = new JScrollPane(canvases[i],
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			Graphics g=getGraphics();
			canvases[i].paint(g);
		}
		
		
		//初始化显示当前鼠标位置的文本域
		statusScrollPane = new JScrollPane(statusTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		statusTextArea.setEnabled(false);
		statusTextArea.setFont(font);
		statusTextArea.setBackground(new Color(70, 80, 91));
		statusTextArea.setDisabledTextColor(Color.yellow);

		/**************初始化菜单*************/
		//初始化菜单项
		newMenuItem.addActionListener(new Act_NewFile());
		openMenuItem.addActionListener(new Act_OpenFile());
		saveMenuItem.addActionListener(new Act_SaveFile());
		saveAsMenuItem.addActionListener(new Act_SaveAs());
		exitMenuItem.addActionListener(new Act_ExitEditor());
		helpMenuItem.addActionListener(new Act_Help());
		stopFlashMenuItem.addActionListener(new Act_StopFlash());
		startFlashMenuItem.addActionListener(new Act_StartFlash());
		startFlashMenuItem.setEnabled(false);
		undoMenuItem.addActionListener(new Act_UndoAs());
		redoMenuItem.addActionListener(new Act_Redo()); 
		clearMenuItem.addActionListener(new Act_Clear());
		lineMenuItem.addActionListener(new Act_Line()); 
		rectangleMenuItem.addActionListener(new Act_Rectangle()); 		
		eraserMenuItem.addActionListener(new Act_Eraser()); 
		circleMenuItem.addActionListener(new Act_Circle());
		selectedMenuItem.addActionListener(new Act_Selected()); 
		unSelectedMenuItem.addActionListener(new Act_UnSelected()); 
		deletedMenuItem.addActionListener(new Act_Deleted());
		textInsertyMenuItem.addActionListener(new Act_TextInsert()); 

		viewColorPaletteMenuItem.addActionListener(new Act_Palette()); 
		stopFlashMenuItem.addActionListener(new Act_StopFlash()); 
		startFlashMenuItem.addActionListener(new Act_StartFlash()); 

		
		//初始化菜单
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);	
		fileMenu.add(exitMenuItem);
		fileMenu.add(exitMenuItem);
		
		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);
		editMenu.add(clearMenuItem);
		helpMenu.add(helpMenuItem);

		
		imageMenu.add(lineMenuItem);
		imageMenu.add(circleMenuItem);
		imageMenu.add(rectangleMenuItem);
		imageMenu.add(eraserMenuItem);
		imageMenu.add(selectedMenuItem);
		imageMenu.add(unSelectedMenuItem);
		imageMenu.add(deletedMenuItem);
		imageMenu.add(textInsertyMenuItem);

		viewMenu.add(viewColorPaletteMenuItem);
		viewMenu.add(stopFlashMenuItem);
		viewMenu.add(startFlashMenuItem);
		
		filterMenu.add(reverseColorFilterMenuItem );
		filterMenu.add(rotateFilterMenuItem );
		filterMenu.add(blurFilterMenuItem );
		
		//初始化菜单栏
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(imageMenu);
		menuBar.add(viewMenu);	
		menuBar.add(filterMenu);
		menuBar.add(helpMenu);
		//将菜单栏添加到窗体中
		setJMenuBar(menuBar);
		
		/***********初始化工具栏以及按钮**********/
		//初始化按钮
		newButton.addActionListener(new Act_NewFile());
		openButton.addActionListener(new Act_OpenFile());
		saveButton.addActionListener(new Act_SaveFile());
		exitButton.addActionListener(new Act_ExitEditor());		
		helpButton.addActionListener(new Act_Help());
		eraserButton.addActionListener(new Act_Eraser());

		lineButton.addActionListener(new Act_Line());
		circleButton.addActionListener(new Act_Circle());
		rectangleButton.addActionListener(new Act_Rectangle());
		pencilButton.addActionListener(new Act_Pencil());
		selectedButton.addActionListener(new Act_Selected());
		unSelectedButton.addActionListener(new Act_UnSelected());
		deletedButton.addActionListener(new Act_Deleted());
		// 为工具栏设置提示信息，当鼠标在工具栏按钮上停留一段时间时，会显示提示信息
		newButton.setToolTipText("New");
		openButton.setToolTipText("Open");
		saveButton.setToolTipText("Save");
		exitButton.setToolTipText("Exit");
		helpButton.setToolTipText("Help");	
		copyButton.setToolTipText("Copy");
		cutButton.setToolTipText("Cut");
		pasteButton.setToolTipText("Paste");
		eraserButton.setToolTipText("Eraser");
		lineButton.setToolTipText("Line");
		circleButton.setToolTipText("Circle");
		rectangleButton.setToolTipText("Rectangle");
		pencilButton.setToolTipText("Pencil");
		selectedButton.setToolTipText("Seleted");	
		unSelectedButton.setToolTipText("UnSelected");
		deletedButton.setToolTipText("Deleted");
		toolsGroup.add(eraserButton);
		toolsGroup.add(lineButton);
		toolsGroup.add(circleButton);
		toolsGroup.add(rectangleButton);
		toolsGroup.add(pencilButton);
		toolsGroup.add(selectedButton);
		toolsGroup.add(unSelectedButton);
		toolsGroup.add(deletedButton);
	
		
		newButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		openButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		saveButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		exitButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		helpButton.setBorder(new BevelBorder(BevelBorder.RAISED));		
		cutButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		copyButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		pasteButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		eraserButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		lineButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		circleButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		rectangleButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		pencilButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		selectedButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		unSelectedButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		deletedButton.setBorder(new BevelBorder(BevelBorder.RAISED));
				
		//初始化工具栏
		setTitle("画板");
		editToolBar.add(newButton);
		editToolBar.add(openButton);
		editToolBar.add(saveButton);
		editToolBar.add(exitButton);
		editToolBar.add(helpButton);
		/*editToolBar.add(copyButton);
		editToolBar.add(cutButton);
		editToolBar.add(pasteButton);*/	
		
		
		imageToolBar.setLayout(new GridLayout(3, 3, 5, 5));
		imageToolBar.add(lineButton);
		imageToolBar.add(circleButton);	
		imageToolBar.add(rectangleButton);
		imageToolBar.add(pencilButton);
		imageToolBar.add(eraserButton);
		imageToolBar.add(selectedButton);
		imageToolBar.add(unSelectedButton);
		imageToolBar.add(deletedButton);
		//初始化控制栏
		ctrlPanel.setLayout(new GridBagLayout());
        ctrlPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        mediumPanel1.setLayout(new BoxLayout(mediumPanel1, BoxLayout.Y_AXIS));

        imageToolBar.setBorder(new TitledBorder("Drawing Tools"));
        mediumPanel1.add(imageToolBar);

        colorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));

        colorPanel.setBorder(new TitledBorder("Color Settings"));
        fgButton.setBackground(canvases[0].getForeground());
        fgButton.setToolTipText("Change Drawing Color");
        fgButton.setBorder(new LineBorder(new Color(0, 0, 0)));
        fgButton.setPreferredSize(new Dimension(30, 30));
        fgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fgButtonActionPerformed(evt);
            }
        });

        colorPanel.add(fgButton);

        bgButton.setBackground(canvases[0].getBackground());
        bgButton.setToolTipText("Change Board Background Color");
        bgButton.setBorder(new LineBorder(new Color(0, 0, 0)));
        bgButton.setPreferredSize(new Dimension(30, 30));
        bgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bgButtonActionPerformed(evt);
            }
        });

        colorPanel.add(bgButton);
        mediumPanel1.add(colorPanel);

        brButton.setBackground(Color.BLACK);
        brButton.setText("Brush Color");
        brButton.setToolTipText("Change Brush Background Color");
        brButton.setBorder(new LineBorder(new Color(0, 0, 0)));
        brButton.setPreferredSize(new Dimension(100, 25));
        brButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                brButtonActionPerformed(evt);
            }
        });

        brColorPanel.setBorder(new TitledBorder("Brush Color Settings"));
        brColorPanel.add(brButton);
        mediumPanel1.add(brColorPanel);

        sizePanel.setLayout(new BorderLayout(0, 3));

        weightCombo.setFont(new Font("Dialog", 0, 10));
        weightCombo.setModel(new DefaultComboBoxModel(new String[] { "Stroke Weight 5px", "Stroke Weight 8px", "Stroke Weight 10px", "Stroke Weight 15px", "Stroke Weight 20px" }));
        weightCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                weightComboActionPerformed(evt);
            }
        });

        sizePanel.setBorder(new TitledBorder("Size Setttings"));
        sizePanel.add(weightCombo, BorderLayout.NORTH);

        eraserCombo.setFont(new Font("Dialog", 0, 10));
        eraserCombo.setModel(new DefaultComboBoxModel(new String[] { "Eraser Size 15px", "Eraser Size 20px", "Eraser Size 30px", "Eraser Size 50px", "Eraser Size 100px" }));
        eraserCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                eraserComboActionPerformed(evt);
            }
        });

        sizePanel.add(eraserCombo, BorderLayout.SOUTH);
        mediumPanel1.add(sizePanel);


        fillPanel.setLayout(new FlowLayout(BoxLayout.Y_AXIS, 4, 4));
        fillPanel.setBorder(new TitledBorder("Fill Settings"));
        //创建当选钮将其增加到按钮组
        //为每个单选按钮指定一个事件监听这者
        ButtonGroup radioGroup = new ButtonGroup();
        emptyButton.addChangeListener(new Item_FillChanged());
        filledButton.addChangeListener(new Item_FillChanged());
        radioGroup.add(emptyButton);
        radioGroup.add(filledButton);
        fillPanel.add(emptyButton);
        fillPanel.add(filledButton);
        mediumPanel1.add(fillPanel);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 5, 5, 5);
        ctrlPanel.add(mediumPanel1, gridBagConstraints);


        copyleft.setFont(new Font("Verdana", 0, 10));
        copyleft.setForeground(new Color(255, 153, 0));
        copyleft.setText("CopyLeft 2007 NJUPT");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 15, 5);
        ctrlPanel.add(copyleft, gridBagConstraints);

		/********初始化tab页面板和组件间的分隔栏*********/
		canvasTabbedPane.addTab("Image1", canvasScrollPanes[0]);
		canvasTabbedPane.addChangeListener(new Act_ChangeTab());
		//文件目录树和Flash之间的分隔栏
		toolFlashSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, flashLabel, ctrlPanel);
		//文件文本域面板与指示光标所在行文本域面板之间的分隔栏
		tabbedStatusSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, canvasTabbedPane, statusScrollPane);
		//左边大面板与右边大面板之间的分隔栏
		leftCenterSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, toolFlashSplitPane, tabbedStatusSplitPane);
		// 设置分隔栏两边面板显示的宽度，如果分割条是左右分割，则参数表示分割条的横坐标
		// 如果分割条是上下分割，则参数表示分割条的纵坐标
		leftCenterSplitPane.setDividerLocation(150);
		tabbedStatusSplitPane.setDividerLocation(460);
		toolFlashSplitPane.setDividerLocation(120);


		//初始化帮助
		initHelp();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(editToolBar, BorderLayout.NORTH);
		this.getContentPane().add(leftCenterSplitPane);

	}

	/**
	 * 初始化帮助信息
	 */
	private void initHelp(){

		//帮助信息主要显示菜单的快捷方式

		// field存放JTable的表头信息，即表的标题
		String[] field = { "MenuItem", "ShortCut Key" };
		// data存放JTable的数据。
		Object[][] data = { { "     New           ", "    Ctrl+N    " },
				{ "    Open          ", "    F12       " },
				{ "    Save          ", "    Ctrl+S    " },
				{ "    Exit          ", "    Ctrl+X    " },
				{ "    Help          ", "    Ctrl+H    " }, };
		// 用表头和数据构造一个表
		JTable help_Table = new JTable(data, field);
		help_Table.setFont(font);
		//不可编辑帮助信息表
		help_Table.setEnabled(false);
		// 为表和文本域设置背景和前景颜色
		helpTextArea.setFont(new Font("Courier", Font.TRUETYPE_FONT, 16));
		helpFrame.getContentPane().setLayout(new BorderLayout());
		help_Table.setForeground(Color.pink);
		helpTextArea.setForeground(Color.pink);
		help_Table.setBackground(new Color(70, 80, 91));
		help_Table.setSelectionBackground(new Color(70, 80, 91));
		helpTextArea.setBackground(new Color(70, 80, 91));
		helpTextArea.setText(" If you want to use this software with all functions,\n"
						+ "     You must do the things following:\n"
						+ "     1: install jdk_1.3 or Higher than it ;\n"
						+ "     2: set your classpath and path correctly;\n"
						+ "     3: if you want to use the compile and build functions,\n"
						+ "       you should save your edited File in the save directory\n"
						+ "       with  this software.\n ");
		// 将文本域和表加到窗体中
		helpFrame.getContentPane().add(
				new JScrollPane(help_Table,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		helpFrame.getContentPane().add(
				new JScrollPane(helpTextArea,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.NORTH);

	}



	//退出画板
	private void exitPaintBoard(){
		if(JOptionPane.showConfirmDialog(this, "你确定退出画板？", "退出", JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION){
			// 如果选择YES，则退出。
			//dispose方法用于释放资源
			//释放由此 Window、其子组件及其拥有的所有子组件所使用的所有本机屏幕资源。
			//即这些 Component 的资源将被破坏，它们使用的所有内存都将返回到操作系统，并将它们标记为不可显示。
			//通过随后对 pack 或 show 的调用重新构造本机资源，可以再次显示 Window 及其子组件。
			//重新创建的 Window 及其子组件的状态在移除 Window 的点上与这些对象的状态将是一样的（不考虑这些操作之间的其他更改）。
			dispose();
			System.exit(0);
		}
	}

	//设置画笔常量
	private static final int STROKE_PEN = 12;       //画笔1
	private static final int STROKE_ERASER = 2;    //橡皮擦2
	private static final int STROKE_RECT = 9;      //矩形 4
	private static final int STROKE_CIRCLE = 8;    //圆 5
	private static final int STROKE_LINE = 6;      //直线7
	public static final int REDO = 201;  //redo
	public static final int UNDO = 202;  //undo
	public static final int CLEAR_BOARD = 203;  //清空面板
	public static final int ACTION_DOWN = 301;
	public static final int ACTION_MOVE = 302;
	public static final int ACTION_UP = 303;
	@Override
	public void onPencil(com.rock.drawboard.module.Point point) {
		switch (point.getState()) {
			case ACTION_DOWN:
				canvases[canvas_control].actionDown(point.getX(),point.getY());
				break;
			case ACTION_MOVE:
				canvases[canvas_control].actionMove(point.getX(),point.getY());
				break;
			case ACTION_UP:
				canvases[canvas_control].actionUp(point.getX(),point.getY());
				break;
		}
	}

	@Override
	public void onCommand(com.rock.drawboard.module.Command command) {
		switch (command.getType()) {
			case STROKE_PEN:
				canvases[canvas_control].setCommandToAndroid(Command.PENCIL);
				break;
			case STROKE_ERASER:
				canvases[canvas_control].setCommandToAndroid(Command.ERASER);
				break;
			case STROKE_CIRCLE:
				canvases[canvas_control].setCommandToAndroid(Command.CIRCLE);
				break;
			case STROKE_LINE:
				canvases[canvas_control].setCommandToAndroid(Command.LINE);
				break;
			case STROKE_RECT:
				canvases[canvas_control].setCommandToAndroid(Command.RECTANGLE);
				break;
			case REDO:
				canvases[canvas_control].redo();
				break;
			case UNDO:
				canvases[canvas_control].undo();
				break;
			case CLEAR_BOARD:
				canvases[canvas_control].clearBoard();
				break;
		}
	}

	@Override
	public void onColor(SelectColor color) {

		canvases[canvas_control].setForegroundColor(new Color(color.getR(),color.getG(),color.getB()));
	}

	@Override
	public void onStroke(StrokeWidth strokeWidth) {
		if(strokeWidth.getType()==STROKE_PEN) {
			canvases[canvas_control].setStrokeIndex(strokeWidth.getStroke());
		}else {
			canvases[canvas_control].setEraserIndex(strokeWidth.getStroke());
		}
	}

	/**
	 * 键盘事件处理器
	 */
	public class MyKeyListener extends KeyAdapter{
		//覆盖父类的keyPressed方法，处理键被按下时的事件。
		public void keyPressed(KeyEvent e){
			//按F12打开文件
			if(e.getKeyCode() == KeyEvent.VK_F12){
				(new Act_OpenFile()).actionPerformed(null);
			}
			// 按Ctrl加S键保存文件
			else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S){
				(new Act_SaveFile()).actionPerformed(null);
			}
			// 按Ctrl加N新建文件
			else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N){
				(new Act_NewFile()).actionPerformed(null);
			}
			// 按Ctrl加E退出编辑器
			else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_E){
				(new Act_ExitEditor()).actionPerformed(null);
			}
			// 按Ctrl加H显示帮助
			else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_H){
				(new Act_Help()).actionPerformed(null);
			}
		}
	}

	/**
	 * 窗口事件侦听器
	 */
	public class WindowListener extends WindowAdapter{
		//处理关闭窗口事件
		public void windowClosing(WindowEvent e){
			dispose();
			System.exit(0);
		}
	}

	/**
	 * 切换tab页事件
	 */
	class Act_ChangeTab implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			//切换tab页时，更新canvas_control的值。
			canvas_control = canvasTabbedPane.getSelectedIndex();
		}
	}

	/**
	 * 打开文件事件
	 */
	class Act_OpenFile implements ActionListener{
		public void actionPerformed(ActionEvent e_ji1){
			//打开已有文件
			 if (filechooser1 == null) {
	                filechooser1 = new JFileChooser();
	                filechooser1.setFileFilter(fileFilter);
	                filechooser1.setMultiSelectionEnabled(false);
	                filechooser1.setAcceptAllFileFilterUsed(false);
	                filechooser1.setCurrentDirectory(new File("./"));
	            }
	            int retVal = filechooser1.showOpenDialog(canvases[canvas_control]);
	            if (retVal != JFileChooser.APPROVE_OPTION)
	                return;
	            //获取待打开的文件名
				String filename = filechooser1.getSelectedFile().getName();
				//新建一个tab页，用于装新打开的文件
				canvasTabbedPane.addTab(filename, canvasScrollPanes[tb]);
				canvasTabbedPane.setSelectedIndex(tb);
				//将当前文本域设置到新打开的文件上
				canvas_control = tb;
				tb++;
				//获取待打开的文件所在的目录，将目录保存至数组，这样在保存文件的时候，能够将文件名保存到目录中
				directory[canvas_control] = filechooser1.getCurrentDirectory().toString();
				try {
					//将文件内容显示到画板中
					canvases[canvas_control].OpenFile(directory[canvas_control] + "/" + filename);
				} catch (Exception e_open) {
					JOptionPane.showMessageDialog(dialogFrame.getContentPane(), "读取发生错误");
				}
		}
	}

	/**
	 * 新建文件事件
	 */
	class Act_NewFile implements ActionListener{
		public void actionPerformed(ActionEvent e_ji10){
			// 切换tab页时，更新canvas_control的值。
			canvasTabbedPane.addTab("Image" + (tb + 1), canvasScrollPanes[tb]);
			canvasTabbedPane.setSelectedIndex(tb);
			canvas_control = tb;
			tb++;
		}
	}

	/**
	 * 保存文件事件
	 */
	class Act_SaveFile implements ActionListener{
		public void actionPerformed(ActionEvent e_ji2){
			 if (filechooser1 == null) {
	                filechooser1 = new JFileChooser();
	                filechooser1.setFileFilter(fileFilter);
	                filechooser1.setMultiSelectionEnabled(false);
	                filechooser1.setAcceptAllFileFilterUsed(false);
	                filechooser1.setCurrentDirectory(new File("./"));
	            }
	            int retVal = filechooser1.showSaveDialog(canvases[canvas_control]);
	            if (retVal != JFileChooser.APPROVE_OPTION)
	                return;
	            File file = filechooser1.getSelectedFile();
	            if (!file.getName().toLowerCase().endsWith(".jpg")) {
	            	//获取待打开的文件名
	    			String filename = filechooser1.getSelectedFile().getName();
	    			canvasTabbedPane.setTitleAt(tb-1, filename);
	    			filename = filename+ ".jpg";
	    			//获取待打开的文件所在的目录，将目录保存至数组，这样在保存文件的时候，能够将文件名保存到目录中
	    			directory[canvas_control] = filechooser1.getCurrentDirectory().toString();
	    			try {
	    				canvases[canvas_control].SaveFile(directory[canvas_control] + "/" + filename);
	    				canvases[canvas_control].SaveJpg();
	    				ImageToJpeg.toJpeg(new File("ss.jpg"), canvases[canvas_control].offScreenImg);
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	            }

		}
	}

	/**
	 * 退出画板事件
	 */
	class Act_ExitEditor implements ActionListener{
		public void actionPerformed(ActionEvent e_ji3){
			//退出画板
			exitPaintBoard();
		}
	}

	/**
	 * 显示帮助Help事件
	 */
	class Act_Help implements ActionListener{
		public void actionPerformed(ActionEvent e_ji9){
			helpFrame.pack();
			helpFrame.setVisible(true);
			helpFrame.requestFocus();
			helpFrame.setLocation(200, 0);
		}
	}

	//恢复
	class Act_Redo implements ActionListener {
		public void actionPerformed(ActionEvent e_ji10) {
			canvases[canvas_control].redo();
		}
	}

	//撤销
	class Act_UndoAs implements ActionListener{
		public void actionPerformed(ActionEvent e_ji9){
			canvases[canvas_control].undo();
		}
	}



	class Act_Line implements ActionListener {
		public void actionPerformed(ActionEvent e_ji11) {
			lineButton.setSelected(true);
			canvases[canvas_control].setCommand(Command.LINE);
			statusTextArea.setText("The current draw tool: " + canvases[canvas_control].getCommandString(Command.LINE));
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(STROKE_LINE)));
		}
	}


	class Act_Circle implements ActionListener{
		public void actionPerformed(ActionEvent e_ji9){
			circleButton.setSelected(true);
			canvases[canvas_control].setCommand(Command.CIRCLE);
			canvases[canvas_control].fill = fill;
			statusTextArea.setText("The current draw tool: " + canvases[canvas_control].getCommandString(Command.CIRCLE));
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(STROKE_CIRCLE)));

		}
	}

	class Act_Rectangle implements ActionListener {
		public void actionPerformed(ActionEvent e_ji10) {
			rectangleButton.setSelected(true);
			canvases[canvas_control].setCommand(Command.RECTANGLE);
			canvases[canvas_control].fill = fill;
			statusTextArea.setText("The current draw tool: " + canvases[canvas_control].getCommandString(Command.RECTANGLE));
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(STROKE_RECT)));
		}
	}

	class Item_FillChanged implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			JRadioButton b = (JRadioButton)e.getSource();
			if(b.isSelected()){
				if(b.getText().equals("Fill")){
					fill = true;
					canvases[canvas_control].fill = true;
				}

				else{
					fill = false;
					canvases[canvas_control].fill = false;
				}

			}
		}
	}



	class Act_Eraser implements ActionListener{
		public void actionPerformed(ActionEvent e_ji9){
			eraserButton.setSelected(true);
			canvases[canvas_control].setCommand(Command.ERASER);
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(STROKE_ERASER)));
		}
	}



	/**
	 * 动画显示事件
	 */
	class Act_timer implements ActionListener {
		public void actionPerformed(ActionEvent e_time) {
			//Flash显示，一共就4个图片文件，按顺序显示
			if (timerControl > 4){
				timerControl = 0;
			}
			flashLabel.setIcon(new ImageIcon(loadImage("image/Juggler" + timerControl
					+ ".gif")));
			timerControl++;
		}
	}

	/**
	 * 停止动画事件
	 */
	class Act_StopFlash implements ActionListener {
		public void actionPerformed(ActionEvent E_stop) {
			//Flash控制。停止动画
			timer.stop();
			startFlashMenuItem.setEnabled(true);
			stopFlashMenuItem.setEnabled(false);
		}
	}

	/**
	 * 启动动画事件
	 */
	class Act_StartFlash implements ActionListener {
		public void actionPerformed(ActionEvent E_start) {
			//Flash控制。启动动画
			timer.start();
			startFlashMenuItem.setEnabled(false);
			stopFlashMenuItem.setEnabled(true);
		}
	}

	/**
	 * 文件过滤器，只支持编辑".jwd"文件
	 */
	class Filter extends FileFilter{
		//覆盖FileFilter的accept方法
		public boolean accept(File file1){
			 if (file1.isDirectory())
	                return true;
			 if (file1.getName().endsWith(".jpg"))
	                return true;
	            return false;
		}

		public String getDescription(){
			return ("*.jpg");
		}
	}

	//格式为jpg的文件过滤器
    private FileFilter jdrawFilter = new FileFilter() {
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            if (f.getName().endsWith(".jpg"))
                return true;
            return false;
        }
        public String getDescription() {
            return "(*.jpg)";
        }

    };


	/**
	 * 从jar包中读取图片文件
	 * @param name
	 * @return	返回一个图片对象
	 */
	private Image loadImage(String name) {
		try {
			java.net.URL url = getClass().getResource(name);
			//根据URL中内容新建一个图片文件
			return createImage((java.awt.image.ImageProducer) url.getContent());
		} catch (Exception ex) {
			return null;
		}
	}


	class Act_Selected implements ActionListener{
		public void actionPerformed(ActionEvent e){
			canvases[canvas_control].redo();
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(REDO)));
		}
	}

	class Act_UnSelected implements ActionListener{
		public void actionPerformed(ActionEvent e){
			canvases[canvas_control].undo();
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(UNDO)));
		}
	}


	class Act_Clear implements ActionListener{
		public void actionPerformed(ActionEvent e){
			canvases[canvas_control].clearBoard();
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(CLEAR_BOARD)));
		}
	}

	class Act_Pencil implements ActionListener{
		public void actionPerformed(ActionEvent e){
			canvases[canvas_control].setCommand(Command.PENCIL);
			statusTextArea.setText("The current draw tool: " + canvases[canvas_control].getCommandString(Command.PENCIL));
			ServerAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(STROKE_PEN)));
		}
	}

	class Act_Palette implements ActionListener{
		public void actionPerformed(ActionEvent e){
			Color tempColor = JColorChooser.showDialog(dialogFrame, "调色板", pencilColor);
			if(tempColor!=null){
				pencilColor = tempColor;
				canvases[canvas_control].setForegroundColor(pencilColor);
				fgButton.setBackground(pencilColor);
				serviceAction.sendData(new DataPackage(DataPackage.DataType.COLOR,new SelectColor(pencilColor.getRed(),pencilColor.getGreen(),pencilColor.getBlue())));
			}
		}
	}

	//插入文本
	class Act_TextInsert implements ActionListener {
		public void actionPerformed(ActionEvent e_ji11) {
			canvases[canvas_control].setCommand(Command.TEXTINSERT);
		}
	}


	class Act_Deleted implements ActionListener{
		public void actionPerformed(ActionEvent e){
			canvases[canvas_control].clearBoard();
			serviceAction.sendData(new DataPackage(DataPackage.DataType.COMMAND,new com.rock.drawboard.module.Command(CLEAR_BOARD)));
		}
	}

    private void fgButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_fgButtonActionPerformed
        Color color = JColorChooser.showDialog(this,
                "请选择绘制图形的颜色", canvases[canvas_control].getForeground());
        if (color != null) {
            canvases[canvas_control].setForeground(color);
            canvases[canvas_control].setForegroundColor(color);
            fgButton.setBackground(color);
        }
    }//GEN-LAST:event_fgButtonActionPerformed

    private void bgButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_bgButtonActionPerformed
        Color color = JColorChooser.showDialog(this,
                "请选择图形的背景色", canvases[canvas_control].getBackground());
        if (color != null) {
            canvases[canvas_control].setBackground(color);
            canvases[canvas_control].setBackgroundColor(color);
            bgButton.setBackground(color);
        }
    }//GEN-LAST:event_bgButtonActionPerformed

    private void brButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_bgButtonActionPerformed
        Color color = JColorChooser.showDialog(this,
                "Change Board Background Color", canvases[canvas_control].getBackground());
        if (color != null) {
            canvases[canvas_control].setBrushColor(color);
            brButton.setBackground(color);
        }
    }

    private void eraserComboActionPerformed(ActionEvent evt) {//GEN-FIRST:event_eraserComboActionPerformed
    	eraserButton.setSelected(true);
    	canvases[canvas_control].setEraserIndex(eraserCombo.getSelectedIndex());
		switch (weightCombo.getSelectedIndex()) {
			case 0:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(15,STROKE_ERASER)));
				break;
			case 1:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(20,STROKE_ERASER)));
				break;
			case 2:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(30,STROKE_ERASER)));
				break;
			case 3:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(50,STROKE_ERASER)));
				break;
			case 4:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(100,STROKE_ERASER)));
				break;
		}
    }//GEN-LAST:event_eraserComboActionPerformed

    private void weightComboActionPerformed(ActionEvent evt) {//GEN-FIRST:event_weightComboActionPerformed
        canvases[canvas_control].setStrokeIndex(weightCombo.getSelectedIndex());
        switch (weightCombo.getSelectedIndex()) {
			case 0:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(5,STROKE_PEN)));
				break;
			case 1:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(8,STROKE_PEN)));
				break;
			case 2:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(10,STROKE_PEN)));
				break;
			case 3:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(15,STROKE_PEN)));
				break;
			case 4:
				ServerAction.sendData(new DataPackage(DataPackage.DataType.STROKE,new StrokeWidth(20,STROKE_PEN)));
				break;
		}
    }//GEN-LAST:event_weightComboActionPerformed

    class Act_SaveAs implements ActionListener{
		public void actionPerformed(ActionEvent e_ji9){
			 if (filechooser2 == null) {
				 filechooser2 = new JFileChooser();
				 filechooser2.setFileFilter(jdrawFilter);
				 filechooser2.setMultiSelectionEnabled(false);
				 filechooser2.setAcceptAllFileFilterUsed(false);
				 filechooser2.setCurrentDirectory(new File("./"));
	            }
	            int retVal = filechooser2.showSaveDialog(canvases[canvas_control]);
	            if (retVal != JFileChooser.APPROVE_OPTION)
	                return;
	            File file = filechooser2.getSelectedFile();
	            if (!file.getName().toLowerCase().endsWith(".jpg")) {
	            	//获取待打开的文件名
	    			String filename = filechooser2.getSelectedFile().getName();
	    			canvasTabbedPane.setTitleAt(tb-1, filename);
	    			filename = filename+ ".jpg";
	    			//获取待打开的文件所在的目录，将目录保存至数组，这样在保存文件的时候，能够将文件名保存到目录中
	    			directory[canvas_control] = filechooser2.getCurrentDirectory().toString();
	    			try {	    				
	    				canvases[canvas_control].SaveJpg();
	    				ImageToJpeg.toJpeg(new File(directory[canvas_control] + "/" + filename), canvases[canvas_control].offScreenImg);
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	            }		
		}
	}


}

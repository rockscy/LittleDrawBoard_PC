package com.rock.drawboard.board;

/*
软件作者： 熊锡君，时守刚
软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class NwFontChooserS extends JDialog
{
  String[] styleList = new String[] {
      "Plain", "Bold", "Italic"};
  String[] sizeList = new String[] {
      "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
      "16", "17",
      "18", "19", "20", "22", "24", "27", "30", "34", "39", "45", "51", "60"};
  NwList StyleList;
  NwList FontList;
  NwList SizeList;
  static JLabel Sample = new JLabel();
  boolean ob = false;

  private NwFontChooserS(Frame parent, boolean modal, Font font) {
    super(parent, modal);
    initAll();
    setTitle("选择字体");
    if (font == null) font = Sample.getFont();
    FontList.setSelectedItem(font.getName());
    SizeList.setSelectedItem(font.getSize() + "");
    StyleList.setSelectedItem(styleList[font.getStyle()]);

  }

  public static Font showDialog(Frame parent, String s, Font font) {
    NwFontChooserS fd = new NwFontChooserS(parent, true, font);
    if (s != null) fd.setTitle(s);
    fd.setVisible(true);
    Font fo = font;
    if (fd.ob) fo = Sample.getFont();
    fd.dispose();
    return (fo);
  }

  private void initAll() {
    getContentPane().setLayout(null);
    setBounds(50, 50, 425, 405);
    addLists();
    addButtons();
    Sample.setBounds(10, 320, 415, 25);
    Sample.setForeground(Color.black);
    getContentPane().add(Sample);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        setVisible(false);
      }
    });
  }

  private void addLists() {
    FontList = new NwList(GraphicsEnvironment.getLocalGraphicsEnvironment
                          ().getAvailableFontFamilyNames());
    StyleList = new NwList(styleList);
    SizeList = new NwList(sizeList);
    FontList.setBounds(10, 10, 260, 295);
    StyleList.setBounds(280, 10, 80, 295);
    SizeList.setBounds(370, 10, 40, 295);
    getContentPane().add(FontList);
    getContentPane().add(StyleList);
    getContentPane().add(SizeList);
  }

  private void addButtons() {
    JButton ok = new JButton("确定");
    ok.setMargin(new Insets(0, 0, 0, 0));
    JButton ca = new JButton("取消");
    ca.setMargin(new Insets(0, 0, 0, 0));
    ok.setBounds(260, 350, 70, 20);
    ok.setFont(new Font(" ", 1, 11));
    ca.setBounds(340, 350, 70, 20);
    ca.setFont(new Font(" ", 1, 12));
    getContentPane().add(ok);
    getContentPane().add(ca);
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        ob = true;
      }
    });
    ca.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        ob = false;
      }
    });
  }

  private void showSample() {
    int g = 0;
    try {
      g = Integer.parseInt(SizeList.getSelectedValue());
    }
    catch (NumberFormatException nfe) {}
    String st = StyleList.getSelectedValue();
    int s = Font.PLAIN;
    if (st.equalsIgnoreCase("Bold")) s = Font.BOLD;
    if (st.equalsIgnoreCase("Italic")) s = Font.ITALIC;
    Sample.setFont(new Font(FontList.getSelectedValue(), s, g));
//Sample.setText("The quick brown fox jumped over the lazy dog.");
    Sample.setText(
        " à&eth;é &auml;&aring;ìê ìèééì áù&icirc;ù &aring;á&ouml;ì, Ok Cancel ");
  }

//////////////////////////////////////////////////////////////////////
  public class NwList
      extends JPanel {
    JList jl;
    JScrollPane sp;
    JLabel jt;
    String si = " ";

    public NwList(String[] values) {
      setLayout(null);
      jl = new JList(values);
      sp = new JScrollPane(jl);
      jt = new JLabel();
      jt.setBackground(Color.white);
      jt.setForeground(Color.black);
      jt.setOpaque(true);
      jt.setBorder(new JTextField().getBorder());
      jt.setFont(getFont());
      jl.setBounds(0, 0, 100, 1000);
      jl.setBackground(Color.white);
      jl.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          jt.setText( (String) jl.getSelectedValue());
          si = (String) jl.getSelectedValue();
          showSample();
        }
      });
      add(sp);
      add(jt);
    }

    public String getSelectedValue() {
      return (si);
    }

    public void setSelectedItem(String s) {
      jl.setSelectedValue(s, true);
    }

    public void setBounds(int x, int y, int w, int h) {
      super.setBounds(x, y, w, h);
      sp.setBounds(0, y + 12, w, h - 23);
      sp.revalidate();
      jt.setBounds(0, 0, w, 20);
    }
  }
}

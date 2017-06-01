package com.rock.drawboard.Painter;

/*
软件作者： 熊锡君，时守刚
软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/
import java.awt.image.*;
import java.io.*;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 将Image对象转换成jpeg图片
 */
public class ImageToJpeg {
  public static boolean toJpeg(File aFile,BufferedImage bimg)
  {
    try
    {
      FileOutputStream fos=new FileOutputStream(aFile);
      JPEGImageEncoder encoder=JPEGCodec.createJPEGEncoder(fos);
      encoder.encode(bimg);
    }
    catch(Exception ex)
    {
      return false;
    }
    return true;
  }
}

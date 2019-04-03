package util;

import com.google.common.primitives.Bytes;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ModbusUtil {
    public static byte slaveID=0x01;
    private static  ArrayList<Byte> al = new ArrayList<>();
    public static int  registerRead(OutputStream outputStream, InputStream inputStream,String address)
    {
        int returnValue=-1;
        try {
            al.clear();
            al.add((byte) 0x12);
            al.add((byte) 0x34);//校验码
            al.add((byte) 0x00);
            al.add((byte) 0x00);//tcp modbus协议
            al.add((byte) 0x00);
            al.add((byte) 0x06);//数据长度
            al.add(slaveID);//Slave ID
            al.add((byte) 0x03);//功能码

           byte[] hex = intString2byte(address);
            if(hex==null)
                return returnValue;
            al.add(hex[0]);
            al.add(hex[1]);//寄存器地址
            al.add((byte) 0x00);
            al.add((byte) 0x01);//寄存器个数
            outputStream.write(Bytes.toArray(al));
            for (int checks = 3; checks > 0; checks--) {
                Thread.sleep(10);
                int length = inputStream.available();
                byte[] recData = new byte[length];
                inputStream.read(recData);
                if (0 != length)//有数据接收
                {
                    byte[] value = new byte[]{recData[9], recData[10]};
                    int integer = Integer.parseInt(ParseUtil.parseByte2HexStr(value), 16);
                    returnValue=integer;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            Log.getInstance().e("寄存器读取",e.toString());
        }
        return returnValue;
    }



    public static boolean  registerWrite(OutputStream outputStream, InputStream inputStream,String address, int value)
    {
        boolean success=false;
        try {
            al.clear();
            al.add((byte) 0x34);
            al.add((byte) 0x12);//校验码
            al.add((byte) 0x00);
            al.add((byte) 0x00);//tcp modbus协议
            al.add((byte) 0x00);
            al.add((byte) 0x06);//数据长度
            al.add(slaveID);//Slave ID
            al.add((byte) 0x06);//功能码

            byte[] hex = intString2byte(address);
            if(hex==null)
                return false;
            al.add(hex[0]);
            al.add(hex[1]);//寄存器地址
             hex = intString2byte(Integer.toHexString(value));
            if(hex==null)
                return false;
            al.add(hex[0]);
            al.add(hex[1]);//寄存器数值
            while (inputStream.available() != 0) {
                inputStream.read(new byte[1]);
            }
            outputStream.write(Bytes.toArray(al));

            for (int checks = 3; checks > 0; checks--) {
                Thread.sleep(10);
                int length = inputStream.available();
                byte[] recData = new byte[length];
                inputStream.read(recData);
                if (0 != length)//有数据接收
                {
                    Log.getInstance().d("设置值的返回信息",ParseUtil.parseByte2HexStr(recData));

                    byte[] vs = new byte[]{recData[10], recData[11]};
                    int integer = Integer.parseInt(ParseUtil.parseByte2HexStr(vs), 16);
                    if (value == integer) {

                      success=true;
                    } else {
                        success=false;
                    }
                    break;
                }
            }
        }catch (Exception e)
        {
            Log.getInstance().e("寄存器写入",e.toString());
        }

        return success;
    }

    public static int coilRead(OutputStream outputStream, InputStream inputStream,String address)
    {
        int bit=-1;

        try {
            al.clear();
            al.add((byte) 0x12);
            al.add((byte) 0x34);//校验码
            al.add((byte) 0x00);
            al.add((byte) 0x00);//tcp modbus协议
            al.add((byte) 0x00);
            al.add((byte) 0x06);//数据长度
            al.add(slaveID);//Slave ID
            al.add((byte) 0x01);//功能码
            byte[] hex = intString2byte(address);
            if(hex==null)
                return bit;
            al.add(hex[0]);
            al.add(hex[1]);//寄存器地址
            al.add((byte) 0x00);
            al.add((byte) 0x01);//寄存器个数
            outputStream.write(Bytes.toArray(al));
            for (int checks = 3; checks > 0; checks--) {
                Thread.sleep(10);
                int length = inputStream.available();
                byte[] recData = new byte[length];
                inputStream.read(recData);
                if (0 != length)//有数据接收
                {
                    byte[] value = new byte[]{recData[9]};
                    int integer = Integer.parseInt(ParseUtil.parseByte2HexStr(value), 16);
                    bit=integer;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
            Log.getInstance().e("线圈读取",e.toString());
        }

     return bit;
    }
    public static boolean  coilWrite(OutputStream outputStream, InputStream inputStream,String address, int value)
    {
        boolean success=false;
        try {
            al.clear();
            al.add((byte) 0x34);
            al.add((byte) 0x12);//校验码
            al.add((byte) 0x00);
            al.add((byte) 0x00);//tcp modbus协议
            al.add((byte) 0x00);
            al.add((byte) 0x06);//数据长度
            al.add(slaveID);//Slave ID
            al.add((byte) 0x05);//功能码

            byte[] hex = intString2byte(address);
            if(hex==null)
                return false;
            al.add(hex[0]);
            al.add(hex[1]);//寄存器地址
           if(value==1)
            al.add((byte)0xff);
           else
               al.add((byte)0x00);
            al.add((byte)0x00);//寄存器数值
            while (inputStream.available() != 0) {
                inputStream.read(new byte[1]);
            }
            outputStream.write(Bytes.toArray(al));

            for (int checks = 3; checks > 0; checks--) {
                Thread.sleep(10);
                int length = inputStream.available();
                byte[] recData = new byte[length];
                inputStream.read(recData);
                if (0 != length)//有数据接收
                {
                    System.out.println(ParseUtil.parseByte2HexStr(recData));
                    if(recData[10]==(byte)0xFF&&value==1)
                    {
                        success=true;
                    }
                    else if(recData[10]==(byte)0x00&&value==0)
                    {
                        success=true;
                    }
                    Log.getInstance().d("设置值的返回信息：" ,ParseUtil.parseByte2HexStr(recData));
                    break;
                }
            }
        }catch (Exception e)
        {
            Log.getInstance().e("线圈写入",e.toString());
        }

        return success;
    }
    public static byte[] intString2byte(String s)//自动补足四位
    {
        String tmp = Integer.toHexString(Integer.parseInt(s));
        switch (tmp.length()) {
            case 0:return null;
            case 1:
                tmp = "000" + tmp;
                break;
            case 2:
                tmp = "00" + tmp;
                break;
            case 3:
                tmp = "0" + tmp;
                break;
        }
        return ParseUtil.parseHexStr2Byte(tmp);
    }

}

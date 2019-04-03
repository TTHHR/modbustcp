package rido.modbustcp.presenter;



import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rido.modbustcp.model.Config;
import rido.modbustcp.model.XianzhiModel;
import rido.modbustcp.view.inter.MainInterface;
import util.Log;
import util.ModbusUtil;
import util.ParseUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainPresenter {
    private  MainInterface mi;
    private boolean running=false;
    private boolean autoRefresh=false;
    private Socket socket=null;
    private Config config=new Config();
    private List<XianzhiModel> xianzhiModels;
    private ArrayList<String> functions=new  ArrayList<>();
    private ArrayList<String> values=new  ArrayList<>();
    private ArrayList<String> functionNames=new  ArrayList<>();
    private ArrayList<String> dataTypes =new  ArrayList<>();
    private ArrayList<String> addresss= new ArrayList<>();
    private ArrayList<String> addressTypes=new  ArrayList<>();
    private ArrayList<String> 单位=new  ArrayList<>();
    private ArrayList<String> remarks=new  ArrayList<>();
    public MainPresenter(MainInterface mi)
    {
        this.mi=mi;
    }
    public void loadExcel(String path)
    {
        try {
            Workbook wb = new XSSFWorkbook(path);
            Sheet sheet = wb.getSheetAt(0);  // 获取第一个Sheet页
            Row row = sheet.getRow(0); // 获取第一行
           boolean isTrueFile=true;
           for(int i=0;i<8;i++)
            {
                Cell cell = row.getCell(i); // 获取单元格
                String tmp;
                switch (i)
                {
                    case 0:tmp="功能块";break;
                    case 1:tmp="功能类型";break;
                    case 2:tmp="功能";break;
                    case 3:tmp="数据类型";break;
                    case 4:tmp="地址";break;
                    case 5:tmp="地址位种类";break;
                    case 6:tmp="单位";break;
                    case 7:tmp="备注";break;
                    default:tmp="152634";
                }
                if(!cell.getStringCellValue().equals(tmp))
                    isTrueFile=false;
                if(!isTrueFile)
                    break;
            }
           if(!isTrueFile)
           {
               mi.showDialog("错误","EXCEL文件不正确");
               wb.close();
               return ;
           }
           xianzhiModels=new ArrayList<>();
           for(int i=1;i<99;i++)
           {
               XianzhiModel xz=new XianzhiModel();
               row = sheet.getRow(i); // 获取行
               Cell cell=row.getCell(0);
               if (cell == null ||cell.getCellType() == CellType.BLANK){
                   break;
               }
               for(int k=0;k<8;k++) {
                   cell=row.getCell(k);
                   String text="";
                   int num=0;
                   if(cell.getCellType()==CellType.STRING)
                       text=cell.getStringCellValue();
                   else
                       num=(int)cell.getNumericCellValue();
                   switch (k)
                   {
                       case 0:xz.setFunctionName(text);break;
                       case 1:xz.setFunctionType(text);break;
                       case 2:xz.setFunction(text);break;
                       case 3:xz.setDataType(text);break;
                       case 4:xz.setAddress(num);break;
                       case 5:xz.setAddressBitType(text);break;
                       case 6:xz.set单位(text);break;
                       case 7:xz.setRemark(text);break;
                       default:
                   }
               }
               xianzhiModels.add(xz);
           }
            wb.close();
           mi.showDialog("成功","总共"+xianzhiModels.size()+"条数据!");
            functions.clear();
           functionNames.clear();
            dataTypes.clear();
            addresss.clear();
            addressTypes.clear();
            单位.clear();
            remarks.clear();
            for(XianzhiModel xzm: xianzhiModels)
            {
                functions.add(xzm.getFunction());
                functionNames.add(xzm.getFunctionName());
                dataTypes.add(xzm.getDataType());
                addresss.add(""+xzm.getAddress());
                addressTypes.add(xzm.getAddressBitType());
                remarks.add(xzm.getRemark());
                单位.add(xzm.get单位());
            }
            mi.setFunctionList(functions);
            mi.setAddressList(addresss);
            mi.setDataTypeList(dataTypes);
            mi.setFunctionNameList(functionNames);
            mi.setAddressTypeList(addressTypes);
            mi.setRemarkList(remarks);
            mi.setValueList(单位);
        }
        catch (Exception e)
        {
            Log.getInstance().e("excel",e.toString());
            mi.showDialog("错误",path+" 不能打开\n"+e);
        }

    }
    public void setSlaveID(String s)
    {
        byte[] id=ModbusUtil.intString2byte(s);
        if(id!=null&&id.length>1) {
            ModbusUtil.slaveID = id[1];//因为自动补足4位，所以使用id[1]
            Log.getInstance().d("id", ParseUtil.parseByte2HexStr(id));
        }
        else
            mi.showDialog("错误","设置有误");
    }
    public void setIpAddress(String text)
    {
        String[] s = text.split(":");
        if(s.length!=2)
        {
            mi.showDialog("ip地址错误","请正确填写，使用英文字符");
            return ;
        }
        try {
            config.setIpAddress(s[0]);
            config.setPort(Integer.parseInt(s[1]));
            if(socket!=null)
                socket.close();
            socket=new Socket(config.getIpAddress(),config.getPort());
        }
        catch (Exception e)
        {
            mi.showDialog("发生错误",e.toString());
        }

    }
    public void query()//一次查询
    {
        if(running) {
            running = false;
            return;
        }

        if(functionNames.size()!=0)
        {

            if(socket==null)
            {
                mi.showDialog("错误","没有正确设置IP");
                return;
            }
                running = true;

                mi.setRunStatus(true);
                new Thread(() -> {
                    values.clear();
                    try {
                        OutputStream outputStream=socket.getOutputStream();
                        InputStream inputStream=socket.getInputStream();
                        for(int i=0;i<xianzhiModels.size();i++) {
                            if(addressTypes.get(i).startsWith("可读写寄存器")) {
                                int v = ModbusUtil.registerRead(outputStream, inputStream, addresss.get(i));
                                values.add(v + 单位.get(i));
                            }
                            if(addressTypes.get(i).startsWith("可读写状态量"))
                            {
                                int v=ModbusUtil.coilRead(outputStream,inputStream,addresss.get(i));
                                values.add(v + 单位.get(i));
                            }
                            if(!running)//停止查询
                                break;
                        }
                        mi.setValueList(values);
                    }catch (Exception e)
                    {
                        mi.showDialog("查詢錯誤",e.toString());
                    }
                    running=false;
                    mi.setRunStatus(false);

                }).start();

        }

    }
    public void setAddresss(int index,String value)
    {
        Log.getInstance().i("设置值","index="+index+"value="+value);
        try {
            int v = Integer.parseInt(value);
            if(v>65535)
            {

                mi.showDialog("错误","数值太大");
            }
            else
            {
                addresss.set(index,v+"");
                mi.setAddressList(addresss);
                if(addressTypes.get(index).startsWith("可读写寄存器")) {
                    OutputStream outputStream=socket.getOutputStream();
                    InputStream inputStream=socket.getInputStream();
                    v=ModbusUtil.registerRead(outputStream,inputStream,addresss.get(index));
                    values.set(index,v+单位.get(index));
                    mi.setValueList(values);
                }
                else if(addressTypes.get(index).startsWith("可读写状态量"))
                {
                        OutputStream outputStream=socket.getOutputStream();
                        InputStream inputStream=socket.getInputStream();
                        v=ModbusUtil.coilRead(outputStream,inputStream,addresss.get(index));
                            values.set(index,v+单位.get(index));
                            mi.setValueList(values);

                }

            }


        }
        catch (Exception e)
        {
            mi.showDialog("错误","请输入正确的值");
        }

    }
    public void setValue(int index,String value)
    {
        Log.getInstance().i("设置值","index="+index+"value="+value);
        try {
            int v = Integer.parseInt(value);
            if(addressTypes.get(index).startsWith("可读写寄存器"))
            {
                if(v>65535)
                {
                    mi.showDialog("错误","数值太大");
                }
                else
                {
                    OutputStream outputStream=socket.getOutputStream();
                    InputStream inputStream=socket.getInputStream();
                    if(ModbusUtil.registerWrite(outputStream,inputStream,addresss.get(index),v)) {
                        values.set(index,v+单位.get(index));
                        mi.setValueList(values);
                        mi.showDialog("成功","如题");
                    }
                    else {
                        mi.showDialog("失败","如题");
                    }

                }
            }
            else if(addressTypes.get(index).startsWith("可读写状态量"))
            {
                if(v!=1&&v!=0)
                {
                    mi.showDialog("错误","数值只能为1或0");
                }
                else
                {
                    OutputStream outputStream=socket.getOutputStream();
                    InputStream inputStream=socket.getInputStream();
                    if(ModbusUtil.coilWrite(outputStream,inputStream,addresss.get(index),v)) {
                        values.set(index,v+单位.get(index));
                        mi.setValueList(values);
                        mi.showDialog("成功","如题");
                    }
                    else {
                        mi.showDialog("失败","如题");
                    }

                }
            }
        }
        catch (Exception e)
        {
            mi.showDialog("错误","请输入正确的值");
        }

    }
    public void run(String time)
    {
        if(autoRefresh)
        {
           autoRefresh=false;
        }
        else
        try {
            autoRefresh=true;
            int v = Integer.parseInt(time);
            Log.getInstance().d("自动刷新",time);
            new Thread(() -> {
                while (autoRefresh) {
                    query();//查询一次
                    while (running)
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    try {
                        Thread.sleep(v);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch (Exception e)
        {
            mi.showDialog("发生错误",e.toString());
        }
    }
    public void close()
    {
        System.out.println("mp close");
        running=false;
    }
}

package rido.modbustcp.view.inter;

import java.util.List;

public interface MainInterface {
    public void showDialog(String titie,String text);
    public void setFunctionList(List list);
    public void setFunctionNameList(List list);
    public void setDataTypeList(List list);
    public void setValueList(List list);
    public void setAddressList(List list);
    public void setAddressTypeList(List list);
    public void setRemarkList(List lisr);
    public void setRunStatus(boolean on);
}

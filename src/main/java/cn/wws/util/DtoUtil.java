package cn.wws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.mysql.jdbc.StringUtils;

public class DtoUtil{
    boolean quotationOpen = false;//引号关闭
    
    public String getObjByTable(String createTableCmd){
        StringBuilder sb = new StringBuilder();
        StringWriter sw = new StringWriter();
        sw.write(createTableCmd);
        
        try(StringReader sr = new StringReader(createTableCmd)) {
            String className = getClassName(sr);
            if(StringUtils.isNullOrEmpty(className)){
                return "数据异常";
            }
            String classNameTail = className.substring(1);
            className = className.substring(0, 1).toUpperCase() + classNameTail;
            //System.out.println("类名：" + className);
            sb.append("public class ").append(className).append(" {");
            
            List<Map<String, String>> columnList = getColumnList(sr);
            //System.out.println("列信息：");
            //System.out.println(columnList);
            collectColumns(sb, columnList);
            sb.append(System.getProperty("line.separator")).append("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    //组装列信息
    private void collectColumns(StringBuilder sb, List<Map<String, String>> columnList){
        for(Map<String, String> map : columnList){
            if(map.containsKey("colComment")){
                sb.append(System.getProperty("line.separator")).append("    //").append(map.get("colComment"));
            }
            sb.append(System.getProperty("line.separator")).append("    private ");
            if(map.get("colType").indexOf("bigint") >= 0){
                sb.append("Long ");
            } else if(map.get("colType").indexOf("int") >= 0){
                sb.append("Integer ");
            } else {
                sb.append("String ");
            }
            sb.append(MapperUtil.underlineToHump(map.get("colName").replace("`", ""))).append(";");
        }
    }
    
    private List<Map<String, String>> getColumnList(StringReader reader) throws IOException{
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        List<String> colLines = getColLines(reader);
        for(String line : colLines){
            line = line.trim();
            if(line.startsWith("KEY ") || line.startsWith("PRIMARY KEY ")
                    || line.startsWith("key ") || line.startsWith("primary key ")){
                continue;
            }
            String[] desc = line.split(" ");
            Map<String, String> columnMap = new HashMap<>();
            for(int i=0; i<desc.length; i++){
                if(i == 0){
                    columnMap.put("colName", desc[i].toLowerCase());
                } else if(i == 1){
                    columnMap.put("colType", desc[i].toLowerCase());
                } else {
                    if("COMMENT".equalsIgnoreCase(desc[i])){
                        columnMap.put("colComment", desc[i+1]);
                        break;
                    }
                }
            }
            list.add(columnMap);
        }
        return list;
    }
    
    private String getClassName(StringReader reader) throws IOException{
        Stack<String> cmdStock = new Stack<>();
        String next = null;
        while((next = getNextWord(reader)) != null){
            if(next.trim().equalsIgnoreCase("CREATE")){
                cmdStock.push(next.trim().toUpperCase());
            }
            if(next.trim().equalsIgnoreCase("TABLE")){
                String last = cmdStock.pop();
                if("CREATE".equals(last)){
                    next = getNextWord(reader);
                    return MapperUtil.underlineToHump(next.replace("`", ""));
                }
            }
        }
        return "";
    }
    
    //列描述
    private List<String> getColLines(StringReader reader) throws IOException{
        Stack<String> cmdStock = new Stack<>();
        List<String> colLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(int cur = reader.read(); cur != -1;cur = reader.read()){
            if(cur == 39){//单引号
                quotationOpen = !quotationOpen;
            } else if(!quotationOpen){
                if(cur == 40){//左括号
                    cmdStock.push("(");
                    if(cmdStock.size() == 1){
                        continue;
                    }
                } else if(cur == 41){//右括号
                    cmdStock.pop();
                    if(cmdStock.isEmpty()){
                        break;
                    }
                //} else if(cur == 44){//逗号
                } else if(cur == 13 || cur == 10){//回车和换行
                    if(sb.length() > 0){
                        if(sb.charAt(sb.length() - 1) == ','){//最后的逗号去掉
                            sb.setLength(sb.length() - 1);
                        }
                        colLines.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    continue;
                }
            }
            char c = (char) cur;
            sb.append(c);
        }
        return colLines;
    }
    
    private String getNextWord(StringReader reader) throws IOException{
        StringBuilder sb = new StringBuilder();
        for(int cur = reader.read(); cur != 32 && (cur < 9 || cur > 13);cur = reader.read()){
            if(cur == -1){
                if(sb.length() == 0){
                    return null;
                } else {
                    char c = (char) cur;
                    sb.append(c);
                    break;
                }
            } else if(cur == 39){//单引号
                quotationOpen = !quotationOpen;
            }
            char c = (char) cur;
            sb.append(c);
        }
        return sb.toString();
    }
    
    public static void main(String[] args) throws IOException{
        DtoUtil ins = new DtoUtil();
        File file = new File("D://out.txt");
        FileInputStream fis = new FileInputStream(file);//创建输入流fis
        int size=fis.available();

        byte[] buffer=new byte[size];

        fis.read(buffer);

        fis.close();

        String str1=new String(buffer,"UTF-8");
        //System.out.println(str1);
        String rst = ins.getObjByTable(str1);
        System.out.println(rst);
    }
}

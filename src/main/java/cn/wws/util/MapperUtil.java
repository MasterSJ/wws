package cn.wws.util;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapperUtil<T> {
    private static MapperUtil instance;
    
    private MapperUtil(){
    }
    
    public static MapperUtil getInstance(){
        if (instance == null){
            synchronized (MapperUtil.class) {
                if(instance == null){
                    instance = new MapperUtil();
                }
            }
        }
        return instance;
    }
    /**    
    * @Description 通过泛型和反射自动Mapper数据. 
    * @author songjun  
    * @date 2019年1月9日   
    * @param maplist
    * @param t
    * @return
    * @throws Exception
    */ 
    public List<T> mapperList(List<Map> maplist,Class<T> t) throws Exception{
        List<Object> rtnlist=new ArrayList<>();
        if(maplist==null||maplist.size()==0){
            return (List<T>)rtnlist;
        }
        for(Map map:maplist){
            Object tobj=mapperObj(map, t);
            rtnlist.add(tobj);
        }
        return (List<T>)rtnlist;
    }
    
    /**
     * 反射Mapper  数据对象(默认按下划线-驼峰映射，未匹配则按原列名映射)
     * @param map
     * @param t
     * @return
     * @throws Exception
     */
    public <T>T mapperObj(Map map, Class<T> t) throws Exception{
        if(map==null||map.size()==0){
            return t.newInstance();
        }
        Object tobj=t.newInstance();
        Field[] fields = t.getDeclaredFields();
        for(Field field : fields){
            Object value = map.get(humpToUnderline(field.getName()));
            if(value != null){
                field.setAccessible(true);
                field.set(tobj, value);
            } else {
                value = map.get(field.getName());
                if(value != null){
                    field.setAccessible(true);
                    field.set(tobj, value);
                }
            }
        }
        return (T) tobj;
    }
    
    //下划线转驼峰
    public String underlineToHump(String para){
        StringBuilder result=new StringBuilder();
        String a[]=para.split("_");
        for(String s:a){
            if(result.length()==0){
                result.append(s.toLowerCase());
            }else{
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
    
    //驼峰转下划线
    public String humpToUnderline(String para){
        StringBuilder sb=new StringBuilder(para);
        int temp=0;//定位
        for(int i=0;i<para.length();i++){
            if(Character.isUpperCase(para.charAt(i))){
                sb.insert(i+temp, "_");
                temp+=1;
            }
        }
        return sb.toString().toLowerCase(); 
    }

}

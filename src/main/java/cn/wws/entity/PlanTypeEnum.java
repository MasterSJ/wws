/**
 * 
 */
package cn.wws.entity;

/**  
* @ClassName: PlanTypeEnum  
* @Description: TODO(这里用一句话描述这个类的作用)  
* @author songjun 
* @date 2019年6月19日  
*    
*/
public enum PlanTypeEnum {
    DAY("day","每天计划"),
    WEEK("week","每周计划"),
    MONTH("month","每月计划"),
    YEAR("year","每年计划"),
    ONCE("once","单次计划");
    
    private String code;
    private String desc;
    private PlanTypeEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}

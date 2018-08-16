package cn.wws.entity;

import java.util.Map;


public class User {
	private String userName;
	private String realName;
	private String email;
	private String mobile;
	private String nikeName;
	private String resume;
	private String birthday;

	
	public User(Map<String, String> userMap){
		setUserName(userMap.get("user_name"));
		setRealName(userMap.get("real_name"));;
		setEmail(userMap.get("email"));
		setMobile(userMap.get("mobile"));
		setNikeName(userMap.get("nike_name"));
		setResume(userMap.get("resume"));
		setBirthday(userMap.get("birthday"));
	}
	
	public User(String userName) {
		this.userName = userName == null ? "" : userName;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? "" : email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? "" : mobile;
	}

	public String getNikeName() {
		return nikeName;
	}

	public void setNikeName(String nikeName) {
		this.nikeName = nikeName == null ? "" : nikeName;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume == null ? "" : resume;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday == null ? "" : birthday;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName == null ? "" : userName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName == null ? "" : realName;
	}
	
	/*public String toString(){
		String ret = Joiner.on("").join("{\"userName\":\"", this.userName, 
				"\",\"realName\"=\"", this.realName,
				"\",\"email\"=\"", this.email,
				"\",\"mobile\"=\"", this.mobile,
				"\",\"nikeName\"=\"", this.nikeName,
				"\",\"resume\"=\"", this.resume,
				"\",\"birthday\"=\"", this.birthday,
				"\"}");
		return ret;
	}*/
}

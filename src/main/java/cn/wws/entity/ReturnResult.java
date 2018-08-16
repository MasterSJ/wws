package cn.wws.entity;

import com.alibaba.fastjson.JSONObject;

public class ReturnResult {
	private String status;
	private String msg;
	
	public boolean isSuccess() {
		if ("FAIL".equals(this.status)) {
			return false;
		} else {
			return true;
		}
	}

	public ReturnResult() {
		this.status = "FAIL";
		this.msg = "";
	}
	
	/**
	 * @param status the status to set
	 */
	public void setSuccess() {
		this.status = "SUCCESS";
	}
	
	/**
	 * @param status the status to set
	 */
	public void setFail() {
		this.status = "FAIL";
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	/**
	 * @Description: 设置状态为SUCCESS，并设置消息msg
	 */
	public void setSuccessMsg(String msg) {
		this.status = "SUCCESS";
		this.msg = msg;
	}
	
	/**
	 * @Description: 设置状态为FAIL，并设置消息msg
	 */
	public void setFailMsg(String msg) {
		this.status = "FAIL";
		this.msg = msg;
	}
	
	public String toJsonString() {
		JSONObject ret = new JSONObject();
		ret.put("status", this.status);
		ret.put("msg", this.msg);
		return ret.toJSONString();
	}
}

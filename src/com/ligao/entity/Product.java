package com.ligao.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 产品信息
 * @author Administrator
 *
 */
public class Product implements Serializable{
	
	
	/**
	 * 主键ID
	 */
	private String Id;
	/**
	 *  产品编码
	 */
	private String PCode;
	/**
	 * 产品名称
	 */
	private String PName;
	/**
	 *  单位
	 */
	private String Unit;
	/**
	 * 计划数量
	 */
	private String PlanAmount;
	/**
	 * 计划单品数量
	 */
	private String PlanSingleAmount;
	/**
	 * 实际数量
	 */
	private String FactsAmount;
	/**
	 *  实际单品数量
	 */
	private String FactsSingleAmount;
	/**
	 * 出入货单ID
	 */
	private String WId;
	/**
	 * 规格
	 */
	private String Spec;
	/**
	 * 工厂编码
	 */
	private String Fcode;
	/**
	 * 产品生产日期
	 */
	private String PDate;
	/**
	 * 箱码集合
	 */
	private List<String> BoxCodeList;
	/**
	 * 垛码集合
	 */
	private  List<String> StackCodeList;
	/**
	 * 单品码集合
	 */
	private  List<String> dpSingleCodeList;
	/**
	 * 
	 */
	//private String BoxLogistics;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getPCode() {
		return PCode;
	}
	public void setPCode(String pCode) {
		PCode = pCode;
	}
	public String getPName() {
		return PName;
	}
	public void setPName(String pName) {
		PName = pName;
	}
	public String getUnit() {
		return Unit;
	}
	public void setUnit(String unit) {
		Unit = unit;
	}
	public String getPlanAmount() {
		return PlanAmount;
	}
	public void setPlanAmount(String planAmount) {
		PlanAmount = planAmount;
	}
	public String getPlanSingleAmount() {
		return PlanSingleAmount;
	}
	public void setPlanSingleAmount(String planSingleAmount) {
		PlanSingleAmount = planSingleAmount;
	}
	public String getFactsAmount() {
		return FactsAmount;
	}
	public void setFactsAmount(String factsAmount) {
		FactsAmount = factsAmount;
	}
	public String getFactsSingleAmount() {
		return FactsSingleAmount;
	}
	public void setFactsSingleAmount(String factsSingleAmount) {
		FactsSingleAmount = factsSingleAmount;
	}
	public String getWId() {
		return WId;
	}
	public void setWId(String wId) {
		WId = wId;
	}
	public String getSpec() {
		return Spec;
	}
	public void setSpec(String spec) {
		Spec = spec;
	}
	public String getFcode() {
		return Fcode;
	}
	public void setFcode(String fcode) {
		Fcode = fcode;
	}
	public String getPDate() {
		return PDate;
	}
	public void setPDate(String pDate) {
		PDate = pDate;
	}
	public List<String> getBoxCodeList() {
		return BoxCodeList;
	}
	public void setBoxCodeList(List<String> boxCodeList) {
		BoxCodeList = boxCodeList;
	}
	public List<String> getStackCodeList() {
		return StackCodeList;
	}
	public void setStackCodeList(List<String> stackCodeList) {
		StackCodeList = stackCodeList;
	}
	public List<String> getDpSingleCodeList() {
		return dpSingleCodeList;
	}
	public void setDpSingleCodeList(List<String> dpSingleCodeList) {
		this.dpSingleCodeList = dpSingleCodeList;
	}
	
	
}
package com.ligao.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 收/发货单
 * @author Administrator
 *
 */
public class Order  implements Serializable{
	
	/**
	 * 收发货编号
	 */
	private String WId;
	/**
	 * 货单类型0发货单，1收货单
	 */
	private String WType;
	/**
	 * 操作物流节点
	 */
	private String MNode;
	/**
	 * 操作物流节点名称
	 */
	private String MName;
	/**
	 * 上一个发货节点或下一个收货节点
	 */
	private String ONode;
	/**
	 * 上一个发货节点或下一个收货节点名称
	 */
	private String OName;
	/**
	 * 工厂编号
	 */
	private String FCode;
	/**
	 *  收发货时间
	 */
	private String ODateTime;
	/**
	 * 收发货时间 结束时间
	 */
	private String OEndDateTime;
	/**
	 * 创建时间 服务赋值
	 */
	private String CDateTime;
	/**
	 *  状态 0:默认 创建  1：已导出 2：已导入 3：已同步 4：已入库 5：已下载 
	 */
	private String State;
	
	
	/**
	 * 获取状态名称
	 * @return 状态名称
	 */
	public  String getStateName(){
		String stateName="默认";
		String stateStatus = getState();
		switch (Integer.parseInt(stateStatus.split("\\..")[0])) {
		case 0:
			stateName = "默认";
			break;
		case 1:
			stateName = "已导出";
			break;
		case 2:
			stateName = "已导入";
			break;
		case 3:
			stateName = "已同步";
			break;
		case 4:
			stateName = "已入库";
			break;
		case 5:
			stateName = "已下载";
			break;
		default:
			stateName = "默认";
			break;
		}
		return stateName;
	}
	
	/**
	 * 单号
	 */
	private String WCode;
	/**
	 * 入库使用 入库记录对应出库记录的ID
	 */
	private String ReceiveId;
	/**
	 * 操作人
	 */
	private String Operator;
	/**
	 * 箱码集合
	 */
	private String BoxLogisticsInfos;
	/**
	 * 单据类型,0为正常单，1为退货单，2为调拨单
	 */
	private String BillType;
	
	/**
	 * 产品信息集合
	 */
	private List<Product> WwaybillProducts;
	
	/**
	 * 下载时间
	 */
	private String DownloadDateTime;
	/**
	 * 手持端出库单状态
	 * 状态 0:未开始   1：进行中  2：已完成  3:删除 
	 */
	private String HandStatus;
	
	/**
	 * 获取手持端状态名称
	 * @return 状态名称
	 */
	public  String getHandStatusName(){
		String stateName="默认";
		String handStatus = getHandStatus();
		if(handStatus!=null&&!"".equals(handStatus)){
			switch (Integer.parseInt(handStatus)) {
			case 0:
				stateName = "未开始";
				break;
			case 1:
				stateName = "进行中";
				break;
			case 2:
				stateName = "已完成";
				break;
			case 3:
				stateName = "删除";
				break;
			default:
				stateName = "默认";
				break;
			}
		}
		return stateName;
	}
	
	
	public String getWId() {
		return WId;
	}

	public void setWId(String wId) {
		WId = wId;
	}

	public String getWType() {
		return WType;
	}

	public void setWType(String wType) {
		WType = wType;
	}

	public String getMNode() {
		return MNode;
	}

	public void setMNode(String mNode) {
		MNode = mNode;
	}

	public String getMName() {
		return MName;
	}

	public void setMName(String mName) {
		MName = mName;
	}

	public String getONode() {
		return ONode;
	}

	public void setONode(String oNode) {
		ONode = oNode;
	}

	public String getOName() {
		return OName;
	}

	public void setOName(String oName) {
		OName = oName;
	}

	public String getFCode() {
		return FCode;
	}

	public void setFCode(String fCode) {
		FCode = fCode;
	}

	public String getODateTime() {
		return ODateTime;
	}

	public void setODateTime(String oDateTime) {
		ODateTime = oDateTime;
	}

	public String getOEndDateTime() {
		return OEndDateTime;
	}

	public void setOEndDateTime(String oEndDateTime) {
		OEndDateTime = oEndDateTime;
	}

	public String getCDateTime() {
		return CDateTime;
	}

	public void setCDateTime(String cDateTime) {
		CDateTime = cDateTime;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getWCode() {
		return WCode;
	}

	public void setWCode(String wCode) {
		WCode = wCode;
	}

	public String getReceiveId() {
		return ReceiveId;
	}

	public void setReceiveId(String receiveId) {
		ReceiveId = receiveId;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public String getBoxLogisticsInfos() {
		return BoxLogisticsInfos;
	}

	public void setBoxLogisticsInfos(String boxLogisticsInfos) {
		BoxLogisticsInfos = boxLogisticsInfos;
	}

	public String getBillType() {
		return BillType;
	}

	public void setBillType(String billType) {
		BillType = billType;
	}

	public List<Product> getWwaybillProducts() {
		return WwaybillProducts;
	}

	public void setWwaybillProducts(List<Product> wwaybillProducts) {
		WwaybillProducts = wwaybillProducts;
	}

	public String getHandStatus() {
		return HandStatus;
	}

	public void setHandStatus(String handStatus) {
		HandStatus = handStatus;
	}

	public String getDownloadDateTime() {
		return DownloadDateTime;
	}

	public void setDownloadDateTime(String downloadDateTime) {
		DownloadDateTime = downloadDateTime;
	}
	


	
}

package com.picc.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 查询信息
 * @author Administrator
 *
 */
public class ClosingListMessage {
	
	private Integer totalCount;//案件个数
	private Double sumMoney;//案件金额
	private Integer nowCount;//当期
	private Integer countYear;//年报件数
	private Double moneyCount;//年报金额
	private Integer countMonth;//月报件数
	private Double moneyMonth;//月报金额
	private Integer countYearMonth;//年报月结件数
	private Double moneyYearMonth;//年报月结金额
	private String userName;//查勘员
	private String groupName;//机构
	private String myCaseType;//案件类型
	public Integer getTotalCount() {
		return totalCount;
	}
	public Double getSumMoney() {
		return sumMoney;
	}
	public void setSumMoney(Double sumMoney) {
		this.sumMoney = sumMoney;
	}
	public Integer getNowCount() {
		return nowCount;
	}
	public void setNowCount(Integer nowCount) {
		this.nowCount = nowCount;
	}
	public Integer getCountYear() {
		return countYear;
	}
	public void setCountYear(Integer countYear) {
		this.countYear = countYear;
	}
	public Double getMoneyCount() {
		return moneyCount;
	}
	public void setMoneyCount(Double moneyCount) {
		this.moneyCount = moneyCount;
	}
	public Integer getCountMonth() {
		return countMonth;
	}
	public void setCountMonth(Integer countMonth) {
		this.countMonth = countMonth;
	}
	public Double getMoneyMonth() {
		return moneyMonth;
	}
	public void setMoneyMonth(Double moneyMonth) {
		this.moneyMonth = moneyMonth;
	}
	public Integer getCountYearMonth() {
		return countYearMonth;
	}
	public void setCountYearMonth(Integer countYearMonth) {
		this.countYearMonth = countYearMonth;
	}
	public Double getMoneyYearMonth() {
		return moneyYearMonth;
	}
	public void setMoneyYearMonth(Double moneyYearMonth) {
		this.moneyYearMonth = moneyYearMonth;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getMyCaseType() {
		return myCaseType;
	}
	public void setMyCaseType(String myCaseType) {
		this.myCaseType = myCaseType;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	
	
	
}

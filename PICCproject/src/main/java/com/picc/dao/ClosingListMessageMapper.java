package com.picc.dao;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.picc.entity.ClosingListMessage;
import com.picc.entity.ClosingListSummarySearchMessage;

public interface ClosingListMessageMapper {
	//按条件查询
	public List<ClosingListMessage> getClosingCountMessageList(ClosingListSummarySearchMessage closingListSummarySearchMessage);
	
	public List<ClosingListSummarySearchMessage> getClosingListCaseType();
	//从数据库获得年份
	public  List<String> getClosingListYear();
	//导表
	public List<ClosingListMessage> exportClosingList(ClosingListSummarySearchMessage closingListSummarySearchMessage);
}

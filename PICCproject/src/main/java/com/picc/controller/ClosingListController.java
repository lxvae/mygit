package com.picc.controller;
/**
 * 结案案件
 * @author Lx
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.maven.shared.utils.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.picc.common.Constant;
import com.picc.entity.ClosingList;

import com.picc.entity.ClosingListMessage;
import com.picc.entity.ClosingListSummarySearchMessage;

import com.picc.entity.Group;
import com.picc.entity.User;
import com.picc.entity.UserCase;
import com.picc.service.ClosingListMessageService;
import com.picc.service.ClosingListService;
import com.picc.service.GroupService;
import com.picc.service.OperationRecordService;
import com.picc.service.UserService;
import com.picc.util.ImportExcelUtil;

@Controller
@RequestMapping("/picc/closing")
public class ClosingListController {
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private ClosingListService closingListService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ClosingListMessageService closingListMessageService;
	@Autowired
	private OperationRecordService oRdService;
	/**
	 * 结案案件导入页面
	 * @param request
	 * @param response
	 * @return 页面
	 */
	@RequiresPermissions("picc.closing.closingimport")
	@RequestMapping(value="/closingimport.html")
	public ModelAndView importPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model  = new ModelAndView();
		model.setViewName("case/closingimport");
		return model;
	}

	/**
	 * 结案案件统计页面
	 * @param request
	 * @param response
	 * @return 页面
	 */
	@RequiresPermissions("picc.closing.closinglist")
	@RequestMapping(value="/closinglist.html")
	public ModelAndView closingListPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model  = new ModelAndView();
		model.setViewName("case/closinglist");
		List<Group> groupList = groupService.getGroupList();
		model.addObject("groupList",groupList);//机构
		List<ClosingListSummarySearchMessage> closingListCaseType = closingListMessageService.getClosingListCaseType();
		model.addObject("closingListCaseType", closingListCaseType);
		List<String> listYear = closingListMessageService.getClosingListYear();
		model.addObject("listYear", listYear);
		return model;
	}
	/**
	 * 通过机构查询对应的查勘员
	 * @param groupId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getUserGroupId")
	@ResponseBody
	public List<User> getUserListByGroupId(@RequestParam(value = "groupId") Integer groupId ,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(groupId==0) {
			return new ArrayList<User>();
		}
		List<User> userList=userService.getUserListByGroupId(groupId);
		
		return userList;
	}
	/**
	 * 结案报表导入
	 */
	@ResponseBody
	@RequestMapping(value = "/closinglistexcel.ajax", method = { RequestMethod.GET, RequestMethod.POST })
	public void closingListUploadExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response)
			throws Exception {
		InputStream in = null;
		List<List<Object>> listob = null;
		if (file.isEmpty()) {
			throw new Exception("工作簿为空");
		}
		in = file.getInputStream();
		listob = new ImportExcelUtil().getBankListByExcel(in, file.getOriginalFilename());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<ClosingList> closingList = new ArrayList<ClosingList>();
		for (int i = 0; i < listob.size(); i++) {
			List<Object> lo = listob.get(i);
			ClosingList cl = new ClosingList();
			if (lo.size() <= 0) {
				break;
			}
			if (lo.get(0).equals("") && lo.get(1).equals("")) {
				break;
			}
				
			try {				
				cl.setReportNumber(String.valueOf(lo.get(0)));;
				cl.setRegistrationNumber(String.valueOf(lo.get(1)));
				cl.setRiskTime(Date.valueOf((String) lo.get(2)));
				cl.setClosingTime(Date.valueOf((String) lo.get(3)));
				cl.setCaseType(String.valueOf(lo.get(4)));	
				cl.setProspectorCode(String.valueOf(lo.get(5)));
				cl.setSurveyor(String.valueOf(lo.get(6)));
				cl.setDuration(String.valueOf(lo.get(7)));
				cl.setAmountOfMoney(String.valueOf(lo.get(8)));
				cl.setGroupId(Integer.parseInt(String.valueOf(lo.get(9))));
			}catch (Exception e) {
			}
			//System.out.println(cl);
			closingList.add(cl);	
		}
		//分批导入，防止数据过大导入失败
		int closingListSize = closingList.size();
		int num = closingListSize/1000;
		int num2 = closingListSize%1000;
		List<ClosingList> arrayList = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			arrayList.clear();
		
			for (int j = i*1000; j < (1 + i) * 1000; j++) {
				arrayList.add(closingList.get(j));
			}
			closingListService.importClosingList(arrayList);
		}
		if (num2 != 0)
		{
			arrayList.clear();
			for (int i = num*1000; i < closingList.size(); i++) {
				arrayList.add(closingList.get(i));
			}
			closingListService.importClosingList(arrayList);
		}
		PrintWriter out = null;
		//ajax中文乱码处理
		response.setCharacterEncoding("utf-8"); 
		out = response.getWriter();
		out.print("导入成功"+closingList.size());
		out.flush();
		out.close();
	
	}
	/**
	 * 结案信息查询结果
	 * @param closingListSummarySerachMessage
	 * @return
	 */
	@PostMapping("/serachClosingListSummary")
	@ResponseBody
	public List<ClosingListSummarySearchMessage> serachClosingListSummary (@RequestParam(value = "groupId") Integer groupId){
		//System.out.println(closingListSummarySerachMessage);
		List<ClosingListSummarySearchMessage> searchList=closingListService.serachClosingListSummaryByGroupId(groupId);
	    return searchList;
		
	}
	/**
	 * 结案信息查询结果
	 * @param closingListSummarySerachMessage
	 * @return
	 */
	@PostMapping("/getClosingListMessage")
	@ResponseBody
	public List<ClosingListMessage> getClosingCountMessageList(ClosingListSummarySearchMessage closingListSummarySearchMessage){
	
		if(closingListSummarySearchMessage.getMonth().equals("0")){
			closingListSummarySearchMessage.setMonth(null);
		}
		List<ClosingListMessage> closingListMessage = closingListMessageService.getClosingCountMessageList(closingListSummarySearchMessage);
		return closingListMessage;
		
	}
	

/**
 * 結案案件报表导出
 * @param request
 * @param response
 * @param session
 */
@ResponseBody
@RequestMapping(value="/closinglistexportexcel.ajax",method= { RequestMethod.POST, RequestMethod.GET})
public void excelUserExport(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
	
	String year = request.getParameter("year");
	String month = request.getParameter("month");
	String moneyType = request.getParameter("moneyType");
	ClosingListSummarySearchMessage clss = new ClosingListSummarySearchMessage();
	clss.setYear(year);
	if(month!=null) {
	clss.setMonth(month);
	}
	clss.setMoneyType(moneyType);
	String fileName=null;
	if(month==null) {
		fileName =year+"年车险结案报表.xlsx";
	}else {
		 
		 fileName =year+"年"+month+"月车险结案个人报表.xlsx";
	}
		try {
			List<ClosingListMessage> closingListMessage = closingListMessageService.getClosingCountMessageList(clss);//导出方法
			List<ClosingListMessage> closingListMessage1 = closingListMessageService.exportClosingList(clss);//导出方法
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/octet-stream;charset=utf-8");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
			//工作簿
			XSSFWorkbook wbk = new XSSFWorkbook();
			// Sheet页
			XSSFSheet sheet = wbk.createSheet("结案报表");
			XSSFCellStyle style = wbk.createCellStyle();;
    		style.setAlignment(HorizontalAlignment.CENTER);   		
    		XSSFRow row = sheet.createRow(0);
    		XSSFCell cell = row.createCell(0);
    		 // 合并日期占两行(4个参数，分别为起始行，结束行，起始列，结束列)
            // 行和列都是从0开始计数，且起始结束都会合并
            // 这里是合并excel中日期的两行为一行
    		// 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
    	    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
    	    if(month!=null) {
    	    cell.setCellValue(year+"年"+month+"月"+moneyType+"元下结案报表");
    	    }else {
    	    	cell.setCellValue(year+"年"+moneyType+"元下结案报表");
    	    }
    	    cell.setCellStyle(style);
    	
    	    row = sheet.createRow(1);
    	    //单元格
    		cell = row.createCell(0);
    		cell.setCellStyle(style);
    		cell.setCellValue("机构");
    		cell = row.createCell(1);
    		cell.setCellStyle(style);
    		cell.setCellValue("查勘员");
    		cell = row.createCell(2);
    		cell.setCellStyle(style);
    		cell.setCellValue("结案件数");
    		cell = row.createCell(3);
    		cell.setCellStyle(style);
    		cell.setCellValue("结案金额");
    		cell = row.createCell(4);
    		cell.setCellStyle(style);
    		cell.setCellValue("当期");
    		cell = row.createCell(5);
    		cell.setCellStyle(style);
    		cell.setCellValue("当年报案当年结案件数");
    		cell = row.createCell(6);
    		cell.setCellStyle(style);
    		cell.setCellValue("当年报案当年结案金额");
    		cell = row.createCell(7);
    		cell.setCellStyle(style);
    		cell.setCellValue("当年报案当月结案件数");
    		cell = row.createCell(8);
    		cell.setCellStyle(style);
    		cell.setCellValue("当年报案当月结案金额");
    		cell = row.createCell(9);
    		cell.setCellStyle(style);
    		cell.setCellValue("当月报案当月结案件数");
    		cell = row.createCell(10);
    		cell.setCellStyle(style);
    		cell.setCellValue("当月报案当月结案金额");
    		XSSFRow rows;
    		XSSFCell cells;
   
    		for (int i = 0; i < closingListMessage.size(); i++) { 
    			// sheet数据行
    			rows = sheet.createRow(i+2);
    			// 单元格
    			// 单元格里设置值
    			cells = rows.createCell(0);
    			cells.setCellValue("中心合计");
    			cells = rows.createCell(1);
    			cells.setCellValue(closingListMessage.get(i).getMyCaseType()); 
    			cells = rows.createCell(2);
    			cells.setCellValue(closingListMessage.get(i).getTotalCount());
    			cells = rows.createCell(3);
    			cells.setCellValue(closingListMessage.get(i).getSumMoney());
    			cells = rows.createCell(4);	    			 
    			cells.setCellValue(closingListMessage.get(i).getNowCount());
    			cells = rows.createCell(5);
    			cells.setCellValue(closingListMessage.get(i).getCountYear());
    			cells = rows.createCell(6);
    			cells.setCellValue(closingListMessage.get(i).getMoneyCount());
    			cells = rows.createCell(7);
    			cells.setCellValue(closingListMessage.get(i).getCountMonth());
    			cells = rows.createCell(8);
    			cells.setCellValue(closingListMessage.get(i).getMoneyMonth());
    			cells = rows.createCell(9);
    			cells.setCellValue(closingListMessage.get(i).getCountYearMonth());
    			cells = rows.createCell(10);
    			cells.setCellValue(closingListMessage.get(i).getMoneyYearMonth());
    		}
    		
			
    		for (int i = 0; i < closingListMessage1.size(); i++) { 
    			// sheet数据行
    			rows = sheet.createRow(i+2+closingListMessage.size());
    			// 单元格
    			// 单元格里设置值
    			cells = rows.createCell(0);
    			cells.setCellValue(closingListMessage1.get(i).getGroupName());
    			cells = rows.createCell(1);
    			cells.setCellValue(closingListMessage1.get(i).getUserName()); 
    			cells = rows.createCell(2);
    			cells.setCellValue(closingListMessage1.get(i).getTotalCount());
    			cells = rows.createCell(3);
    			cells.setCellValue(closingListMessage1.get(i).getSumMoney());
    			cells = rows.createCell(4);	    			 
    			cells.setCellValue(closingListMessage1.get(i).getNowCount());
    			cells = rows.createCell(5);
    			cells.setCellValue(closingListMessage1.get(i).getCountYear());
    			cells = rows.createCell(6);
    			cells.setCellValue(closingListMessage1.get(i).getMoneyCount());
    			cells = rows.createCell(7);
    			cells.setCellValue(closingListMessage1.get(i).getCountMonth());
    			cells = rows.createCell(8);
    			cells.setCellValue(closingListMessage1.get(i).getMoneyMonth());
    			cells = rows.createCell(9);
    			cells.setCellValue(closingListMessage1.get(i).getCountYearMonth());
    			cells = rows.createCell(10);
    			cells.setCellValue(closingListMessage1.get(i).getMoneyYearMonth());
    				
    		}
    		
			try {
				OutputStream out = response.getOutputStream();
				wbk.write(out);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
	}catch (Exception e) {
		
	}
}
}

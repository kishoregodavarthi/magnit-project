package org.magnit.HappienessIndex;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@EnableWebMvc
@Controller
public class RestController {
	
	@Autowired
	Util util;
	
	
	@CrossOrigin
	@RequestMapping(value = "/get-questions", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<List<Question>> getQuestions()
    {
        
		return new ResponseEntity<List<Question>>(util.questions, HttpStatus.OK);
    }
	
	@CrossOrigin
	@RequestMapping(value = "/submit-questions", method = RequestMethod.POST)
	@ResponseBody
	String submitQuestions(@RequestBody Map<String,String> allRequestParams)
    {
        
		for(Map.Entry<String,String> me:allRequestParams.entrySet()) {
			boolean isThere = false;
			for(Question q:util.questions) {
				if(q.getName().equalsIgnoreCase(me.getKey())) {
					isThere = true;
					break;
				}
			}
			if(!isThere) {
				Question q=new Question();
				q.setName(me.getKey());
				util.questions.add(q);
			}
			Index e=new Index();
			e.setQuestion(me.getKey());
			e.setAnswer(me.getValue());
			//util.index.add(e);
			Integer ii=util.index.get(me.getKey()+"##"+me.getValue());
			if(ii==null) {
				ii= 0;
			}
			ii = ii+1;
			util.index.put(me.getKey()+"##"+me.getValue(),ii);
		}
		return "success";
    }
	
	@CrossOrigin
	@RequestMapping(value = "/get-report", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<Resource> getReport(@RequestParam String reportType)
    {
        String reportName ="report."+reportType;
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+reportName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        Map<String, Integer> report=new HashMap<>();
        for(Question q:util.questions) {
        	Integer ii=util.index.get(q.getName()+"##Yes");
        	if(ii!=null) {
        		report.put(q.getName()+"##Yes", ii);
        	}
        	ii=util.index.get(q.getName()+"##No");
        	if(ii!=null) {
        		report.put(q.getName()+"##No", ii);
        	}
        }
        ByteArrayResource resource = new ByteArrayResource(getReportContent(report,reportType));
        
		return ResponseEntity.ok()
                .headers(header)
                //.contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }
	
	byte[] getReportContent(Map<String, Integer> report,String rptType) {
		if(rptType.equalsIgnoreCase("txt")) {
			StringBuffer sb=new StringBuffer();
			if(!report.isEmpty()) {
				sb.append("format is (<question>\\t<answer>\\t<total_count>)");
				sb.append("\n");
				sb.append("\n");
				for(Map.Entry<String, Integer> me:report.entrySet()) {
					sb.append(me.getKey().split("##")[0]+"\t"+me.getKey().split("##")[1]+"\t"+me.getValue()+"\n");
				}
			}else {
				sb.append("No data available");
			}
			return sb.toString().getBytes();
		}else {
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("report");
			int r=0;
			if(!report.isEmpty()) {
				Row row = sheet.createRow(++r);
				int c=0;
				Cell cell = row.createCell(++c);
				cell.setCellValue("Question");
				cell = row.createCell(++c);
				cell.setCellValue("Answer");
				cell = row.createCell(++c);
				cell.setCellValue("Total Count");
				for(Map.Entry<String, Integer> me:report.entrySet()) {
					c=0;
					row = sheet.createRow(++r);
					cell = row.createCell(++c);
					cell.setCellValue(me.getKey().split("##")[0]);
					cell = row.createCell(++c);
					cell.setCellValue(me.getKey().split("##")[1]);
					cell = row.createCell(++c);
					cell.setCellValue(""+me.getValue());
				}
			}else {
				Row row = sheet.createRow(++r);
				int c=0;
				Cell cell = row.createCell(++c);
				cell.setCellValue("No data available");
			}
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			try {
				workbook.write(bos);
				bos.flush();
			}catch(Exception e) {
				
			}
			return bos.toByteArray();
		}
	}
	public static void fillSheet(XSSFSheet sheet,ArrayList<LinkedHashMap<String, String>>  l) {
		int r=0;
		if(!l.isEmpty()) {
			LinkedHashMap<String, String> m1 =l.get(0);
			Row row = sheet.createRow(++r);
			int c=0;
			for(String s:m1.keySet()) {
				Cell cell = row.createCell(++c);
				cell.setCellValue(s);
			}
			for(LinkedHashMap<String, String> m:l) {
				row = sheet.createRow(++r);
				c=0;
				for(Map.Entry<String, String> me:m.entrySet()) {
					Cell cell = row.createCell(++c);
					cell.setCellValue(me.getValue());
				}
			}
		}
	}
	@Bean
	Util loadUtils() {
		List<Question> ll = new ArrayList<>();
		Question q1 = new Question();
		q1.setName("Employee most happy during weekend");
		ll.add(q1);
		q1 = new Question();
		q1.setName("Employee most happy during weekday");
		ll.add(q1);
		q1 = new Question();
		q1.setName("Employee is sad because of work");
		ll.add(q1);
		q1 = new Question();
		q1.setName("Employee is sad because of personal");
		ll.add(q1);
		q1 = new Question();
		q1.setName("Employee enjoys the work");
		ll.add(q1);
		Util uu = new Util();
		uu.questions = ll;
		return uu;
	}
	
}

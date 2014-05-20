package uk.co.javawork.svcs.download.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

@Controller
public class DefaultWebController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWebController.class);
	
	@RequestMapping(value="/login", method={RequestMethod.GET})
	public String login(){
		//required for spring security
		return "/login";
	}
	
	@RequestMapping(value="/", method={RequestMethod.GET})
	public String hello(){
		//default to the files list view
		return "redirect:/files";
	}

	@RequestMapping("/cass")
	@ResponseBody
	public String testCassandra(){
		
		final long start = System.nanoTime();
		
		Cluster c = Cluster.builder().addContactPoint("localhost").build();
		Session s = c.connect("testkeyspace");
		
		final long cTime = System.nanoTime();
		LOGGER.debug("Connected in " + ((cTime - start) / 1000_000));
		
		ResultSet rs = s.execute("select * from users");
		List<Row> rows = rs.all();
		
		final long qTime = System.nanoTime();
		LOGGER.debug("Queried in " + ((qTime - cTime) / 1000_000));
		
		StringBuilder sb = new StringBuilder();
		
		for(Row r : rows){
			String lname = r.getString("lname");
			sb.append(" *lname : ").append(lname);
		}
		
		s.shutdown();
		c.shutdown();
		
		return sb.toString();
	}
}

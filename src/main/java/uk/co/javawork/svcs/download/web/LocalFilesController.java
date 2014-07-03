package uk.co.javawork.svcs.download.web;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller()
public class LocalFilesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalFilesController.class);
	
	@Value("${storage-dir}")
	private String storagePath;

	@RequestMapping(value="/files-json", method={RequestMethod.GET}, produces={"application/json"})
	@ResponseBody
	public Map<String, Long> listLocalFilesJson() {
		
		return listFiles();
	}
	
	@RequestMapping(value="/files", method={RequestMethod.GET})
	public String listLocalFilesHtml(HttpServletRequest req) {
		
		Map<String, Long> files = listFiles();
		
		req.setAttribute("files", files);
		
		return "/list-files";
	}
	
	private Map<String, Long> listFiles(){

		
		Map<String, Long> files = new TreeMap<>(new CaseUnsensitiveComparator());
		
		File tmpDir = new File(storagePath);
		File[] all = tmpDir.listFiles();
		
		//just in case the folder doesn't exist
		if(all != null){

			for(File f : all){
				
				if(f.canRead() && f.isFile()){
					files.put(f.getName(), f.length());
				}
			}
		}

		
		return files;
	}
	
	@RequestMapping(value="/files/{name:.+}", method={RequestMethod.GET})
	@ResponseBody
	public FileSystemResource getLocalFile(@PathVariable String name){
		
		File f = new File(storagePath, name);
		return new FileSystemResource(f);
	}
	
	@RequestMapping(value="/delete/{name:.+}", method={RequestMethod.GET})
	public String deleteLocalFile(HttpServletRequest req, @PathVariable String name){
		
		File f = new File(storagePath, name);
		
		if(f.exists()){
			
			if(f.canWrite()){
				
				boolean b = f.delete();
				
				if(b){
					req.setAttribute("infomsg", "File deleted: " + name);
				}else{
					req.setAttribute("errormsg", "Delete failed - not sure why though.");
				}
				
			}else{
				
				req.setAttribute("errormsg", "Delete failed - insufficient permissions: " + name);
			}
			
		}else{
			
			req.setAttribute("errormsg", "Delete failed - file does not exist: " + name);
		}
		
		return listLocalFilesHtml(req);
	}
	
	private class CaseUnsensitiveComparator implements Comparator<String> {
		
		@Override
		public int compare(String s1, String s2) {
			
			String l1 = s1.toLowerCase();
			String l2 = s2.toLowerCase();
			
			return l1.compareTo(l2);
		}
	}
}

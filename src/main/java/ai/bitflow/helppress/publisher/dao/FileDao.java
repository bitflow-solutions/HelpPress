package ai.bitflow.helppress.publisher.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import ai.bitflow.helppress.publisher.domain.Contents;
import ai.bitflow.helppress.publisher.domain.ContentsGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileDao {

	private final Logger logger = LoggerFactory.getLogger(FileDao.class);
	
	@Value("${app.upload.root.path}")
	private String UPLOAD_ROOT_PATH;

    @Autowired
    private SpringTemplateEngine tengine;
    
    @PostConstruct
    public void init() {
    	this.tengine.setTemplateResolver(templateResolver()); 
	}
	
	/**
	 * 도움말 HTML 파일 생성
	 * @param item
	 * @return
	 * @throws IOException
	 */
	public boolean newContentFile(Contents item) {
		
		File dir = new File(UPLOAD_ROOT_PATH);
		if (!dir.exists()) {
			boolean success = dir.mkdirs();
		}
		 
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					UPLOAD_ROOT_PATH + File.separator + String.format("%05d" , item.getId()) + ".html"), "UTF-8"));
			writer.write(getHeader(item.getTitle()));
			if (item.getContent()!=null) {
				writer.write(item.getContent());
			}
			writer.write(getFooter());
//			HtmlConverter.convertToPdf(html, new FileOutputStream(dest));

		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException e) { }
			}
		}
	}
	
	/**
	 * 전체 도움말 그룹 HTML 재생성
	 * @param list
	 * @return
	 */
	public boolean makeAllContentGroupHTML(List<ContentsGroup> list) {
		// All contents group
		for (int i=0; i<list.size(); i++) {
			ContentsGroup item1 = list.get(i);
			item1.setClassName("on");
			// Write to HTML file
			Context ctx = new Context();
			ctx.setVariable("group", list);
			ctx.setVariable("tree",  item1.getTree());
			String htmlCodes = this.tengine.process("hp-group-template.html", ctx);
			makeNewContentGroupTemplate(item1, htmlCodes);
			item1.setClassName("");
			if (i==0) {
				ctx.setVariable("targetHtml", item1.getGroupId() + ".html");
//				this.tengine.setTemplateResolver(new ClassLoaderTemplateResolver());
				String indexHtmlCodes = this.tengine.process("hp-index-redirection.html", ctx);
				makeNewIndexRedirectionHtml(indexHtmlCodes);
			}
		}
		return true;
	}
	
    private FileTemplateResolver templateResolver() {
    	FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("./export/");
        resolver.setSuffix(".html");
        resolver.setCacheable(false);
        return resolver;
    }
	
	/**
	 * 
	 * @param item
	 * @param htmlCodes
	 * @return
	 */
	public boolean makeNewContentGroupTemplate(ContentsGroup item, String htmlCodes) {
		
		File dir = new File(UPLOAD_ROOT_PATH);
		if (!dir.exists()) {
			boolean success = dir.mkdirs();
		}
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					UPLOAD_ROOT_PATH + File.separator + item.getGroupId() + ".html"), "UTF-8"));
			writer.write(htmlCodes);
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException e) { }
			}
		}
	}
	
	public boolean makeNewIndexRedirectionHtml(String htmlCodes) {
		
		File dir = new File(UPLOAD_ROOT_PATH);
		if (!dir.exists()) {
			boolean success = dir.mkdirs();
		}
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					UPLOAD_ROOT_PATH + File.separator + "index.html"), "UTF-8"));
			writer.write(htmlCodes);
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException e) { }
			}
		}
	}

	private String getHeader(String title) {
		return "<!doctype html><html><head><meta charset=\"utf-8\">"
				 + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no, shrink-to-fit=no\">"
				 + "<title>" + title + "</title></head><body>";
	}

	private String getFooter() {
		return "</body></html>";
	}
	
}

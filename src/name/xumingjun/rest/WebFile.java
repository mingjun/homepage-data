package name.xumingjun.rest;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import name.xumingjun.rest.bean.AbstractJsonBean;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@Path("/files")
public class WebFile {

	@Context
	private HttpServletRequest request;
	
	@Context 
	private HttpServletResponse response;
	
	private final static int BUFF_SIZE = 1024;
	private final static int MAX_FILE_SIZE = 1024 * 1024 * 1024;
	public final static File PARENCT_DIR = new File("/home/mingjun/www/share/");
	
	static {
		if(!PARENCT_DIR.isDirectory()) {
			PARENCT_DIR.delete();
		}
		if(!PARENCT_DIR.exists()) {
			PARENCT_DIR.mkdirs();
		}
	}
	
	private static DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response upload(@QueryParam("callback") @DefaultValue("console.debug") String callbackName) {
		response.setHeader("Content-Type", MediaType.TEXT_HTML);
		ServletOutputStream out = null;
		
		
		String fileUrl = "/share/";
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(BUFF_SIZE);
		factory.setRepository(PARENCT_DIR);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAX_FILE_SIZE);
		
		
		try {
			out = response.getOutputStream();
			writeHtmlHead(out);
			setProgressListener(upload);
			
			for (Object item : upload.parseRequest(request)) {
				FileItem fi = (FileItem) item;
				String fileName = fi.getName();
				if (!fi.isFormField() && fileName.length() != 0 ) {
					File newFile = new File(PARENCT_DIR, fileName);
					if(newFile.exists()) {
						newFile = createPeerFile(PARENCT_DIR, fileName);
					}
					fi.write(newFile);
					fileUrl += newFile.getName();
				}
			}
			writeHtmlScript(out, callbackName, AbstractJsonBean.gson.toJson(fileUrl));
			writeHtmlEnd(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != out) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		return Response.ok().build();
	}
	
	public void setProgressListener(ServletFileUpload upload) {
		final long startTime = System.currentTimeMillis();
		final UploadProgress progress = new UploadProgress();
		
		upload.setProgressListener(new ProgressListener(){
			private int lastPercentage = 0;
			private long lastTime = startTime;
			@Override
			public void update(long pBytesRead, long pContentLength, int _) {
				int percentage = (int) Math.round(pBytesRead*100/(double)(pContentLength));
				long now = System.currentTimeMillis();
				if( percentage > lastPercentage && now - lastTime > 100) { // every +1% && after 0.1s
					
					StringBuffer sb = new StringBuffer();
					sb.append("time =").append(df.format(new Date(now))).append("\t")
						.append(percentage).append("% ")
						.append("out of ").append(pContentLength).append(" Bytes");
					String message = sb.toString();
					
					// write to session object
					progress.uploadSize = pBytesRead;
					progress.totalSize = pContentLength;
					progress.percentage = percentage;
					progress.timePoint = now;
					System.out.println(message);//TODO
					
					//set for last
					lastPercentage = percentage;
					lastTime = now;
				}
			}
		});
	}
	
	
	public File createPeerFile(File dir, String fileName) {
		String ext = null, pre = null;
		int index = fileName.lastIndexOf('.');
		if(index >= 0) { // .ext
			pre = fileName.substring(0, index);
			ext = fileName.substring(index);
		} else {
			pre = fileName;
			ext = "";
		}
		int i = 1;
		File newFile = null;
		do {
			String newName = pre + "_"+(i++) + ext;
			newFile = new File(PARENCT_DIR, newName);
		} while(newFile.exists());
		return newFile;
	}

	protected void writeHtmlHead(ServletOutputStream out) throws IOException {
		out.println("<!DOCTYPE html>");
		out.println("<html><head><meta charset='UTF-8'></head><body>");
		out.flush();
	}
	
	protected void writeHtmlScript(ServletOutputStream out, String callback, String arg)  throws IOException {
		String template = "<script type='text/javascript'>{callback} && {callback}({arg});</script>";
		out.println(template.replaceAll("\\{callback\\}", callback).replaceAll("\\{arg\\}", arg));
		out.flush();
	}
	protected void writeHtmlText(ServletOutputStream out, String message)  throws IOException {
		out.println(message);
		out.flush();
	}
	
	protected void writeHtmlEnd(ServletOutputStream out) throws IOException {
		out.println("</body></html>");
		out.flush();
	}
	
	class UploadProgress extends AbstractJsonBean{
		long uploadSize;
		long totalSize;
		int percentage;
		long timePoint;
	}
	
	@GET
	public Response getUploadInfo(@QueryParam("callback") @DefaultValue("console.debug") String callbackName) {
		response.setHeader("Content-Type", MediaType.TEXT_HTML);
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			writeHtmlHead(out);
			for(int i=0;i<10;i++) {
				writeHtmlScript(out, callbackName, String.valueOf(i));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			
			writeHtmlEnd(out);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != out) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		return Response.ok().build();
	}
}

package name.xumingjun.rest;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import name.xumingjun.rest.bean.AbstractJsonBean;
import name.xumingjun.util.SessionAccessor;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@Path("/files")
public class WebFile {

	@Context
	private HttpServletRequest request;
	private final static int BUFF_SIZE = 1024;
	private final static int MAX_FILE_SIZE = 1024 * 1024 * 1024;
	public final static File PARENCT_DIR = new File("/home/mingjun/www/share/");
	public final static String PARENCT_DIR_URL = "/share/";
	public final static String ATTRIBUTE_NAME_IN_SESSION = "file-upload-status-Observable";
	class FileInfo {
		String srcName, serverCopyURL;
	}
	public Map<String, FileInfo> fileLookup = new HashMap<String, FileInfo>();
	static {
		if(!PARENCT_DIR.isDirectory()) {
			PARENCT_DIR.delete();
		}
		if(!PARENCT_DIR.exists()) {
			PARENCT_DIR.mkdirs();
		}
	}
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String upload() {
		UploadProgressObservable notifier = createObservableToSession(request.getSession());
		String result = "{}";
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(BUFF_SIZE);
		factory.setRepository(PARENCT_DIR);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAX_FILE_SIZE);
		UploadedResult jsonResult = new UploadedResult();
		try {
			setProgressListener(upload, notifier);
			int index = 0;
			for (Object item : upload.parseRequest(request)) {
				index ++;
				FileItem fi = (FileItem) item;
				String fileName = fi.getName();
				if (!fi.isFormField() && fileName.length() != 0 ) {
					File newFile = new File(PARENCT_DIR, fileName);
					if(newFile.exists()) {
						newFile = createPeerFile(PARENCT_DIR, fileName);
					}
					fi.write(newFile);
					String uploadFileName = newFile.getName();
					String uploadFileUrl = new URI(
							request.getScheme(), null,
							request.getLocalAddr(), -1,
							PARENCT_DIR_URL+uploadFileName, null, null).toString();
					jsonResult.addDetail(index, fileName, uploadFileName, uploadFileUrl);
					System.out.println(index + "\t" + fileName + "\t->\t" + uploadFileUrl);
				}
			}
			result = jsonResult.toJson();
			notifier.sendMessage(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	void setProgressListener(ServletFileUpload upload, final UploadProgressObservable notifier) {
		final long startTime = System.currentTimeMillis();
		final UploadProgress progress = new UploadProgress();
		upload.setProgressListener(new ProgressListener() {
			private int lastPercentage = 0;
			private long lastTime = startTime;
			@Override
			public void update(long pBytesRead, long pContentLength, int index) {
				int percentage = (int) Math.round(pBytesRead*100/(double)(pContentLength));
				long now = System.currentTimeMillis();
				if( percentage > lastPercentage && now - lastTime > 100) { // every +1% && after 0.1s
					// write to an observable target in session
					progress.uploadSize = pBytesRead;
					progress.totalSize = pContentLength;
					progress.percentage = percentage;
					progress.fileIndex = index;
					progress.timePoint = now;
					System.out.println(progress.toJson());
					notifier.sendMessage(progress.toJson());
					//set current as last, for next loop
					lastPercentage = percentage;
					lastTime = now;
				}
			}
		});
	}
	File createPeerFile(File dir, String fileName) {
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

	public static class UploadProgressObservable extends Observable {
		public void sendMessage(String value) {
			this.setChanged();
			this.notifyObservers(value);
		}
	}
	public static UploadProgressObservable createObservableToSession(HttpSession session) {
		UploadProgressObservable obs = new UploadProgressObservable();
		return SessionAccessor.touchSessionAttribute(session, ATTRIBUTE_NAME_IN_SESSION, UploadProgressObservable.class, obs);
	}
}
abstract class FileUploadBean extends AbstractJsonBean {
	String messageType;
	public FileUploadBean() {
		messageType = this.getClass().getSimpleName();
	}
}
class UploadProgress extends FileUploadBean {
	long uploadSize;
	long totalSize;
	int percentage;
	int fileIndex;
	long timePoint;
}

class DetailedResult {
	int index;
	String fileName;
	String uploadedName;
	String uplaodedUrl;
}
class UploadedResult extends FileUploadBean {
	List<DetailedResult> details = new ArrayList<DetailedResult>();
	public void addDetail(int index, String src, String target, String url) {
		DetailedResult d = new DetailedResult();
		d.index = index;
		d.fileName = src;
		d.uploadedName = target;
		d.uplaodedUrl = url;
		details.add(d);
	}
}

package name.xumingjun.rest;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
	public final static String OBSERVERABLE_KEY_IN_SESSION = "file-upload-status-Observable";
	public final static String QUEUE_KEY_IN_SESSION = "file-upload-status-queue";
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
		Queue<UploadedResult> uploadedFiles = createResultQueueToSession(request.getSession());
		ArrayList<UploadedResult> array = new ArrayList<UploadedResult>();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(BUFF_SIZE);
		factory.setRepository(PARENCT_DIR);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAX_FILE_SIZE);

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
					UploadedResult fileInfo = new UploadedResult();
					fileInfo.index = index;
					fileInfo.fileName = fileName;
					fileInfo.uploadedName =  newFile.getName();
					fileInfo.uploadedUrl = new URI(
							request.getScheme(), null,
							request.getLocalAddr(), -1,
							PARENCT_DIR_URL+ fileInfo.uploadedName, null, null).toString();
					uploadedFiles.offer(fileInfo); //enqueue
					array.add(fileInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AbstractJsonBean.gson.toJson(array);
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String GetUploadResult() {
		Queue<UploadedResult> uploadedFiles = createResultQueueToSession(request.getSession());
		String result = AbstractJsonBean.gson.toJson(uploadedFiles);
		uploadedFiles.clear();
		return result;
	}

	public static class UploadProgressObservable extends Observable {
		public void sendMessage(String value) {
			this.setChanged();
			this.notifyObservers(value);
		}
	}
	public static UploadProgressObservable createObservableToSession(HttpSession session) {
		UploadProgressObservable obs = new UploadProgressObservable();
		return SessionAccessor.touchSessionAttribute(session, OBSERVERABLE_KEY_IN_SESSION, UploadProgressObservable.class, obs);
	}

	@SuppressWarnings("unchecked")
	public static Queue<UploadedResult> createResultQueueToSession(HttpSession session) {
		Queue<UploadedResult> queue = new LinkedList<UploadedResult>();
		return SessionAccessor.touchSessionAttribute(session, QUEUE_KEY_IN_SESSION, Queue.class, queue);
	}
}

class UploadProgress extends AbstractJsonBean {
	long uploadSize;
	long totalSize;
	int percentage;
	int fileIndex;
	long timePoint;
}

class UploadedResult extends AbstractJsonBean {
	int index;
	String fileName;
	String uploadedName;
	String uploadedUrl;
}

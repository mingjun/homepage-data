package name.xumingjun.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Observable;
import java.util.Observer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.xumingjun.rest.WebFile;
import name.xumingjun.rest.WebFile.UploadProgressObservable;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

/**
 * Servlet implementation class WebSocketServlet
 */
@WebServlet("/files/status")
public class FileUploadStatusServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	UploadProgressObservable progressTarget;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		progressTarget = WebFile.createObservableToSession(request.getSession());
		super.doGet(request, response);
	}

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1) {
		return new ProgressPipeIn(progressTarget);
	}
}

class ProgressPipeIn extends MessageInbound {
	Observer progressListener;
	UploadProgressObservable progressTarget;
	public ProgressPipeIn(UploadProgressObservable t) {
		this.progressTarget = t;
	}

	@Override
	protected void onClose(int status) {
		progressTarget.deleteObserver(progressListener);
		super.onClose(status);
	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		super.onOpen(outbound);
		final WsOutbound out = this.getWsOutbound();
		this.progressListener = new Observer(){
			@Override
			public void update(Observable observable, Object data) {
				try {
					out.writeTextMessage(CharBuffer.wrap(String.valueOf(data)));
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		progressTarget.addObserver(progressListener);
	}

	@Override
	protected void onBinaryMessage(ByteBuffer bin) throws IOException {
	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
	}
}

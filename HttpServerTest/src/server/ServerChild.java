package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.StringTokenizer;

public class ServerChild implements Runnable {
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private String encoding = "gbk";

	public ServerChild(Socket socket) {
		this.socket = socket;
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		String line = "";
		String method = "";
		String resource = "";

		// ��ȡ������,��ȡ���󷽷���������Դ
		try {
			line = readLine(is);
			StringTokenizer tokenizer = new StringTokenizer(line);
			method = tokenizer.nextToken();
			resource = tokenizer.nextToken();
			resource = URLDecoder.decode(resource, encoding);

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("method:" + method + ",resource:" + resource);

		if (method.equals("GET")) {
			// ȷ����Դ
			File file = new File("webapp" + resource);
			FileInputStream fis = null;
			if (!file.exists()) {
				PrintStream out = new PrintStream(os);
				out.println("HTTP/1.0 200 OK");// ����Ӧ����Ϣ,������Ӧ��
				out.println("Content-Type: text/html;charset=" + encoding);
				// ���������ֽ���
				out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ
				out.println("<html><body>not found resource!</body></html>");
			} else {
				PrintStream out = new PrintStream(os);
				out.println("HTTP/1.0 200 OK");// ����Ӧ����Ϣ,������Ӧ��
				out.println("Content-Type: text/html;charset=" + encoding);
				out.println("Content-Length: " + file.length());// ���������ֽ���
				out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ

				try {
					fis = new FileInputStream(file);
					byte[] buf = new byte[512];
					while (fis.read(buf) != -1) {
						out.write(buf);
					}

					fis.close();
				} catch (IOException e) {

				} finally {
					try {
						out.close();
						fis.close();
						is.close();
						os.close();
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	}

	/**
	 * ��ȡhttp������һ�е���Ϣ
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String readLine(InputStream is) throws IOException {
		String readLine = "";
		byte[] buf = new byte[1];

		do {
			is.read(buf);
			readLine += new String(buf, encoding);
		} while (buf[0] != 10);

		return readLine;
	}

}

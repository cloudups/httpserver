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

		// 获取请求行,获取请求方法和请求资源
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
			// 确定资源
			File file = new File("webapp" + resource);
			FileInputStream fis = null;
			if (!file.exists()) {
				PrintStream out = new PrintStream(os);
				out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
				out.println("Content-Type: text/html;charset=" + encoding);
				// 返回内容字节数
				out.println();// 根据 HTTP 协议, 空行将结束头信息
				out.println("<html><body>not found resource!</body></html>");
			} else {
				PrintStream out = new PrintStream(os);
				out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
				out.println("Content-Type: text/html;charset=" + encoding);
				out.println("Content-Length: " + file.length());// 返回内容字节数
				out.println();// 根据 HTTP 协议, 空行将结束头信息

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
	 * 读取http请求报文一行的信息
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

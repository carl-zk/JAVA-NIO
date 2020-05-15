package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 2. Threaded blocking server
 * 
 * handle accepted request in new thread, the number of creating-thread is
 * limited. outrange would encounter OOM.
 * 
 * @author carl
 *
 */
public class BlockingServerThreaded {

	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(8080);
		while (true) {
			Socket s = ss.accept();
			System.out.println(s);
			new Thread(() -> handle(s)).start();
		}
	}

	private static void handle(Socket s) {
		try (InputStream in = s.getInputStream(); OutputStream out = s.getOutputStream();) {
			int data;
			while ((data = in.read()) != -1) {
				data = transmogrify(data);
				out.write(data);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static int transmogrify(int data) {
		return Character.isLetter(data) ? data ^ ' ' : data;
	}
}

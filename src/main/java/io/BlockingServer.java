package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 1. Simple blocking server
 * 
 * only accept request after previous one disconnected.
 * 
 * command line: <br/>
 * telnet localhost 8080 <br/>
 * lsof -i :8080
 * 
 * @author carl
 *
 */
public class BlockingServer {

	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(8080);
		while (true) {
			Socket s = ss.accept();
			handle(s);
		}
	}

	private static void handle(Socket s) {
		System.out.println(s);
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

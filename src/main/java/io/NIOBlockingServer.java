package io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 4. NIO blocking
 * 
 * @author carl
 *
 */
public class NIOBlockingServer {
	public static void main(String[] args) throws IOException {
		ServerSocketChannel ss = ServerSocketChannel.open();
		ss.bind(new InetSocketAddress(8080));
		while (true) {
			SocketChannel s = ss.accept();
			// blocking, never null
			System.out.println(s);
			handle(s);
		}
	}

	private static void handle(SocketChannel s) {
		ByteBuffer buf = ByteBuffer.allocateDirect(80);
		try {
			int data;
			while ((data = s.read(buf)) != -1) {
				buf.flip();
				transmogrify(buf);
				while (buf.hasRemaining()) {
					s.write(buf);
				}
				buf.compact();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	private static void transmogrify(ByteBuffer buf) {
		for (int i = 0; i < buf.limit(); i++) {
			buf.put(i, (byte) transmogrify(buf.get(i)));
		}

	}

	private static int transmogrify(int data) {
		return Character.isLetter(data) ? data ^ ' ' : data;
	}
}

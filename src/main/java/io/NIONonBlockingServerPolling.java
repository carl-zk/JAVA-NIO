package io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 5. NIO non-blocking polling
 * 
 * just for showing the idea of how to handle multi-requests in one thread.
 * 
 * @author carl
 *
 */
// bad class, don't do this, cuz this method wastes CPU.
public class NIONonBlockingServerPolling {
	public static void main(String[] args) throws IOException {
		ServerSocketChannel ss = ServerSocketChannel.open();
		ss.bind(new InetSocketAddress(8080));
		ss.configureBlocking(false);
		Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();
		while (true) {
			SocketChannel s = ss.accept();
			// non-blocking, almost always null
			if (s != null) {
				System.out.println(s);
				s.configureBlocking(false);
				sockets.put(s, ByteBuffer.allocateDirect(80));
			}
			sockets.keySet().removeIf(socketChannel -> !socketChannel.isOpen());
			sockets.forEach(NIONonBlockingServerPolling::handle);
		}
	}

	private static void handle(SocketChannel socket, ByteBuffer buf) {
		try {
			int data = socket.read(buf);
			if (data == -1) { // channel has reached end-of-stream
				close(socket);
			} else if (data != 0) { // do have data
				buf.flip();
				transmogrify(buf);
				while (buf.hasRemaining()) {
					socket.write(buf);
				}
				buf.compact();
			}
		} catch (IOException e) {
			close(socket);
			throw new UncheckedIOException(e);
		}
	}

	private static void close(SocketChannel socket) {
		try {
			socket.close();
		} catch (IOException ignore) {
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

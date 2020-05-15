package io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 6. NIO non-blocking selector
 * 
 * standard NIO non-blocking example
 * 
 * @author carl
 *
 */
public class NIONonBlockingServerSelector {
	static Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

	public static void main(String[] args) throws IOException {
		ServerSocketChannel ss = ServerSocketChannel.open();
		ss.bind(new InetSocketAddress(8080));
		ss.configureBlocking(false);

		Selector selector = Selector.open();
		ss.register(selector, SelectionKey.OP_ACCEPT);
		while (true) {
			selector.select(); // blocking
			Set<SelectionKey> keys = selector.selectedKeys();
			for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
				SelectionKey key = it.next();
				it.remove();
				try {
					if (key.isValid()) {
						if (key.isAcceptable()) {
							accept(key);
						} else if (key.isReadable()) {
							read(key);
						} else if (key.isWritable()) {
							write(key);
						}
					}
				} catch (IOException e) {
					System.err.println(e);
				}
			}
			sockets.keySet().removeIf(socketChannel -> !socketChannel.isOpen());
		}
	}

	private static void accept(SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel s = ssc.accept();
		// non-blocking, never null
		System.out.println(s);
		s.configureBlocking(false);
		s.register(key.selector(), SelectionKey.OP_READ);
		sockets.put(s, ByteBuffer.allocateDirect(1024));
	}

	private static void read(SelectionKey key) throws IOException {
		System.out.println("read..........");
		SocketChannel s = (SocketChannel) key.channel();
		ByteBuffer buf = sockets.get(s);
		int data = s.read(buf);
		if (data == -1) {
			close(s);
			sockets.remove(s);
			System.out.println("close....");
			return;
		}
		buf.flip();
		transmogrify(buf);
		key.interestOps(SelectionKey.OP_WRITE);
	}

	private static void write(SelectionKey key) throws IOException {
		System.out.println("write......");
		SocketChannel s = (SocketChannel) key.channel();
		ByteBuffer buf = sockets.get(s);
		System.out.println(buf.limit());
		s.write(buf); // won't always write everything
		if (!buf.hasRemaining()) {
			buf.compact();
			key.interestOps(SelectionKey.OP_READ);
		} else {
			System.out.println("buf has some left &&&&");
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

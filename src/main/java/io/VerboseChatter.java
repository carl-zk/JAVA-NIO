package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class VerboseChatter {

	public static void main(String[] args) throws UnknownHostException, IOException {
		fun1();
	}

	static void fun1() throws UnknownHostException, IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append("hello world");
		}
		Socket s = new Socket("localhost", 8080);
		PrintStream out = new PrintStream(s.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()), 32);

		for (int i = 0; i < 1; i++) {
			out.println(sb);
			System.out.println(in.readLine()); // readLine() is not recommend.

		}
		s.close();
	}
}

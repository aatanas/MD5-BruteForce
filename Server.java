package bezb_proj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

public class Server {

	public static void main(String[] args) throws Exception {
		ServerSocket ss = new ServerSocket(2021);
		Socket sock;
		
		while((sock = ss.accept()) != null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()),true);
			BufferedReader tin = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Unesi poruku za hesiranje:");
			
			String poruka = tin.readLine();
			
			MessageDigest hash = MessageDigest.getInstance("MD5");
			
			byte[] hes = hash.digest(poruka.getBytes());
			
			out.println(bytesToHex(hes));
			
			sock.close();
		}
		ss.close();

	}
	
	public static String bytesToHex(byte[] data) {
		StringBuffer results = new StringBuffer();
		for (byte byt : data)
			results.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return results.toString();
	}

}

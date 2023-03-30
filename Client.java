package bezb_proj;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;

public class Client {

	public static void main(String[] args) throws Exception {
		
		final BufferedReader tin = new BufferedReader(new InputStreamReader(System.in));
		final Socket sock = new Socket("localhost",2021);
		final BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		final PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()),true);
		
		final String hes = in.readLine();
		System.out.println("MD5: "+hes);

		final byte[] md5 = decodeUsingBigInteger(hes);
		
		in.close(); out.close(); 
		sock.close();
		
		System.out.println("Unesi duzinu za pogadjanje:");
		final int len = Integer.parseInt(tin.readLine());
		/*System.out.println("Unesi donju granicu:");
		final byte min = (byte) tin.readLine().charAt(0);
		System.out.println("Unesi gornju granicu:");
		final byte max = (byte) tin.readLine().charAt(0)+1;*/
		final byte min = 'a'; final byte max = 'z'+1;
		System.out.println("Unesi broj niti("+Runtime.getRuntime().availableProcessors()+"):");
		final int niti = Integer.parseInt(tin.readLine());
		tin.close();
		final int mod = (max-min)%niti;
		final int podeok = (max-min)/niti;
		int tmin = min, tmax;
		for(int i = 0; i < niti; i++) {
			tmax = tmin + (i < mod ? podeok+1 : podeok);
			// 26 karaktera na 4 niti:
			// 7 	  7		 6 		 6
			//[0,7) [7,14) [14,20) [20, 26) 
			new Thread(new Runnable() {
				private int tmin;
				private int tmax;
				private int br;
				
				public Runnable init(int tmin, int tmax, int br) {
					this.tmin = tmin;
					this.tmax = tmax;
					this.br = br;
					return this;
				}
				
				@Override
				public void run() {
					final byte tmin = (byte)this.tmin;
					final byte tmax = (byte)this.tmax;
					final int brTreda = this.br;
					
					MessageDigest hash = null;
					try { hash = MessageDigest.getInstance("MD5");} 
					catch (Exception e) {e.printStackTrace();}
					
					byte[] niz = {tmin};
					boolean nasao = false;
					System.out.println("Thread["+brTreda+"] Running...");
					long t = System.nanoTime();
					int i = 0;
					while(true) { 
			    		if(niz[i] == tmax && i == niz.length-1 ) {
			    			if(++i == len) break;
			    			niz = new byte[i+1];
		    				Arrays.fill(niz,0,i,min);
		    				niz[i] = tmin;
			    		}
			    		else if(niz[i] == max) {
			    			niz[i] = min; 
			    			++niz[++i];
			    		}
			    		else{
			    			if(Arrays.equals(hash.digest(niz),md5)) {
			    				nasao=true; break;
			    			}
			    			++niz[i=0]; 
			    		} 
			    	}
					t = System.nanoTime() - t;
			    	double ts = t / 1e9;
			    	System.out.println("Thread["+brTreda+"] Vreme: " + ts+"s");
			    	if(nasao) {
			    		System.out.println("Thread["+brTreda+"] Vrednost je: "+new String(niz));
			    		System.exit(0);
			    	}
			    	else System.out.println("Thread["+brTreda+"] nije nasao :(");
					
				}
			}.init(tmin,tmax,i)).start();
			tmin=tmax;
		}
	}
	
	private static byte[] decodeUsingBigInteger(String hexString) throws Exception {
		byte[] byteArray = new BigInteger(hexString, 16).toByteArray();
	    if (byteArray[0] == 0) {
	        byteArray = Arrays.copyOfRange(byteArray, 1, byteArray.length);
	    }
	    return byteArray;
	}

}

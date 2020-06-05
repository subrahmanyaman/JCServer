import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.sun.javacard.apduio.CadTransportException;
 
/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class JCServer {
	private static byte[] STATUS_OK = Utils.hexStringToByteArray("9000");
	private static JavaCardHostApp hostApp = new JavaCardHostApp();
 
    public static void main(String[] args) {
        if (args.length < 1) {
        	System.out.println("Port no is expected as argument.");
        	return;
        }
 
        int port = Integer.parseInt(args[0]);
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
        	initaliseSimulator();
        	if(!setupKeymasterOnSimulator()) {
        		System.out.println("Failed to setup Java card keymaster simulator.");
        		System.exit(-1);
        	}
        	
			System.out.println("Now try get hardware info");
			byte[] response = executeApdu(Utils.hexStringToByteArray("801E4000007F"));
			if(Arrays.equals(response, STATUS_OK)) {
				byte[] outData = hostApp.decodeDataOut();
	    		System.out.println("Return Data " + Utils.byteArrayToHexString(outData));
			
	    		System.out.println("Server is listening on port " + port + " and " + new String(outData) + " is ready");
			
	            while (true) {
	            	try {
		                Socket socket = serverSocket.accept();
		                System.out.println("\n\n\n\n\n");
		                System.out.println("------------------------New client connected on " + socket.getPort() + "--------------------");
		                OutputStream output = null;
		                InputStream isReader = null;
		                try {
			                output = socket.getOutputStream();
			                isReader = socket.getInputStream();
			 
		                	byte[] inBytes = new byte[65536];
		                	int readLen = 0;
		                	while((readLen = isReader.read(inBytes)) > 0) {
		                	if(readLen > 0) {
		                		byte[] outBytes = executeApdu(Arrays.copyOfRange(inBytes, 0, readLen));
				                outData = hostApp.decodeDataOut();
		        	    		System.out.println("Return Data " + Utils.byteArrayToHexString(outData));
				                byte[] finalOutData = new byte[outData.length + outBytes.length];
				                System.arraycopy(outData, 0, finalOutData, 0, outData.length);
				                System.arraycopy(outBytes, 0, finalOutData, outData.length, outBytes.length);
				                output.write(finalOutData);
		                		output.flush();
		                	}
		                	}
		                } catch (IOException e) {
		                	e.printStackTrace();
		                } finally {
		                	if(output != null) output.close();
		                	if(isReader != null) isReader.close();
		                	socket.close();
		                }
	            	} catch (IOException e) {
	            		break;
	            	}
	            }
			}
        	disconnectSimulator();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (CadTransportException e1) {
			e1.printStackTrace();
		}
    }
    
    /*class myRunnable implements Runnable {
    	
    	public myRunnable() {
    		
    	}
    		
		@Override
		public void run() {
			OutputStream output = null;
            InputStream isReader = null;
            try {
                output = socket.getOutputStream();
                isReader = socket.getInputStream();
 
            	byte[] inBytes = new byte[261];
            	int readLen = 0;
            	while((readLen = isReader.read(inBytes)) > 0) {
            	if(readLen > 0) {
            		byte[] outBytes = executeApdu(Arrays.copyOfRange(inBytes, 0, readLen));
	                outData = hostApp.decodeDataOut();
	                byte[] finalOutData = new byte[outData.length + outBytes.length];
	                System.arraycopy(outData, 0, finalOutData, 0, outData.length);
	                System.arraycopy(outBytes, 0, finalOutData, outData.length, outBytes.length);
	                output.write(finalOutData);
            		output.flush();
            	}
            	}
            } catch (IOException e) {
            	e.printStackTrace();
            } finally {
            	if(output != null) output.close();
            	if(isReader != null) isReader.close();
            	socket.close();
            }
			
		}
	};*/

    private static void initaliseSimulator() throws IOException, CadTransportException {
    	hostApp.establishConnectionToSimulator();
    	hostApp.powerUp();
	}
    
    private static void disconnectSimulator() throws IOException, CadTransportException {
    	hostApp.closeConnection();
    	hostApp.powerDown();
	}

	private static boolean setupKeymasterOnSimulator() {
    	try {
	    	ArrayList<byte[]> scriptApdus = ScriptParser.getApdusFromScript("res/JavaCardKeymaster.scr");
	    	//if(true) return false;
	    	for (byte[] apdu : scriptApdus) {
	    		byte[] response = null;
	        	if((response = executeApdu(apdu)) != null) {
	            	if(!Arrays.equals(response, STATUS_OK)) {
	    	    		System.out.println("Error response from simulator " + Utils.byteArrayToHexString(response));
	            		return false;
	            	}
	        	} else {
	        		return false;
	        	}
	    	}
	    	return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	} catch (CadTransportException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
	
	private static byte[] executeApdu(byte []apdu) throws IOException, CadTransportException {
		System.out.println("Exeuting apdu " + Utils.byteArrayToHexString(apdu));
    	if(hostApp.decodeApduBytes(apdu)) {
    		hostApp.exchangeTheAPDUWithSimulator();
        	byte[] response = hostApp.decodeStatus();
        	System.out.println("Decode status length:"+response.length);
        	//for(int i = 0; i < response.length; i++) {
        		System.out.print(Utils.byteArrayToHexString(response));
        	//}
        	System.out.println();
        	return response;
    	} else {
    		System.out.println("Failed to decode APDU [" + Utils.byteArrayToHexString(apdu) + "]");
    		return null;
    	}
	}
}
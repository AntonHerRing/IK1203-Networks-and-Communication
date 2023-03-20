package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient
{
	boolean shutdown = false;
	Integer timeout = null;
	Integer limit = null;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit)
    {
		this.shutdown = shutdown;
		this.timeout = timeout;
		this.limit = limit;
    }

    //shutdown: true, shutdown outgoing connection after sending data. false, dont shutdown.
    //timeout: max time in ms to wait for returning data. null = no timeout.
    //limit: max amount of data in bytes before returning. null = no limit.
    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException
    {
		//create stream socket, connect to port and host
		Socket sock = new Socket(hostname, port);

		//small fixed buffer. use in read operation.
		byte[] buffer = new byte[1];

		//dynamic big buffer. store data received
		ByteArrayOutputStream DynamicBuffer = new ByteArrayOutputStream();

		//start communication with server
		try{
			//write bytes to socket
			OutputStream out = sock.getOutputStream();
    		out.write(toServerBytes);

    		//test if shutdown true, and close
			if(this.shutdown)
				sock.shutdownOutput();
		}
		catch(Exception e){
			e.getStackTrace();
			System.out.println("Failed OutputStream");
		}

		//start transfering information from server
		try{
			//prepare to read bytes from socket, and set timeout.
			InputStream in = sock.getInputStream();
			sock.setSoTimeout((timeout == null)? 0 : timeout);

			//read and convert stream to bytes
			//until -1 if limit null, until limit if limit not null
			while(limit == null || DynamicBuffer.size() < limit)
			{
    			buffer[0] = (byte)in.read();
    			if(buffer[0] == -1)
    				break;
    			DynamicBuffer.write(buffer, 0, 1);
			}
		}
		catch(Exception e){
			e.getStackTrace();
			System.out.println("Failed inputStream");
		}

		//convert buffer to byte array
		byte[] returning = DynamicBuffer.toByteArray();
		sock.close();

        return returning;
	}

}

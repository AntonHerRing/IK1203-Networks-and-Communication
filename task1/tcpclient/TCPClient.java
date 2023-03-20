package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient
{
    public TCPClient(){
    }

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
		}
		catch(Exception e){
			e.getStackTrace();
			System.out.println("Failed OutputStream");
		}

		//start transfering information from server
		try{
			//read bytes from socket
			InputStream in = sock.getInputStream();

			//read and convert stream to bytes
			while(true)
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

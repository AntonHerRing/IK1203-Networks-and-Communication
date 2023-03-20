import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

//http://localhost:8888/ask?hostname=time.nist.gov&limit=1200&port=13
//GET /ask?hostname=time.nist.gov&limit=1200&port=13 HTTP/1.1
//Host: localhost:8888

public class ConcHTTPAsk
{
    public static void main( String[] args) throws IOException
    {
		//get port number from args
		int argindex = 0;
		int serverport = Integer.parseInt(args[argindex++]);

		//start server
		try{
			System.out.println("looking for connection on port: " + serverport);
			ServerSocket server = new ServerSocket( serverport );
			while(true)
			{
				Socket client = server.accept();

				//call runnable thread
				System.out.println("Starting new thread");
				new Thread(new MyRunnable(client)).start();

			}
		}
		catch(IOException e)
		{
			e.getStackTrace();
			System.out.println("ConcHTTPAsk: " + e);
		}
    }
}


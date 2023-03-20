import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

//http://localhost:8888/ask?hostname=time.nist.gov&limit=1200&port=13
//GET /ask?hostname=time.nist.gov&limit=1200&port=13 HTTP/1.1
//Host: localhost:8888

public class HTTPAsk
{
    public static void main( String[] args) throws IOException
    {
		//obligatory variable dekleration
        String hostname = null;
        int port = -1;

		//optional variable dekleration
        String string = "";
        boolean shutdown = false;
		Integer timeout = null;
		Integer limit = null;
		String ErrorData = null;

		//get port number from args
		int argindex = 0;
		int serverport = Integer.parseInt(args[argindex++]);

		//start server
		try{
			System.out.println("looking for connection on port: " + serverport);
			ServerSocket server = new ServerSocket( serverport );
			while(true)
			{
        		//try connecting to socket
        		try(Socket socket = server.accept())
        		{
					//declare I/Ostreams
        			InputStream in = socket.getInputStream();
        			OutputStream out = socket.getOutputStream();

        			//read incomming data
        			byte[] buffer = new byte[1024];
        			in.read(buffer);
					String input = new String(buffer);

					//validate ask request
					if(!input.contains("/ask") || !input.contains("GET") || !input.contains("HTTP/1.1"))
					{
						ErrorData = "HTTP/1.1 404 Not Found\r\n\r\n";
						out.write(ErrorData.getBytes());
						throw new IOException("404 Not Found");
					}
					input = input.split("HTTP/1.1")[0];

					//filter obligatory TCP components
					if(input.contains("hostname="))
						hostname = (input.split("hostname=")[1].split("&"))[0];
					if(input.contains("port="))
					{
						String temp = input.split("port=")[1];
						if(temp.contains("&"))
							port = Integer.parseInt(temp.split("&")[0]);
						else
							port = Integer.parseInt((input.split("port=")[1].split(" "))[0]);
					}
					if(hostname == null || port == -1)
					{
						ErrorData = "HTTP/1.1 400 Bad Request\r\n\r\n";
						out.write(ErrorData.getBytes());
						throw new IOException("400 Bad Request");
					}
					System.out.println(hostname);

					//try filtering optional TCP components
					if(input.contains("string="))
					{
						String temp = input.split("string=")[1];
						if(temp.contains("&"))
							string = temp.split("&")[0] + "\n";
						else
							string = temp.split(" ")[0] + "\n";
					}
					if(input.contains("limit="))
					{
						String temp = input.split("limit=")[1];
						if(temp.contains("&"))
							limit = Integer.parseInt(temp.split("&")[0]);
						else
							limit = Integer.parseInt(temp.split(" ")[0]);
					}
					if(input.contains("timeout="))
					{
						String temp = input.split("timeout=")[1];
						if(temp.contains("&"))
							timeout = Integer.parseInt(temp.split("&")[0]);
						else
							timeout = Integer.parseInt(temp.split(" ")[0]);
					}
					if(input.contains("shutdown="))
					{
						String temp = input.split("shutdown=")[1];
						if(temp.contains("&"))
							shutdown = Boolean.parseBoolean(temp.split("&")[0]);
						else
							shutdown = Boolean.parseBoolean(temp.split(" ")[0]);
					}

					//try sending to tcpclient and decode the results
					try{
						TCPClient tcp = new TCPClient(shutdown, timeout, limit);
        				byte[] encodedData = tcp.askServer(hostname, port, string.getBytes());
        				String decodedData = new String(encodedData);

        				//HTTPAsk output
        				String str = "HTTP/1.1 200 OK\r\n\r\n" + decodedData;
        				out.write(str.getBytes());
					}
					catch(Exception e)
					{
						//return 400 bad request error to web page
						ErrorData = "HTTP/1.1 400 Bad Request\r\n\r\n";
        				out.write(ErrorData.getBytes());
					}
				}
			}
		}
		catch(IOException e)
		{
			e.getStackTrace();
			System.out.println(e);
		}
    }
}


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler implements Runnable{
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;

	public boolean gridSet = false;

	public Thread thread;
	public Server server;
	public String player;
	public ClientHandler[] clients;

	public ClientHandler(String player, Server server, Socket socket) throws IOException{
		this.socket = socket;
		this.server = server;
		this.player = player;

		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		thread = new Thread(this);
	}

	@Override
	public void run() {
		try {
			while (true) {
				String msgFromClient = br.readLine();

				if (msgFromClient == null) { //Server has disconnected
					break;
				}

				if (msgFromClient.equals("TURN DONE")){
					int posX = Integer.parseInt(br.readLine());
					int posY = Integer.parseInt(br.readLine());

					try { //update server grid
						server.gridLock.lock();
						server.grid[posX][posY] = player;
					} finally {
						server.gridLock.unlock();
					}

					sendMessageToOther("UPDATE GRID");
					sendMessage("UPDATE GRID");
					for (int x = 0; x < server.grid.length; x++){ //send entire grid over to user
						for (int y = 0; y < server.grid[x].length; y++){
							sendMessage(server.grid[x][y]);
							sendMessageToOther(server.grid[x][y]);
						}
					}
					sendMessageToOther("TURN"); //Send message to other player for their turn
				}

			}
			System.out.println("CLOSING SOCKET");
			socket.close();
			br.close();
			bw.close();

		} catch (IOException e) {
			System.out.println("PROBLEM OCCURED");
			e.printStackTrace();
		}
	}

	public void sendMessage(String message){
		try {
			bw.write(message);
	                bw.newLine();
        	        bw.flush();

		} catch (IOException e){
			System.out.println("IOEXCEPTION SENDING MESSAGE");
			e.printStackTrace();
		}
	}

	public void sendMessageToOther(String message){
		for (int x = 0; x < clients.length; x++){
			if (clients[x] != this){
				clients[x].sendMessage(message);

			}
		}
	}

	public boolean checkWin(){ //check if five in a row in any direction (up, side, diagonal);

		String[][] g = server.grid; //grid

		for (int x = 0; x < g.length; x++){
			for (int y = 0; y < g.length; y++){

				try { //check column
					if (g[x][y] == player && 
						g[x][y+1] == player && 
						g[x][y+2] == player && 
						g[x][y+3] == player &&
						g[x][y+4] == player){ 

						return true;
					}

				} catch (NullPointerException e) {
					System.out.println("here");
				}

				try { //check row
					if (g[x][y] == player && 
                                                g[x+1][y] == player &&
                                                g[x+2][y] == player &&
                                                g[x+3][y] == player &&
                                                g[x+4][y] == player){

                                                return true;
                                        }

				} catch (NullPointerException e) {
					System.out.println("here.");
				}

				try { //check diagonal
					if (g[x][y] == player &&
                                                g[x+1][y+1] == player &&
                                                g[x+2][y+2] == player &&
                                                g[x+3][y+3] == player &&
                                                g[x+4][y+4] == player){

                                                return true;
                                        }

				} catch (NullPointerException e) {
					System.out.println("here..");
				}
			}
		}
		return false;
	}
}

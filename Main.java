import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

public class Main implements ActionListener {	
	//Visual
	public JButton[][] grid = new JButton[19][19];
	public JFrame frame;

	//Networking
	public Client client;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		System.out.println("Play Gomoku?");
		char input = in.nextLine().charAt(0);

		if (input == 'y' || input == 'Y'){
			System.out.println("Have fun playing!");
			new Main().setUpGame();	
		} else {
			System.out.println("Come back soon!");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int x = 0; x < grid.length; x++){
                        for (int y = 0; y < grid[x].length; y++){
                                if (e.getSource() == grid[x][y] && 
					!grid[x][y].getText().equals("x") &&
					!grid[x][y].getText().equals("o")){	

					removeButtonListeners();
					
					client.sendMessage("TURN DONE");
					client.sendMessage(String.valueOf(x));
					client.sendMessage(String.valueOf(y));
				}
                        }
                }
	}

	public void setUpGame() {
		client = new Client(this);
		createWindow();
                receiveMessages();
	}

	public void receiveMessages() {
		while (true){
			try {
				String message = client.br.readLine();

				if (message.equals("UPDATE GRID")){
                                        frame.setTitle("Updating Information...");
                                        for (int x = 0; x < grid.length; x++){ //update grid
                                                for (int y = 0; y < grid[x].length; y++){
                                                        grid[x][y].setText(client.br.readLine());
                                                }
                                        }
					frame.setTitle("Waiting for other player");
				}

				if (message.equals("TURN")){	
					frame.setTitle("Your Turn!");
					addButtonListeners();					
				}

				if (message.equals("RESULTS")){
					String result = client.br.readLine();
					System.out.println("GAME OVER!!!");
					System.out.println("Player " + result + " wins!");
					frame.setTitle("Game Over! Player " + result + " wins!");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					break;
				}

			} catch (IOException e){
				System.out.println("ERROR READING MESSAGES");
				e.printStackTrace();
			}
		}
	}

	public void createWindow() {
		frame = new JFrame("Gomoku");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(19, 19));
		
		for (int x = 0; x < grid.length; x++){
                       for (int y = 0; y < grid[x].length; y++){
			       grid[x][y] = new JButton("");
                               panel.add(grid[x][y]);
                       }
                }

		frame.add(panel);
                frame.setSize(855, 855);
		frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setResizable(true);
		frame.setVisible(true);
	}

	public void removeButtonListeners(){ //When not users turn, remove listeners so buttons can't be clicked
		for (int x = 0; x < grid.length; x++){
			for (int y = 0; y < grid[x].length; y++){
				grid[x][y].removeActionListener(this);
			}
		}
	}

	public void addButtonListeners(){ //add button listeners
		for (int x = 0; x < grid.length; x++){
                        for (int y = 0; y < grid[x].length; y++){
                                grid[x][y].addActionListener(this);
                        }
                }

	}
}

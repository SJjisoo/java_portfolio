package ui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//메인 메뉴
public class Main extends JFrame {
	private JButton btn [] = new JButton[4];
	private String btnTx [] = {"사용자", "관리자", "사원등록", "종료"};
	
	public Main() {
		setTitle("메인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		setLayout(new GridLayout(4, 1));
		
		for(int i=0; i<btn.length; i++) {
			btn[i] = new JButton(btnTx[i]);
			c.add(btn[i]);
			btn[i].addActionListener(new mainAction());
		}
		
		setSize(200, 200);
		setVisible(true);
	}
	
	class mainAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String choice = e.getActionCommand();
			managerPw pad = new managerPw();
			switch(choice) {
				case "사용자" :
					dispose();
					new menuForCustomer();
					break;
				case "관리자" :
					int result = JOptionPane.showConfirmDialog(null, pad, "관리자 패스워드 입력", JOptionPane.OK_CANCEL_OPTION);
					if(result == JOptionPane.CANCEL_OPTION) {						
						dispose();
						new Main();
					}
					else {
						String getPw = pad.getPf();
						if(getPw.equals("0000"))
							new managerMenu();
						else
							JOptionPane.showMessageDialog(null, "관리자 패스워드를 확인하십시오.", "Message", JOptionPane.ERROR_MESSAGE);
					}
					break;
				case "사원등록" :
					new addCustomer();
					break;
				case "종료" :
					dispose();
					break;
			}
		}
	}
	
	public static void main(String[] args) {
		new Main();
	}
}

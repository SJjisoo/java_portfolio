package ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import db.Driver_connect;

//메인에서 사원 등록 버튼을 누를 때 나타나는 프레임
public class addCustomer extends JFrame {
	private JLabel lb [] = new JLabel[4];
	private String lbTx [] = {"사원번호:", "*사 원 명:", "*패스워드:", "*패스워드 재입력:"};
	private JTextField tf [] = new JTextField[2];
	private JPasswordField pf [] = new JPasswordField[2];
	private JButton btn [] = new JButton[2];
	private String btnTx [] = {"등록", "닫기"};
	private JPanel pwPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JLabel check = new JLabel("");
	
	public addCustomer() {
		setTitle("사원등록");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new GridLayout(5, 2));
		
		for(int i=0; i<lb.length; i++) {
			if(i<2) {
				lb[i] = new JLabel(lbTx[i]);
				add(lb[i]);
				tf[i] = new JTextField(5);
				add(tf[i]);
			}
			else if(i==2) {
				lb[i] = new JLabel(lbTx[i]);
				add(lb[i]);
				pf[i-2] = new JPasswordField(5);
				add(pf[i-2]);
				pf[i-2].addKeyListener(new pfAction());
			}
			else {
				lb[i] = new JLabel(lbTx[i]);
				pwPanel.add(lb[3]);
				pwPanel.add(check);
				add(pwPanel);
				lb[i].setHorizontalAlignment(JLabel.LEFT);
				pf[i-2] = new JPasswordField(5);
				add(pf[i-2]);
				pf[i-2].addKeyListener(new pfAction());
			}
			
		}
		
		for(int i=0; i<btn.length; i++) {
			btn[i] = new JButton(btnTx[i]);
			add(btn[i]);
			btn[i].addActionListener(new myAction());
		}
		
		makeMemberNum();
		tf[0].setEnabled(false);
		
		setSize(310, 200);
		setVisible(true);
	}
	
	public void makeMemberNum() {
		String sql = "select max(memberNo) from member;";
		int number=0;
		
		try {
			Connection con = Driver_connect.makeConnection("meal");
			PreparedStatement psmt = con.prepareStatement(sql);
			
			ResultSet rs = psmt.executeQuery();
			
			while(rs.next())
				number = Integer.parseInt(rs.getString(1));
			
			tf[0].setText(Integer.toString(number+1));
		} catch (Exception e) {
			System.out.println(e);
		}
			
	}
	
	class pfAction extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			JPasswordField actionField = (JPasswordField)e.getSource();
			char typing = e.getKeyChar();
			String now = new String(actionField.getPassword());
			
			if(now.length()>4 || !Character.isDigit(typing)) {
				actionField.setText("");
			}
			
			if(actionField == pf[1]) {
				String firstPw = new String(pf[0].getPassword());
				String secondPw = new String(pf[1].getPassword());
				if(secondPw.equals(firstPw)) {
					System.out.println("같다");
					lb[3].setText(lbTx[3]);
					check.setForeground(Color.BLUE);
					check.setText("일치");
				}
				else {
					System.out.println("다르다");
					check.setForeground(Color.RED);
					check.setText("불일치");	
				}
			}
		}
	}
	
	class myAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String sql = "insert into member values(?, ?, ?)";
			JButton choice = (JButton)e.getSource();
			if(choice == btn[1])
				dispose();
			else {
				String password1 = new String(pf[0].getPassword());
				String password2 = new String(pf[1].getPassword());
				if(tf[1].getText()=="" || password1=="" || password2=="")
					JOptionPane.showMessageDialog(null, "필수 항목(*) 누락", "Message", JOptionPane.ERROR_MESSAGE);
				else if(!password1.equals(password2))
					JOptionPane.showMessageDialog(null, "패스워드 확인 요망", "Message", JOptionPane.ERROR_MESSAGE);
				else {
					try {
						Connection con = Driver_connect.makeConnection("meal");
						PreparedStatement psmt = con.prepareStatement(sql);
						
						psmt.setInt(1, Integer.parseInt(tf[0].getText()));
						psmt.setString(2, tf[1].getText());
						psmt.setString(3, password1);
						
						psmt.executeUpdate();
						
						dispose();
					} catch (Exception ee) {
						System.out.println(ee);
					}
					
					JOptionPane.showMessageDialog(null, "사원이 등록되었습니다", "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
}
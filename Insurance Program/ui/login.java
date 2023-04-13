package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

//제일 첫 창인 로그인 창
public class login extends JFrame {
	private JLabel label [] = new JLabel[2];
	private String labelTx [] = {"이름", "비밀번호"};
	private JTextField tf = new JTextField(10);
	private JPasswordField pf = new JPasswordField(10);
	private JButton btn [] = new JButton[2];
	private String btnTx [] = {"확인", "종료"};
	
	public login() {
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		setLayout(new BorderLayout(10, 10));
		
		JLabel title = new JLabel("관리자 로그인");
		title.setFont(new Font("Dotum", Font.BOLD, 15));
		title.setHorizontalAlignment(JLabel.CENTER);
		//라벨 가운데정렬 하는법
		
		add(title, BorderLayout.NORTH);
		add(new loginField(), BorderLayout.CENTER);
		add(new buttonField(), BorderLayout.SOUTH);
		
		setSize(300, 160);
		setVisible(true);
	}
	
	class loginField extends JPanel {
		public loginField() {
			setLayout(new GridLayout(2, 2));
			for(int i=0; i<label.length; i++) {
				label[i] = new JLabel(labelTx[i]);
			}
			add(label[0]);
			add(tf);
			add(label[1]);
			add(pf);
		}
	}
	
	class buttonField extends JPanel {
		public buttonField() {
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(btnTx[i]);
				add(btn[i]);
			}
			btn[0].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Connection con = Driver_connect.makeConnection("customer");
					PreparedStatement psmt = null;
					
					try {
						psmt = con.prepareStatement("select name from admin where name=? and passwd=?");
						
						String name = tf.getText();
						String password = new String(pf.getPassword());
						
						psmt.setString(1, name);
						psmt.setString(2, password);
						
						ResultSet rs = psmt.executeQuery();
						//select할때는 쿼리 나머지는 다 업데이트
					
						if(rs.next()) {
							dispose();
							new insurance();
						} else {
							JOptionPane.showMessageDialog(null, "잘못된 정보입니다.", "로그인실패", JOptionPane.ERROR_MESSAGE);
							tf.setText("");
							pf.setText("");
							//dispose하고 다시 new login(); 해주는 식으로 메모리 소비하면서 복잡하게 하지말고
							//그냥 단순하게 셋텍스트로 비워주기만 하면 이미 실행된 그 상태에서 계속 아이디/비번 입력할 수 있게 됨
						}
					} catch (Exception ee) {}	
				}
			});
			
			btn[1].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(null, "종료합니다", "종료", JOptionPane.CLOSED_OPTION);
					dispose();
				}
			});
		}
	}
	
	public static void main(String[] args) {
		new login();
	}
}

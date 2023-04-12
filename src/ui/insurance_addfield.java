package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import db.Driver_connect;

//메인 메뉴에서 고객등록을 눌렀을때
public class insurance_addfield extends JFrame {
	private JLabel addLabel [] = new JLabel[6];
	private String addLabelTx [] = {"고객 코드 : ", "*고객명 : ", "*생년월일(YYYY-MM-DD)", "*연락처 : ", "주소 : ", "회사 : "};
	private JTextField tf [] = new JTextField[6];
	private JButton addBtn [] = new JButton[2];
	private String addBtnTx [] = {"추가", "닫기"};
	private String code = null;
	
	public insurance_addfield() {
		setTitle("고객 등록");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		setLayout(new BorderLayout());
		
		add(new addTxField(), BorderLayout.NORTH);
		add(new addButtonField(), BorderLayout.SOUTH);
		
		setSize(300,200);
		setVisible(true);
	}
	

	class addTxField extends JPanel {
		public addTxField() {
			setLayout(new GridLayout(6, 2));
			
			for(int i=0; i<addLabel.length; i++) {
				addLabel[i] = new JLabel(addLabelTx[i]);
				add(addLabel[i]);
				tf[i] = new JTextField(10);
				add(tf[i]);
			}
			tf[0].setEnabled(false);
			tf[2].addActionListener(new codeListener());
		}
	}
	
	class addButtonField extends JPanel {
		public addButtonField() {
			for(int i=0; i<addBtn.length; i++) {
				addBtn[i] = new JButton(addBtnTx[i]);
				add(addBtn[i]);
			}
			addBtn[0].addActionListener(new addAction());
			addBtn[1].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
	}
	
	class codeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String birth = tf[2].getText(); 
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR) - 2000;
			String str[] = birth.split("-");
			int hap = Integer.valueOf(str[0]) + Integer.valueOf(str[1]) + Integer.valueOf(str[2]);
			code = new String("S" + year + hap);
			tf[0].setText(code);
		}
	}
	
	class addAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(tf[1].getText().equals("") || tf[2].getText().equals("") || tf[3].getText().equals("")) {
				JOptionPane.showMessageDialog(null, "필수 항목(*)을 모두 입력하세요", "고객 등록 에러", JOptionPane.ERROR_MESSAGE);
			}
			else {
				try {					
					Connection con = Driver_connect.makeConnection("customer");
					PreparedStatement psmt = null;
					String text = "insert into customer values (?, ?, ?, ?, ?, ?)";
					
					psmt = con.prepareStatement(text);
					
					for(int i=0; i<tf.length; i++) {
						if(i==0)
							psmt.setString(i+1, code);
						else
							psmt.setString(i+1, tf[i].getText());
					}
					psmt.executeUpdate();
					JOptionPane.showMessageDialog(null, "고객추가가 완료되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
					dispose();
				} catch (Exception ee) {
				}
			}
		}
	}		
}			
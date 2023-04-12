package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//로그인에 성공 했을 때 메인메뉴
public class insurance extends JFrame {
	private JButton mainBtn [] = new JButton[4];
	private String mainBtnTx[] = {"고객등록", "고객조회", "고객관리", "종료"};
	
	public insurance() {
		setTitle("보험계약 관리화면");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		c.add(new northPanel(), BorderLayout.NORTH);
		
		ImageIcon image = new ImageIcon("C:\\Users\\SJCOM\\Desktop\\customer\\customer\\imgs\\img.jpg");
		JLabel imgLabel = new JLabel(image);
		c.add(imgLabel, BorderLayout.SOUTH);
		
		setSize(600,450);
		setVisible(true);
	}
	
	class northPanel extends JPanel {
		public northPanel() {
			for(int i=0; i<mainBtn.length; i++) {
				mainBtn[i] = new JButton(mainBtnTx[i]);
				add(mainBtn[i]);
			}
			mainBtn[0].addActionListener(new MyActionListener());
			mainBtn[1].addActionListener(new MyActionListener2());
			mainBtn[2].addActionListener(new MyActionListener3());
			mainBtn[3].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
	}
	
	class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new insurance_addfield();
		}
	}
	
	class MyActionListener2 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new insurance_search();
		}
	}
	
	class MyActionListener3 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new insurance_control();
		}
	}
}

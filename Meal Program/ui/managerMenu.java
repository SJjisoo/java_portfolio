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

//메인에서 관리자모드로 들어가서 관리자 비밀번호를 입력했을때 뜨는 관리자 메뉴
public class managerMenu extends JFrame {
	private JButton [] menuBtn = new JButton[5];
	private String btnTx [] = {"메뉴 등록", "메뉴 관리", "결제 조회", "종류별 차트", "종 료"};
	
	public managerMenu() {
		setTitle("관리");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		add(new northPanel(), BorderLayout.NORTH);
		
		ImageIcon img = new ImageIcon("\\\\192.168.0.40\\서진컴_랩실2\\강지수\\meal\\meal\\main.jpg");
		JLabel imgLb = new JLabel(img);
		
		add(imgLb, BorderLayout.CENTER);
		
		setSize(500, 500);
		setVisible(true);
	}
	
	class northPanel extends JPanel {
		public northPanel() {
			for(int i=0; i<menuBtn.length; i++) {
				menuBtn[i] = new JButton(btnTx[i]);
				this.add(menuBtn[i]);
				menuBtn[i].addActionListener(new myAction());
			}
		}
		
		class myAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				String choice = e.getActionCommand();
				switch(choice) {
					case "메뉴 등록" :
						new addMenu();
						break;
					case "메뉴 관리" :
						new menuManagement();
						break;
					case "결제 조회" :
						new paymentList();
						break;
					case "종류별 차트" :
						new chartFrame();
						break;
					case "종 료" :
						dispose();
						break;
				}
			}
		}
	}
}

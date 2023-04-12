package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

//메인에서 사용자 버튼을 눌렀을때 음식 분류를 고를 수 있는 프레임
public class menuForCustomer extends JFrame {
	private JButton btn [] = new JButton[4];
	private ImageIcon img [] = new ImageIcon[4];
	private String foodName [] = {"한식", "중식", "일식", "양식"};
	private String watch;
	private timeThread tth;
	private JLabel watchLabel = new JLabel(watch);
	
	public menuForCustomer() {
		setTitle("식권 발매 프로그램");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		JLabel lb = new JLabel("식권 발매 시스템");
		add(lb, BorderLayout.NORTH);
		lb.setHorizontalAlignment(JLabel.CENTER);
		
		JTabbedPane tab = new JTabbedPane();
		tab.addTab("메뉴", new menu());
		add(tab, BorderLayout.CENTER);
		
		add(new southPanel(), BorderLayout.SOUTH);
		
		setSize(500, 600);
		setVisible(true);
		
		tth = new timeThread();
		tth.start();
	}

	
	class menu extends JPanel {
		public menu() {
			setLayout(new GridLayout(2,2));
			
			for(int i=0; i<btn.length; i++) {
				img[i] = new ImageIcon("\\\\192.168.0.40\\서진컴_랩실2\\강지수\\meal\\meal\\menu_" + (i+1) + ".png");
				btn[i] = new JButton(img[i]);
				btn[i].setToolTipText(foodName[i]);
				btn[i].addActionListener(new myActionListener());
				add(btn[i]);
			}
		}
	}
	
	class myActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				new foodMenu(b.getToolTipText());
				dispose();
		}
	}
	

	class southPanel extends JPanel {
		public southPanel() {
			this.setBackground(Color.BLACK);
			watchLabel.setForeground(Color.CYAN);
			add(watchLabel);
		}
	}
	
	class timeThread extends Thread {
		@Override
		public void run() {
			while(true) {				
				Date time = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("현재시간 : yyyy년 MM월 dd일 HH시 mm분 ss초");
				watch = sdf.format(time);
				watchLabel.setText(watch);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {}
			}
		}
	}
}

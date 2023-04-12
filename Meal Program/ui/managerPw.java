package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

//메인에서 관리자를 눌렀을때 관리자 패스워드를 입력하는 패널
public class managerPw extends JPanel {
	private JButton [] numBtn = new JButton[10];
	private JPasswordField pf;
	
	public managerPw() {
		this.setLayout(new BorderLayout());
		
		for(int i=0; i<numBtn.length; i++) {
			numBtn[i] = new JButton(Integer.toString(i));
			numBtn[i].addActionListener(new myAction());
		}
		
		pf = new JPasswordField(10);
		
		this.add(pf, BorderLayout.NORTH);
		this.add(new centerPanel(), BorderLayout.CENTER);
		this.add(numBtn[0], BorderLayout.SOUTH);
		//보더레이아웃 사우쓰로 해서 사우쓰패널을 만들어서 거기에 버튼을 넣고 붙였는데
		//그러면 0이 flowLayout적용되서 작게 붙여짐
		//그냥 사우쓰 자체에 버튼을 바로 붙여버리면 보더 사이즈에 맞게 버튼이 붙음
	}
	
	public String getPf() {
		return new String(pf.getPassword());
	}
	
	class centerPanel extends JPanel {
		public centerPanel() {
			this.setLayout(new GridLayout(3, 3));
			
			for(int i=1;  i<numBtn.length; i++) {
				this.add(numBtn[i]);
			}
		}
	}
	
	class myAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String goField = e.getActionCommand();
			String before = new String(pf.getPassword());
			
			if(before.length()<4) {
				//비밀번호 배열을 투스트링해서 거기에 입력된 값을 붙이는 방법
				String after = before.toString().concat(goField);
				pf.setText(after);
			}
		}
	}
}

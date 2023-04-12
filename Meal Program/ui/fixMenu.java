package ui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import db.Driver_connect;

//관리자 메뉴에서 메뉴 관리 버튼을 누르고 들어가서 메뉴를 선택하고 수정 버튼을 눌렀을때 나타나는 프레임
public class fixMenu extends JFrame {
	private JLabel lb [] = new JLabel[4];
	private String lbTx [] = {"종류", "*메뉴명", "가격", "조리가능수량"};
	private String sub [] = {"한식", "중식", "일식", "양식"};
	private Vector<Integer> price, cnt;
	private JComboBox<String> subB;
	private JComboBox<Integer> priceB, cntB;
	private JTextField mealNameF = new JTextField();
	private JButton btn [] = new JButton[2];
	private String btnTx [] = {"수정", "닫기"};
	private Vector<String> getSend = new Vector<String>();

	public fixMenu(Vector<String> send) {
		//액션리스너에서 쓰기 위해 send를 이 클래스에서 객체화
		getSend = send;
		
		setTitle("메뉴 수정");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new GridLayout(5, 2));
		
		for(int i=0; i<lb.length; i++)
			lb[i] = new JLabel(lbTx[i]);
		
		subB = new JComboBox<String>(sub);
		
		price = new Vector<Integer>();
		int payAdd = 1000;
		for(int i=0; i<=22; i++) {
			price.add(payAdd+(i*500));
		}
		priceB = new JComboBox<Integer>(price);
		
		cnt = new Vector<Integer>();
		for(int i=0; i<=50; i++) {
			cnt.add(i);
		}
		cntB = new JComboBox<Integer>(cnt);
		
		for(int i=0; i<lb.length; i++) {
			this.add(lb[i]);
			if(i==0) {
				this.add(subB);
			}
			else if(i==1) {
				this.add(mealNameF);
			}
			else if(i==2) {
				this.add(priceB);
			}
			else if(i==3) {
				this.add(cntB);
			}
		}
		
		for(int i=0; i<btn.length; i++) {
			btn[i] = new JButton(btnTx[i]);
			this.add(btn[i]);
		}
		
		//테이블에서 받아온 값들 디폴트로 적용해두기
		int priceCheck = 0;
		int cntCheck = 0;
		subB.setSelectedIndex(Integer.parseInt(send.get(0)));
		mealNameF.setText(getSend.get(1));
		for(int i=0; i<price.size(); i++) {
			if(send.get(2).equals(Integer.toString(price.get(i)))) {
				priceCheck = i;
			}
		}
		priceB.setSelectedIndex(priceCheck);
		for(int i=0; i<cnt.size(); i++) {
			if(send.get(3).equals(Integer.toString(cnt.get(i)))) {
				cntCheck = i;
			}
		}
		cntB.setSelectedIndex(cntCheck);
		
		btn[0].addActionListener(new myAction());
		btn[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setSize(200, 200);
		setVisible(true);
	}
	
	class myAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String sql = "update meal set cuisineNo=?, mealname=?, price=?, maxCount=? where mealname=?";
			int cuisineIn = subB.getSelectedIndex()+1;
			String mealNameIn = mealNameF.getText();
			int priceIn = price.get(priceB.getSelectedIndex());
			int maxCountIn = cnt.get(cntB.getSelectedIndex());
			
			try {
				Connection con = Driver_connect.makeConnection("meal");
				PreparedStatement psmt = con.prepareStatement(sql);
				
				psmt.setInt(1, cuisineIn);
				psmt.setString(2, mealNameIn);
				psmt.setInt(3, priceIn);
				psmt.setInt(4, maxCountIn);
				psmt.setString(5, getSend.get(1));
				
				if(mealNameIn.equals("")) {
					JOptionPane.showMessageDialog(null, "수정할 메뉴명을 입력해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(null, "메뉴가 정상적으로 수정되었습니다.", "Message", JOptionPane.INFORMATION_MESSAGE);
					psmt.executeUpdate();
					dispose();
					
				}
			} catch (Exception ee) {
				System.out.println(ee);
			}
		}
	}
}
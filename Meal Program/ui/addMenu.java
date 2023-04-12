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

//관리자 메뉴에서 메뉴등록 버튼을 눌렀을때 열리는 프레임
public class addMenu extends JFrame {
	private JLabel lb [] = new JLabel[4];
	private String lbTx [] = {"종류", "*메뉴명", "가격", "조리가능수량"};
	private String sub [] = {"한식", "중식", "일식", "양식"};
	private Vector<Integer> price, cnt;
	private JComboBox<String> subB;
	private JComboBox<Integer> priceB, cntB;
	private JTextField mealNameF = new JTextField();
	private JButton btn [] = new JButton[2];
	private String btnTx [] = {"등록", "닫기"};
	
	public addMenu() {
		setTitle("신규 메뉴 등록");
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
			String sql = "insert into meal (cuisineNo, mealName, price, maxCount, todayMeal) values (?,?,?,?,1)";
			int cuisineIn = subB.getSelectedIndex()+1;
			String mealNameIn = mealNameF.getText();
			int priceIn = price.get(priceB.getSelectedIndex());
			int maxCountIn = cnt.get(cntB.getSelectedIndex());
			
			try {
				Connection con = Driver_connect.makeConnection("meal");
				// 그냥 스테이트먼트는 이미 완성된 문장을 넣어주고 실행하는거라고 보면되고
				// 프리페어스테이트먼트는 이런식으로 sql문에 ?가 있어서 대입을 해줘야할때 쓰면 된다고 기억
				PreparedStatement psmt = con.prepareStatement(sql);
				
				psmt.setInt(1, cuisineIn);
				psmt.setString(2, mealNameIn);
				psmt.setInt(3, priceIn);
				psmt.setInt(4, maxCountIn);
				
				if(mealNameIn.equals("")) {
					JOptionPane.showMessageDialog(null, "메뉴명을 입력해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(null, "메뉴가 정상적으로 등록되었습니다.", "Message", JOptionPane.INFORMATION_MESSAGE);
					psmt.executeUpdate();
					dispose();
				}
			} catch (Exception ee) {
				System.out.println(ee);
			}
		}
	}
}
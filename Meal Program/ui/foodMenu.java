package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import db.Driver_connect;

//메인에서 사용자 버튼을 누르고 음식 분류를 골랐을때 메뉴를 선택하고 결제 할 수 있는 프레임
public class foodMenu extends JFrame {
	private String choiceMenu;
	private int sub;
	//총결제금액 라벨 요소
	private JLabel payLb = new JLabel();
	//테이블 만드는 요소들
	private Vector<String> col;
	private Vector<Vector<String>> data;
	private JTable jt;
	private DefaultTableModel model = new DefaultTableModel();
	private String colTx [] = {"상품번호", "품명", "수량", "금액"};
	//테이블 밑에 텍스트필드 만드는 요소들
	private JLabel selectLb [] = new JLabel[2];
	private String selectLbTx [] = {"선택품명:", "수량:"};
	private JTextField selectTf [] = new JTextField[2];
	
	//금액표시 3자리마다 나뉘어지게
	private DecimalFormat df = new DecimalFormat("#,###");
	//총 금액 라벨에 올릴 총 금액 수치
	private int payInt = 0;
	
	private JButton numBtn [] = new JButton[10];
	private JButton confirm, reset, paybtn, cancel;
	
	private foodBtnMake [] makeBtn;
	
	//결제버튼 눌렀을때 결제자 인증창 구성요소들
	private JLabel payCustomerLb [] = new JLabel[2];
	private String payCustomerTx [] = {"사원번호", "패스워드"};
	private JComboBox<String> customerCb;
	private Vector<String> comboMember = new Vector<String>();
	private JPasswordField passField = new JPasswordField(5);
	private String memberName;
	private int cbCount = 0;
	
	public foodMenu(String a) {
		setTitle("결제");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		choiceMenu = a;
		
		switch(a) {
			case "한식" :
				sub = 1;
				break;
			case "중식" :
				sub = 2;
				break;
			case "일식" :
				sub = 3;
				break;	
			case "양식" :
				sub = 4;
				break;
		}
		
		add(new northPanel(), BorderLayout.NORTH);
		add(new centerPanel(), BorderLayout.CENTER);
		
		setSize(800, 800);
		setVisible(true);
	}
	
	class northPanel extends JPanel {
		public northPanel() {
			JLabel foodNameLb;
			foodNameLb = new JLabel(choiceMenu);
			foodNameLb.setFont(new Font("Dottum", Font.BOLD, 20));
			add(foodNameLb);	
		}
	}
	
	class centerPanel extends JPanel {
		public centerPanel() {
			this.setLayout(new GridLayout(1, 2, 10, 10));
			add(new centerLeftPanel());
			add(new centerRightPanel());
		}
	}
	
	class centerLeftPanel extends JPanel {
		public centerLeftPanel() {
			String sel = "select mealName, price, maxCount, todayMeal from meal where cuisineNo=" + sub;
			Vector<String> mealNameV = new Vector<String>();
			Vector<String> priceV = new Vector<String>();
			Vector<String> maxCountV = new Vector<String>();
			Vector<String> todayMealV = new Vector<String>();
			
			Connection con = Driver_connect.makeConnection("meal");
			
			try {
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sel);
				
				while(rs.next()) {
					mealNameV.add(rs.getString(1));
					priceV.add(rs.getString(2));
					maxCountV.add(rs.getString(3));
					todayMealV.add(rs.getString(4));
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			
			makeBtn = new foodBtnMake[mealNameV.size()];
			
			for(int i=0; i<makeBtn.length; i++) {
				makeBtn[i] = new foodBtnMake(mealNameV.get(i), priceV.get(i), maxCountV.get(i), todayMealV.get(i));
			}
			
			int x;
			
			if(makeBtn.length%5 > 0) {
				x = makeBtn.length/5 + 1;
			}
			else
				x = makeBtn.length/5;
			
			this.setLayout(new GridLayout(x, 5));
			for(int i=0; i<makeBtn.length; i++) {
				this.add(makeBtn[i]);
			}
		}
	}
	
	//센터레프트페널에 구성되는 버튼을 만드는 항목
	class foodBtnMake extends JButton {
		//버튼 만들면서 이름을 담아두기 위해서 private형으로 만들어둠
		private String mealName;
		public foodBtnMake(String mealName, String price, String count, String today) {
			this.mealName = mealName;
			this.setText("<HTML><center>" + mealName + "<br>" + price + "원</center></HTML>");
			if(count.equals("0") || today.equals("0")) 
				this.setEnabled(false);
			
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectTf[0].setText(mealName);
				}
			});
		}
		//테이블에 메뉴 입력될때 버튼 찾아서 비활성화 시켜야해서 해당되는 버튼을 찾기 위해서 메뉴 이름을 뽑아내는 메소드
		public String getMealName() {
			return this.mealName;
		}
	}
	
	
	class centerRightPanel extends JPanel {
		public centerRightPanel() {
			this.setLayout(new GridLayout(2, 1, 10, 10));
			
			this.add(new centerRight_up());
			this.add(new centerRight_down());
		}
	}
	
	class centerRight_up extends JPanel {
		public centerRight_up() {
			this.setLayout(new BorderLayout());
			
			payLb = new JLabel("총결제금액 : 0원");
			payLb.setHorizontalAlignment(JLabel.RIGHT);
			add(payLb, BorderLayout.NORTH);
			
			col = new Vector<String>();
			for(int i=0; i<colTx.length; i++) {
				col.add(colTx[i]);
			}
			data = new Vector<Vector<String>>();
			model = new DefaultTableModel(data, col);
			
			jt = new JTable(model);
			jt.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					int selection = jt.getSelectedRow();
					if(e.getClickCount()==2) {
						// 더블클릭할때
						Vector<String> v = data.get(selection);
						model.removeRow(selection);
						for(int i=0; i<makeBtn.length; i++) {
							if(makeBtn[i].getMealName().equals(v.get(1))) {
								makeBtn[i].setEnabled(true);
								payInt -= Integer.parseInt(v.get(2)) * Integer.parseInt(v.get(3));
								payLb.setText("총결제금액 : " + df.format(payInt) + "원");
							}
						}
						jt.updateUI();
					}	
				}
			});
			
			JScrollPane jps = new JScrollPane(jt);
			//이미 테이블에 들어갈 값이 정해져 있고 그게 스크롤이 필요하지 않은 경우에는 굳이 JScrollPane을 안해줘도 되는 것 같고
			//여기처럼 미리 정해진 값이 없고 변동이 생기는 테이블은 JScrollPane을 안해주니까 테이블 자체가 뜨지 않음
			this.add(jps, BorderLayout.CENTER);
			this.add(new centerRight_up_south(), BorderLayout.SOUTH);
		}
	}
	
	class centerRight_up_south extends JPanel {
		public centerRight_up_south() {
			selectTf[0] = new JTextField(10);
			selectTf[1] = new JTextField(3);
			
			for(int i=0; i<selectLb.length; i++) {
				selectLb[i] = new JLabel(selectLbTx[i]);
				this.add(selectLb[i]);
				this.add(selectTf[i]);
			}
		}
	}
	
	class centerRight_down extends JPanel {
		public centerRight_down() {
			this.setLayout(new BorderLayout());
			
			add(new centerRight_down_center(), BorderLayout.CENTER);
			add(new centerRight_down_south(), BorderLayout.SOUTH);
			add(new confirm_or_reset(), BorderLayout.EAST);
		}
	}
	
	// 숫자판 들어가는 패널
	class centerRight_down_center extends JPanel {
		public centerRight_down_center() {
			this.setLayout(new BorderLayout());
			
			add(new numberPad(), BorderLayout.CENTER);
		}
	}
	
	class numberPad extends JPanel {
		public numberPad() {
			this.setLayout(new BorderLayout());
			
			add(new numberPad_big(), BorderLayout.CENTER);
			add(numBtn[0], BorderLayout.SOUTH);
		}
	}
	
	class numberPad_big extends JPanel {
		public numberPad_big() {
			this.setLayout(new GridLayout(3, 3));
			for(int i=0; i<numBtn.length; i++) {				
				numBtn[i] = new JButton(Integer.toString(i));
				numBtn[i].addActionListener(new numberListener());
			}
			for(int i=1; i<numBtn.length; i++)
				add(numBtn[i]);
		}
	}
	
	//숫자 버튼에 붙이는 리스너
	class numberListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String after = e.getActionCommand();
			String before = selectTf[1].getText();
			if(before.length() >= 2)
				selectTf[1].setText("");
			else
				selectTf[1].setText(before + after);
		}
	}
	
	//입력, 초기화 버튼 부분
	class confirm_or_reset extends JPanel {
		public confirm_or_reset() {
			this.setLayout(new GridLayout(2, 1));
			
			confirm = new JButton("입력");
			reset = new JButton("초기화");
			
			add(confirm);
			add(reset);
			
			confirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String checkFood = "select maxCount from meal where mealName='" + selectTf[0].getText() + "'";
					
					if(selectTf[0].getText().equals(""))
						JOptionPane.showMessageDialog(null, "품명을 선택해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
					if(selectTf[1].getText().equals(""))
						JOptionPane.showMessageDialog(null, "수량을 지정해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
					
					Connection con = Driver_connect.makeConnection("meal");
					
					try {
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery(checkFood);
						
						int foodCount=0;
						while(rs.next())
							foodCount = Integer.parseInt(rs.getString(1));
						//ResultSet을 활용할때는 그 값이 꼭 하나만 나오는건 아니여서 while문을 같이 써줘야함
						
						if(Integer.parseInt(selectTf[1].getText()) > foodCount || Integer.parseInt(selectTf[1].getText())>10) {
							JOptionPane.showMessageDialog(null, "조리가능수량을 초과하였습니다.", "Message", JOptionPane.ERROR_MESSAGE);
							selectTf[1].setText("");
						}
						
						String addFood = "select mealNo, mealName, price from meal where mealName='" + selectTf[0].getText() + "'";
						ResultSet rs2 = st.executeQuery(addFood);
						
						while(rs2.next()) {
							Vector<String> v = new Vector<String>();
							for(int i=0; i<colTx.length; i++) {
								if(i==2)
									v.add(selectTf[1].getText());
								else if(i==3)
									v.add(rs2.getString(i));
								else
									v.add(rs2.getString(i+1));
							}
							data.add(v);
							payInt += Integer.parseInt(selectTf[1].getText()) * Integer.parseInt(rs2.getString(3));
							payLb.setText("총결제금액 : " + df.format(payInt) + "원");
						}
						jt.updateUI();
						
						// 입력해서 테이블에 올라가는 메뉴에 해당하는 버튼 찾는 메소드
						for(int i=0; i<makeBtn.length; i++) {
							if(makeBtn[i].getMealName().equals(selectTf[0].getText())) {
								makeBtn[i].setEnabled(false);
							}
						}
						
						selectTf[0].setText("");
						selectTf[1].setText("");
					} catch (Exception ee) {
						System.out.println(ee);
					}
				}
			});
			
			reset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectTf[0].setText("");
					selectTf[1].setText("");
				}
			});
		}
	}
	
	// 결제, 취소 버튼 들어가는 패널
	class centerRight_down_south extends JPanel {
		public centerRight_down_south() {
			this.setLayout(new GridLayout(1,2));
			
			paybtn = new JButton("결제");
			cancel = new JButton("취소");
			
			add(paybtn);
			add(cancel);
			
			paybtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(null, new payCustomerCheck_menu(), "결제자 인증", JOptionPane.OK_CANCEL_OPTION);
					
					if(result == JOptionPane.CANCEL_OPTION) {
						dispose();
						new Main();
					}
					else {
						try {						
							String sql = "select passwd from member where memberNo='" + memberName + "'";
							Connection con = Driver_connect.makeConnection("meal");
							Statement st = con.createStatement();
							ResultSet rs3 = st.executeQuery(sql);
							while(rs3.next()) {							
								if(rs3.getString(1).equals(new String(passField.getPassword()))) {
									JOptionPane.showConfirmDialog(null, "결제과 완료되었습니다.\n식권을 출력합니다.", "Message", JOptionPane.PLAIN_MESSAGE);
									new mealTicket(model, sub, memberName);
									dispose();
									new Main();
								}
								else {
									JOptionPane.showMessageDialog(null, "패스워드가 일치하지 않습니다.", "Message", JOptionPane.ERROR_MESSAGE);
								}
							}
						} catch (Exception ee) {
							System.out.println(ee);
						}
					
					}
				}
			});
			
			cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
					new Main();
				}
			});
		}
	}
	
	//음식 고르고 결제 눌렀을때 사원번호와 비밀번호를 입력하는 부분(JOptionPane.OK_CANCEL_OPTION에 담김)
	class payCustomerCheck_menu extends JPanel {
		public payCustomerCheck_menu() {
			this.setLayout(new GridLayout(2,2));
			
			for(int i=0; i<payCustomerTx.length; i++) {
				payCustomerLb[i] = new JLabel(payCustomerTx[i]);
			}
			
			String sql = "select memberNo from member";
			Connection con = Driver_connect.makeConnection("meal");
			
			try {
				Statement st = con.createStatement();
				ResultSet rs1 = st.executeQuery(sql);
				
				while(rs1.next()) {
					//ResultSet 형식은 겟스트링으로 스트링 형식을 뽑아야 한다
					comboMember.add(rs1.getString(1));
				}
				
				customerCb = new JComboBox<String>(comboMember);
				
				memberName = comboMember.get(cbCount);
				customerCb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JComboBox<String> cb = (JComboBox<String>)e.getSource();
	
						cbCount = cb.getSelectedIndex();
						memberName = comboMember.get(cbCount);
					}
				});
			} catch (Exception e) {
				System.out.println(e);
			}
			
			add(payCustomerLb[0]);
			add(customerCb);
			add(payCustomerLb[1]);
			add(passField);
		}
	}

}
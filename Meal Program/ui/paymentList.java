package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import db.Driver_connect;

//관리자 메뉴에서 결제 조회 버튼을 눌렀을 때 나타나는 프레임
public class paymentList extends JFrame {
	private JLabel lb = new JLabel("메뉴명:");
	private JTextField tf = new JTextField(10);
	private JButton btn [] = new JButton[4];
	private String btnTx [] = {"조회", "새로고침", "파일로 저장", "닫기"};
	
	private Vector<String> col;
	private Vector<Vector<String>> data;
	private JTable jt;
	private DefaultTableModel model = new DefaultTableModel();
	private String colTx [] = {"종류", "메뉴명", "사원명", "결제수량", "총결제금액", "결제일"};
	
	public paymentList() {
		setTitle("결제 조회");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		add(new northPanel(), BorderLayout.NORTH);
		add(new southPanel(), BorderLayout.SOUTH);
		
		setSize(550, 500);
		setVisible(true);
	}
	
	class northPanel extends JPanel {
		public northPanel() {
			add(lb);
			add(tf);
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(btnTx[i]);
				add(btn[i]);
				btn[i].addActionListener(new myAction());
			}
		}
		
		class myAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton click = (JButton)e.getSource();
				if(click == btn[0]) {
					searchT();
				}
				else if(click == btn[1]) {
					showT();
					tf.setText("");
				}
				else if(click == btn[2]) {
					new makeFile();
				}
				else
					dispose();
			}
		}
	}
	
	class southPanel extends JPanel {
		public southPanel() {
			col = new Vector<String>();
			for(int i=0; i<colTx.length; i++) {
				col.add(colTx[i]);
			}
			data = new Vector<Vector<String>>();
			model = new DefaultTableModel(data, col);
			jt = new JTable(model);
			JScrollPane jps = new JScrollPane(jt);
			add(jps);
			
			showT();
		}		
	}
	
	public void searchT() {
		String search = tf.getText();
		String sql = "select orderlist.cuisineNo, meal.mealName, member.memberName, orderCount, amount, orderDate from orderlist "
				+ "inner join member on orderlist.memberNo=member.memberNo join meal on orderlist.mealNo=meal.mealNo "
				+ "where meal.mealName like '%" + search + "%'";
		
		makeTable(sql);
		
	}
	
	public void showT() {
		String sql = "select orderlist.cuisineNo, meal.mealName, member.memberName, orderCount, amount, orderDate from orderlist "
				+ "inner join member on orderlist.memberNo=member.memberNo join meal on orderlist.mealNo=meal.mealNo";
		
		makeTable(sql);
	}
	
	public void makeTable(String sql) {
		try {
			data.clear();
			Connection con = Driver_connect.makeConnection("meal");
			PreparedStatement psmt = con.prepareStatement(sql);
			
			ResultSet rs = psmt.executeQuery();
			
			String sub [] = {"한식", "중식", "일식", "양식"};
			
			while(rs.next()) {
				Vector<String> v = new Vector<String>();
				for(int i=0; i<colTx.length; i++) {
					if(i==0)
						v.add(sub[Integer.parseInt(rs.getString(i+1))-1]);
					else
						v.add(rs.getString(i+1));
				}
				data.add(v);
			}
			jt.updateUI();
		} catch (Exception ee) {
			System.out.println(ee);
		}
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcm = jt.getColumnModel();
		
		for(int i=0; i<tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellRenderer(dtcr);
		}
	}
	
	
	class makeFile {
		public makeFile() {
			FileDialog fdSave = new FileDialog(paymentList.this, "텍스트 파일로 저장하기", FileDialog.SAVE);
			fdSave.setVisible(true);
			
			String path = fdSave.getDirectory();
			String name = fdSave.getFile() + ".txt";
			
			if(path == null) {
				return;
			}
			
			File file = new File(path);
			BufferedWriter br = null;
			
			try {
				br = new BufferedWriter(new FileWriter(file + "/" + name));
				String answer = writeText();
				
				br.write(answer);
				br.flush();
				
				JOptionPane.showMessageDialog(paymentList.this, "텍스트 파일이 생성되었습니다.");
				br.close();
			} catch (Exception ee) {}
		}
		
		String writeText() {
			String text = "종류\t메뉴명\t사원명\t결제수량\t총결제금액\t결제일\r\n";
			
			for(int i=0; i<data.size(); i++) {
				for(int j=0; j<col.size(); j++) {
					text = text.concat(data.get(i).get(j) + "\t");
				}
				text = text.concat("\r\n");
			}
			return text;
			//이거 굳이 백터해서 담고 그럴 필요 없이 data에서 get(i).get(j) 방식으로 값만 뽑아오면 됨
		}
	}
}
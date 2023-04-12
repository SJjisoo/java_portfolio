package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

//메인 메뉴에서 고객조회를 눌렀을때
public class insurance_search extends JFrame {
	private JButton btn [] = new JButton[5];
	private String btnTx [] = {"조회", "전체보기", "수정", "삭제", "닫기"};
	private JTextField tf = new JTextField(10);
	private Vector<String> col;
	private Vector<Vector<String>> data;
	private JTable jt;
	private DefaultTableModel model = new DefaultTableModel();
	private String colTx [] = {"code", "name", "birth", "tel", "address", "company"};
	private Vector<String> vc;
	
	public insurance_search() {
		setTitle("고객조회");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		c.add(new northPanel(), BorderLayout.NORTH);
		c.add(new centerPanel(), BorderLayout.CENTER);
		
		setSize(600,550);
		setVisible(true);
	}
	
	class northPanel extends JPanel {
		public northPanel() {
			setLayout(new FlowLayout());
			
			JLabel lb = new JLabel("성명");
			add(lb); add(tf);
			
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(btnTx[i]);
				add(btn[i]);
				btn[i].addActionListener(new menuListener());
			}
		}
	}
	
	class centerPanel extends JPanel {
		public centerPanel() {
			col = new Vector<String>();
			for(int i=0; i<colTx.length; i++) {
				col.add(colTx[i]);
			}
			data = new Vector<Vector<String>>();
			model = new DefaultTableModel(data, col);
			
			jt = new JTable(model);
			
			JScrollPane jps = new JScrollPane(jt);
			add(jps);
			
			jt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {	
					int selection = jt.getSelectedRow();
					vc = data.get(selection);
				}
			});
		}
	}
	
	class menuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String comand = e.getActionCommand();
			switch(comand) {
				case "조회" : searchCustomer(); break;
				case "전체보기" : listCustomer(); break;
				case "수정" :
					new insurance_search_fix(vc);
					dispose();
					break;
				case "삭제" : deleteCustomer(); break;
				case "닫기" : dispose(); break;
			}
			
		}
	}
	
	void listCustomer() {
		String sql = "select * from customer";
		try {
			data.clear();
			Connection conn = db.Driver_connect.makeConnection("customer");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				Vector<String> v = new Vector<String>();
				for(int i=0; i<colTx.length; i++) {
					v.add(rs.getString(i+1));
				}
				data.add(v);
			}
			jt.updateUI();
		} catch (Exception ee) {
			System.out.println(ee);
		}
	}
	
	void searchCustomer() {
		String sql = "select * from customer where name like'" + tf.getText() + "%'";
		try {
			data.clear();
			Connection conn = db.Driver_connect.makeConnection("customer");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				Vector<String> v = new Vector<String>();
				for(int i=0; i<colTx.length; i++) {
					v.add(rs.getString(i+1));
				}
				data.add(v);
			}
			jt.updateUI();
		} catch (Exception ee) {
			System.out.println(ee);
		}
	}
	
	void deleteCustomer() {
		String selectName = vc.get(1);
		String sql = "delete from customer where name='" + selectName + "'";
		
		try {
			Connection conn = db.Driver_connect.makeConnection("customer");
			Statement stmt = conn.createStatement();
			
			int result = JOptionPane.showConfirmDialog(null, selectName + "님을 정말 삭제하시겠습니까?");
			if(result == JOptionPane.CLOSED_OPTION) {
				listCustomer();
			}
			else if(result == JOptionPane.YES_OPTION) {
				stmt.executeUpdate(sql);
				listCustomer();
				// jt.updateUI()는 테이블 자체에서 뭔가를 지우거나 뺐을때 그걸 새로고침 하는거라고 보면 됨
				// 여기서는 데이터베이스에서 삭제를 하기 때문에 그냥 전체 리스트 불러오는 메소드를 한번 활용하면 됨
			}
			else {
				listCustomer();
			}
		} catch (Exception e) {
			System.out.println(e);
		}	
	}
}

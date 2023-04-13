package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import db.Driver_connect;

//메인 메뉴에서 고객 관리 버튼 눌렀을 때
public class insurance_control extends JFrame {
	private JLabel lb [] = new JLabel[4];
	private JLabel lb2 [] = new JLabel[3];
	private String lbTx [] = {"고객코드 : ", "고 객 명 : ", "생년월일 : ", "연 락 처 : "};
	private String lbTx2 [] = {"보험상품 : ", "가입금액 : ", "월보험료 : "};
	private JTextField tf [] = new JTextField[5];
	private JComboBox<String> cb1 = new JComboBox<String>();
	private JComboBox<String> cb2 = new JComboBox<String>();
	private JComboBox<String> adminCb = new JComboBox<String>();
	private Vector<String> customerName, adminName, contractName;
	private JButton [] btn = new JButton[4];
	private String btnTx [] = {"가입", "삭제", "파일로저장", "닫기"};
	
	private Vector<String> col;
	private Vector<Vector<String>> data;
	private JTable jt;
	private DefaultTableModel model = new DefaultTableModel();
	private String colTx [] = {"customerCode", "contractName", "regPrice", "regDate", "monthPrice", "adminName"};
	
	private Vector<String> vc = new Vector<String>();
	String text;
	
	//첫 화면
	public insurance_control() {
		setTitle("보험 계약");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		add(new northPanel(), BorderLayout.NORTH);
		add(new southPanel(), BorderLayout.SOUTH);
		
		setSize(800,650);
		setVisible(true);
	}
	
	// 위쪽 패널
	class northPanel extends JPanel {
		public northPanel() {
			makeCustomerName();
			makeContractName();

			cb1 = new JComboBox<String>(customerName);
			cb2 = new JComboBox<String>(contractName);
			
			setLayout(new BorderLayout());
			
			add(new north_center(), BorderLayout.CENTER);
			add(new north_south(), BorderLayout.SOUTH);
		}
	}
	
	//위쪽-위쪽 패널
	class north_center extends JPanel {
		public north_center() {
			setLayout(new GridLayout(1,2));
			
			add(new north_center1());
			add(new north_center2());
		}
	}
	
	//위쪽-아래쪽 패널
	class north_south extends JPanel {
		public north_south() {
			setLayout(new FlowLayout());
			
			makeAdminName();
			
			JLabel adminLb = new JLabel("담당자:");
			add(adminLb);
			adminCb = new JComboBox<String>(adminName);
			add(adminCb);
			
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(btnTx[i]);
				add(btn[i]);
			}
			
			btn[0].addActionListener(new joinListener());
			btn[1].addActionListener(new deleteListener());
			btn[2].addActionListener(new saveListener());
			btn[3].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
	}
	
	//위쪽-위쪽의 좌측 패널
	class north_center1 extends JPanel {
		public north_center1() {
			setLayout(new GridLayout(4,2));
			
			for(int i=0; i<lb.length; i++) {
				lb[i] = new JLabel(lbTx[i]);
				add(lb[i]);
				if(i == 0) {
					tf[i] = new JTextField(10);
					add(tf[i]);
				}
				else if(i == 1) {
					add(cb1);
				}
				else {
					tf[i-1] = new JTextField(10);
					add(tf[i-1]);
				}
			}
			tf[0].setEnabled(false);
			
			// 디폴트로 첫 고객 정보 뜨게 하는것
			setTextField(customerName.get(0));
			
			cb1.addActionListener(new customerListener());
		}
	}
	
	////위쪽-위쪽의 우측 패널
	class north_center2 extends JPanel {
		public north_center2() {
			setLayout(new GridLayout(3,2));
			
			for(int i=0; i<lb2.length; i++) {
				lb2[i] = new JLabel(lbTx2[i]);
			}
			tf[3] = new JTextField(10);
			tf[4] = new JTextField(10);
			
			add(lb2[0]); add(cb2);
			add(lb2[1]); add(tf[3]);
			add(lb2[2]); add(tf[4]);
		}
	}
	
	//아래쪽 패널
	class southPanel extends JPanel {
		public southPanel() {
			setLayout(new BorderLayout());
			
			JLabel listLb = new JLabel("<고객 보험 계약현황>");
			listLb.setHorizontalAlignment(JLabel.CENTER);
			add(listLb, BorderLayout.NORTH);
			
			col = new Vector<String>();
			for(int i=0; i<colTx.length; i++)
				col.add(colTx[i]);
			
			data = new Vector<Vector<String>>();
			model = new DefaultTableModel(data, col);
			
			jt = new JTable(model);
			
			JScrollPane jps = new JScrollPane(jt);
			add(jps, BorderLayout.CENTER);
			
			jt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {	
					int selection = jt.getSelectedRow();
					vc = data.get(selection);
				}
			});
			
			setTable(tf[0].getText());
		}	
	}
	
	//customer 목록 콤보박스 만드는 메소드
	void makeCustomerName() {
		String sql = "select name from customer";
		customerName = new Vector<String>();
		
		try {
			Connection con = Driver_connect.makeConnection("customer");
			PreparedStatement psmt = con.prepareStatement(sql);
			
			ResultSet rs = psmt.executeQuery(sql);
			
			while(rs.next())
					customerName.add(rs.getString(1));
			//mysql 명령어에서는 ? ? ? 이런식으로 해놓고 불러올때도 그렇고 숫자가 1부터 시작함
			//기존에 자바에서 배열 등이 0부터 시작하는거랑 다른걸 유의해야함
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	//보험상품 목록 콤보박스 만드는 메소드
	void makeContractName() {
		String sql = "select distinct contractName from contract";
		contractName = new Vector<String>();
		
		try {
			Connection con = Driver_connect.makeConnection("customer");
			PreparedStatement psmt = con.prepareStatement(sql);
			
			ResultSet rs = psmt.executeQuery(sql);
			
			while(rs.next())
				contractName.add(rs.getString(1));
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	// 담당자 목록 콤보박스 만드는 메소드
	void makeAdminName() {
		String sql = "select name from admin";
		adminName = new Vector<String>();
		
		try {
			Connection con = Driver_connect.makeConnection("customer");
			PreparedStatement psmt = con.prepareStatement(sql);
			
			ResultSet rs = psmt.executeQuery(sql);
			
			while(rs.next())
					adminName.add(rs.getString(1));
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	// 고객 이름 선택했을때 액션
	class customerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> aCb = (JComboBox<String>)e.getSource();
			int index = aCb.getSelectedIndex();
			String name = customerName.get(index);
			setTextField(name);
			setTable(tf[0].getText());
		}
	}
	
	// 고객 이름 선택했을때 코드, 생년월일, 전화번호가 TF에 뜨게하는 메소드
	void setTextField(String name) {
		String sql = "select code, birth, tel from customer where name='" + name + "'"; 
		
		try {
			Connection con = Driver_connect.makeConnection("customer");
			PreparedStatement psmt = con.prepareStatement(sql);
			
			ResultSet rs = psmt.executeQuery(sql);
			
			while(rs.next()) {	
				for(int i=0; i<3; i++) {
					tf[i].setText(rs.getString(i+1));
				}
			}
		} catch (Exception ee) {
			System.out.println(ee);
		}
	}
	
	// 고객 이름 선택했을때 테이블이 뜨게 하는 메소드
	void setTable(String code) {
		String sql = "select * from contract where customerCode='" + code + "' order by regDate desc";
		// 내림차순 정렬은 order by 원하는컬럼 desc;
		// 오름차순 정렬은 order by 원하는컬럼 asc;
		// select등의 명령어에 뒤에 띄어쓰기하고 그대로 해주면 됨
		
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
	
	//가입 버튼 눌렀을때 리스너
	class joinListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String sql = "insert into contract values (?, ?, ?, ?, ?, ?)";
				
				Date utilDate = new Date();
				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				
				Connection conn = db.Driver_connect.makeConnection("customer");
				
				PreparedStatement psmt = conn.prepareStatement(sql);
				
				psmt.setString(1, tf[0].getText());
				psmt.setString(2, contractName.get(cb2.getSelectedIndex()));
				psmt.setString(3, tf[3].getText());
				psmt.setDate(4, sqlDate);
				psmt.setString(5, tf[4].getText());
				psmt.setString(6, adminName.get(adminCb.getSelectedIndex()));
				// 콤보박스에서 텍스트를 가져올때는 단순하게 겟텍스트, 겟액션커맨드를 하는게 아니라
				// 그 콤보박스 안에서 몇번째를 선택한건지 겟 셀렉티드인덱스를 가져오고
				// 그 콤보박스를 만들때 사용한 벡터안에서 해당하는 인덱스의 텍스트를 get으로 가져와야함
				
				psmt.executeUpdate();
				
				setTable(tf[0].getText());
			} catch (Exception ee) {
				System.out.println(ee);
			}
		}
	}
	
	//삭제 버튼 눌렀을때 리스너
	class deleteListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String code = tf[0].getText();
			String selectContract = vc.get(1);
			int result = JOptionPane.showConfirmDialog(null, code + "(" + vc.get(1) + ")을 삭제하시겠습니까?", "계약정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result == 0) {
				String sql = "delete from contract where contractName='" + selectContract + "'";
				
				try {
					Connection conn = db.Driver_connect.makeConnection("customer");
					Statement stmt = conn.createStatement();
					
					stmt.executeUpdate(sql);
					
					setTable(tf[0].getText());
				} catch (Exception ee) {
					System.out.println(ee);
				}	
			}
		}
	}
	
	//파일로 저장 눌렀을때 리스너
	class saveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			FileDialog fdSave = new FileDialog(insurance_control.this, "텍스트 파일로 저장하기", FileDialog.SAVE);
			fdSave.setVisible(true);
			
			String path = fdSave.getDirectory();
			String name = fdSave.getFile();
			
			if(path == null) {
				return;
			}
			
			File file = new File(path);
			BufferedWriter br = null;
			
			try {
				br = new BufferedWriter(new FileWriter(file + "/" + name));
				
				
				br.write(writeText());
				br.flush();
				
				JOptionPane.showMessageDialog(insurance_control.this, "파일이 생성되었습니다.");
				
				br.close();
			} catch (Exception ee) {}
		}
	}
	
	String writeText() {
		String text = "고객명 : " + customerName.get(cb1.getSelectedIndex()) + "(" + tf[0].getText() + ")\r\n\r\n" + "담당자명 : " + adminName.get(adminCb.getSelectedIndex()) + "\r\n\r\n";
		text += "보험상품\t가입금액\t가입일\t월보험료\r\n";
		
		for(int i=0; i<data.size(); i++) {
			vc = data.get(i);
			text = text.concat(vc.get(1) + "\t");
			text = text.concat(vc.get(2) + "\t");
			text = text.concat(vc.get(3) + "\t");
			text = text.concat(vc.get(4) + "\r\n");
		}
		return text;
	}
	//텍스트 파일로 보내줄때는 \n만 가지고는 줄바꿈이 안됨 \r도 같이 해줘야 줄바꿈이 됨
}

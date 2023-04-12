package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import db.Driver_connect;

//관리자 메뉴에서 메뉴 관리를 눌렀을때 나타나는 프레임
public class menuManagement extends JFrame {
	private JLabel lb = new JLabel("종류:");
	private JComboBox<String> cb;
	private String cbTx [] = {"한식", "중식", "일식", "양식"};
	private JButton btn [] = new JButton[5];
	private String btnTx [] = {"검색", "수정", "삭제", "오늘의메뉴 선정", "닫기"};
	
	private Vector<String> col;
	private Vector<Vector<Object>> data;
	private JTable jt;
	private DefaultTableModel model = new DefaultTableModel();
	private String colTx [] = {"□", "menuName", "price", "maxCount", "todayMeal"};
	private Vector<Object> vc;
	
	public menuManagement() {
		setTitle("메뉴 관리");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		add(new northPanel(), BorderLayout.NORTH);
		add(new centerPanel(), BorderLayout.CENTER);
		
		setSize(500, 500);
		setVisible(true);
	}
	
	class northPanel extends JPanel {
		public northPanel() {
			this.add(lb);
			this.add(cb = new JComboBox<String>(cbTx));
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(btnTx[i]);
				this.add(btn[i]);
				btn[i].addActionListener(new btnAction());
			}
		}
		
		class btnAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton click = (JButton)e.getSource();
				if(click == btn[0]) searchAction();
				else if(click == btn[1]) fixAction();
				else if(click == btn[2]) {
					deleteAction(); searchAction();
				}
				else if(click == btn[3]) {
					todayMenuAction(); searchAction();
				}
				else
					dispose();
				//수정, 삭제를 할때 그 작업을 한 결과물이  바로 새로고침이 되게 하려면
				//결국 서치리스너를 실행되게 해야하는데 액션리스너는 무슨 행동을 할때만 실행되는거지
				//메소드처럼 불러온다고 실행이 되는게 아님
				//결국 버튼마다 같은 리스너를 달아주고 어떤 버튼을 클릭한건지 구별해서
				//특정 메소드들이 실행되게 하면 하나의 버튼을 눌렀을때 2개 이상의 작업이 되게 할 수 있음
			}
			
		}
		
		public void searchAction() {
			String choice = Integer.toString(cb.getSelectedIndex()+1);
			String sql = "select mealName, price, maxCount, todayMeal from meal where cuisineNo=" + choice;
			
			try {
				data.clear();
				Connection con = Driver_connect.makeConnection("meal");
				PreparedStatement psmt = con.prepareStatement(sql);
				
				ResultSet rs = psmt.executeQuery();
				
				while(rs.next()) {
					Vector<Object> v = new Vector<Object>();
					v.add(false);
					for(int i=0; i<colTx.length-1; i++) {
						if(i==3) {
							if(rs.getString(i+1).equals("0"))
								v.add("N");
							else
								v.add("Y");
						}
						else
							v.add(rs.getString(i+1));
					}
					data.add(v);
					//체크박스는 String형식이 아니라 boolean 형식이기 때문에 data에 담을 백터 v도 object 형식이여야 하고
					//이 벡터 v를 담을 data도 object 형식이여야 함
				}
				jt.updateUI();
			} catch (Exception ee) {
				System.out.println(ee);
			}
			
			//테이블 내용들 가운데 정렬 방법
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			TableColumnModel tcm = jt.getColumnModel();
			
			for(int i=1; i<tcm.getColumnCount(); i++) {
				tcm.getColumn(i).setCellRenderer(dtcr);
			}
		}
		
		public void fixAction() {
			int i = 0;
			int cnt = 0;
			int selectRow = 0;
			for(i=0; i<jt.getRowCount(); i++) {
				if((boolean)jt.getValueAt(i, 0)) {
					cnt++;
					selectRow = i;
				}
			}
			if(cnt == 0) {
				JOptionPane.showMessageDialog(null, "수정할 메뉴를 선택해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
			}
			else if(cnt>1) {
				JOptionPane.showMessageDialog(null, "하나씩 수정가능합니다.", "Message", JOptionPane.ERROR_MESSAGE);
			}
			else {
				Vector<String> send = new Vector<String>();
				send.add(Integer.toString(cb.getSelectedIndex()));					
				for(int j=1; j<=3; j++) {
					send.add((String)jt.getValueAt(selectRow, j));
				}
				new fixMenu(send);
			}
		}
		
		public void deleteAction() {
			String sqlFinal = null;
			String sql = "delete from meal where mealName='";
			
			try {					
				Vector<String> selectRowV = new Vector<String>();
				for(int i=0; i<jt.getRowCount(); i++) {					
					if((boolean)jt.getValueAt(i, 0)) {
						selectRowV.add((String)jt.getValueAt(i, 1));
					}
				}
				
				Connection con = Driver_connect.makeConnection("meal");
				Statement smt = con.createStatement();
				
				for(int i=0; i<selectRowV.size(); i++) {
					sqlFinal = sql + selectRowV.get(i) + "'";
					smt.executeUpdate(sqlFinal);
				}
				//for문으로 하나의 sql안에 where mealName 조건이 뒤에 붙게 여러개를 만들어줄 경우에
				//기존처럼 String a 해놓고 sql문 뒤에 + a해도 대입이 안됨
				//차라리 for문 안에서 sql 구문뒤에 특정값을 합쳐주게 조립을 해야 됨
				//이건 원리는 100% 이해 되지는 않는데 일단은 기존에 내가 하던대로 해서 안됐을 경우에
				//이런식으로 해야 한다고 기억을 해둬야 할듯
			} catch (Exception ee) {
				System.out.println(ee);
			}
		}
		
		public void todayMenuAction() {
			String nSqlFinal = null, ySqlFinal = null;
			String nSql = "update meal set todayMeal=0 where mealName='";
			String ySql = "update meal set todayMeal=1 where mealName='";
			int cnt = 0;
			
			try {					
				Connection con = Driver_connect.makeConnection("meal");
				Statement smt = con.createStatement();
				//sql문을 조립하고 나중에 조립된 sql문을 써야 할 경우에는 그냥 스테이트먼트를 쓰고 대입해줘야함
				//?를 해두고 셋을 해서 값을 넣어주고 실행할 때는 프리페어스테이트먼트를 쓰는 듯
				for(int i=0; i<jt.getRowCount(); i++) {
					if((boolean)jt.getValueAt(i, 0))
						cnt++;
				}
				
				if(cnt>25) {
					JOptionPane.showMessageDialog(null, "25개를 초과할 수 없습니다.", "Message", JOptionPane.ERROR_MESSAGE);
				} else {						
					for(int i=0; i<jt.getRowCount(); i++) {
						if((boolean)jt.getValueAt(i, 0)) {
							ySqlFinal = ySql + (String)jt.getValueAt(i, 1) + "'";
							smt.executeUpdate(ySqlFinal);
						} else {							
							nSqlFinal = nSql + (String)jt.getValueAt(i, 1) + "'";
							smt.executeUpdate(nSqlFinal);
						}
					}
				}
			} catch (Exception ee) {
				System.out.println(ee);
			}
		}
	}
	
	class centerPanel extends JPanel {
		public centerPanel() {
			col = new Vector<String>();
			for(int i=0; i<colTx.length; i++) {
				col.add(colTx[i]);
			}
			data = new Vector<Vector<Object>>();
			//체크 박스 넣는 방법.
			//디펄트테이블 모델 자체가 가지고 있는 겟컬럼클래스 메소드를 오버라이딩하고
			//거기서 0번째 컬럼(체크박스가 있는 컬럼)은 불리언(트루, 펄스)모드로 한다는 설정
			model = new DefaultTableModel(data, col) {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					switch(columnIndex) {
						case 0 :
							return Boolean.class;
						default :
							return String.class;
					}
				}
			};
			
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
}
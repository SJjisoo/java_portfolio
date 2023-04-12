package ui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

//고객조회 창에서 수정 버튼을 눌렀을 때
public class insurance_search_fix extends JFrame {
	private JLabel [] lb = new JLabel[6];
	private String [] lbTx = {"고객코드", "고 객 명 :", "생년월일:", "연 락 처 :", "주  소:", "회 사 명 :"};
	private JTextField [] tf = new JTextField[6];
	private JButton btn [] = new JButton[2];
	private String btnTx [] = {"수정", "취소"};

	public insurance_search_fix(Vector<String> vc) {
		setTitle("고객수정");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new GridLayout(7, 2));
		
		for(int i=0; i<lb.length; i++) {
			lb[i] = new JLabel(lbTx[i]);
			tf[i] = new JTextField(10);
			add(lb[i]); add(tf[i]);
		}
		tf[0].setEnabled(false);
		tf[1].setEnabled(false);
		for(int j=0; j<btn.length; j++) {
			btn[j] = new JButton(btnTx[j]);
			add(btn[j]);
		}
		
		for(int i=0; i<vc.size(); i++) {
			tf[i].setText(vc.get(i));
		}
		
		btn[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fixCustomer(vc);
			}
		});
		btn[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setSize(300,200);
		setVisible(true);
	}
	
	void fixCustomer(Vector<String> vc) {
		try {
			Connection conn = db.Driver_connect.makeConnection("customer");
			String sql = "update customer set birth=?, tel=?, address=?, company=? where name='" + vc.get(1) + "'";
			PreparedStatement psmt = conn.prepareStatement(sql);
			
			Vector<String> tfV = new Vector<String>();
			
			for(int i=2; i<tf.length; i++) {
				tfV.add(tf[i].getText());
			}
			for(int j=0; j<4; j++) {
				psmt.setString(j+1, tfV.get(j));
			}
			
			int re = psmt.executeUpdate();
			if(re>0) {
				JOptionPane.showMessageDialog(this, "수정완료");
				dispose();
				new insurance_search();
			}
			else {
				JOptionPane.showMessageDialog(this, "수정실패");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

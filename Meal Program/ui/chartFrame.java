package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import db.Driver_connect;

//관리자 메뉴에서 종류별 차트 버튼을 눌렀을 때 나타나는 프레임
public class chartFrame extends JFrame {
	private int cuisineSum [];
	
	private String cuisineName [] = {"한식", "중식", "일식", "양식"};
	
	private JButton btn [] = new JButton[2];
	private String btnTx [] = {"차트이미지저장", "닫기"};
	
	private double allCuisine = 0;
	private Color colorRandom [] = new Color[4];
	
	public chartFrame() {
		setTitle("관리");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		countFood();
		makeColor();
		
		add(new graphPanel(), BorderLayout.CENTER);
		add(new northPanel(), BorderLayout.NORTH);
		
		setSize(400, 400);
		setVisible(true);
	}
	
	public void countFood() {
		cuisineSum = new int[4];
		String cuisineNum = "";
		String sql = "select sum(orderCount) from orderlist where cuisineNo=";
		
		try {
			Connection con = Driver_connect.makeConnection("meal");
			Statement smt = con.createStatement();
			//프리페어스테이트먼트는 con.preparedstatement(sql)이런식으로 이미 sql문을 넣어주기 때문에
			//sql을 미리 뒤에 들어갈 칸을 비워놓을 경우 sql 오류가 뜸
			//그냥 스테이트먼트 자체를 con.크리트스테이트먼트 해주고 그 뒤에 조합된 sql문을
			//익스큐트쿼리해줘야함
			for(int i=1; i<=4; i++) {
				String sqlAfter = sql;
				cuisineNum = Integer.toString(i);
				sqlAfter += cuisineNum;
				
				ResultSet rs = smt.executeQuery(sqlAfter);
				
				while(rs.next()) {
					cuisineSum[i-1] = Integer.parseInt(rs.getString(1));
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void makeColor() {
		for(int i=0; i<colorRandom.length; i++) {
			int r = (int)(Math.random()*255+1);
			int g = (int)(Math.random()*255+1);
			int b = (int)(Math.random()*255+1);
			colorRandom[i] = new Color(r,g,b);
		}
	}
	
	class graphPanel extends JPanel {
		int start = 0;
		int cuisineAngle [] = new int[4];
		
		public graphPanel() {
			setLayout(null);
			
			for(int i=0; i<cuisineSum.length; i++)
				allCuisine += cuisineSum[i];
			
			for(int i=0; i<cuisineSum.length; i++) {
				cuisineAngle[i] = (int)(Math.ceil((cuisineSum[i]/allCuisine)*360));
				//반올림하면 전부 내림이거나 내림이 더 많은 경우에 칸이 미세하게 벌어져서 그냥 다 올림함
			}
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			for(int i=0; i<cuisineSum.length; i++) {
				g.setColor(colorRandom[i]);
				g.fillArc(30, 40, (int)((double)getWidth()/100*60), (int)((double)getHeight()/100*70), start, cuisineAngle[i]);
				start += cuisineAngle[i];
			}
			
			int x = (int)((double)getWidth()/100*72);
			int y = (int)((double)getHeight()/100*35);
			
			for(int i=0; i<cuisineSum.length; i++) {
				g.setColor(colorRandom[i]);
				g.fillRect(x, y, 10, 10);
				g.setColor(Color.BLACK);
				g.setFont(new Font("Dotum", Font.ITALIC, 12));
				g.drawString(cuisineName[i] + " (" + cuisineSum[i] + "건)", x+15, y+9);
				y += 20;
			}
		}
	}
	
	class northPanel extends JPanel {
		public northPanel() {
			add(new JLabel("종류별 결제건수 통계챠트"));
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
					saveImage();
				}
				else
					dispose();
			}
		}
	}
	
	public void saveImage() {
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String watch = sdf.format(time);
		
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		paint(g2);
		try {
			ImageIO.write(image, "jpg", new File(watch + "-종류별결제현황차트.jpg"));
			JOptionPane.showMessageDialog(this, "차트 이미지를 저장했습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
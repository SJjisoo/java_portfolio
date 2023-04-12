package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

//사용자메뉴에서 음식을 고르고 결제자 인증까지 마쳤을때 고른 음식들로 식권이 만들어지는 부분)
public class mealTicket extends JFrame {
	private String mealName;
	private int pay, mealCnt;
	private String countLb;
	private Vector<ticketMake> ticketV = new Vector<ticketMake>();
	
	//식권 금액 표시
	private DecimalFormat df = new DecimalFormat("#,###");
	//식권에 시간등록
	private String watch;
	private JLabel watchLb;
	//생성자 래퍼런스로 받은 멤버이름(5자리숫자로된것), 메뉴번호를 담을 요소
	private String memberName;
	private int menuNum;
	
	public mealTicket(DefaultTableModel model, int menuNum, String memberName) {
		int ticketCnt = 0;
		
		//래퍼런스로 받아온것들 여기서 적용하기
		this.memberName = memberName;
		this.menuNum = menuNum;
		
		//일단 식권 수를 다 뽑아야 하니까 (품명은 2개여도 2개씩 주문하면 4장을 뽑아야 하니까
		//그걸 뽑아내는 식
		for(int i=0; i<model.getRowCount(); i++) {
			String cntSt = (String)model.getValueAt(i, 2);
			ticketCnt += Integer.parseInt(cntSt);
			//getValueAt이 오브젝트 형으로 받아지는건데 이유는 모르겠는데
			//(int)는 안먹히고 (String)만 먹힘
			//String으로 받아서 parseInt로 int형으로 바꿔서 계산해야할듯
		}
		
		setTitle("결제");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new GridLayout(ticketCnt, 1, 10, 10));
		
		int colorNum = 0;
		
		//식권을 반복해서 만들어내고 벡터에 담는 식
		for(int i=0; i<model.getRowCount(); i++) {
			mealName = (String) model.getValueAt(i, 1);
			mealCnt = Integer.parseInt((String)model.getValueAt(i, 2));
			pay = Integer.parseInt((String)model.getValueAt(i, 3));
			
			for(int j=0; j<mealCnt; j++) {
				ticketV.add(new ticketMake(mealName, j+1 + "/" + mealCnt, pay, colorNum));
			}
			//식권에 1/2 2/2 이런식으로 표시해야 하니까 for문이 아니라 if문으로 해서
			colorNum++;
			colorNum %= 2;
		}
		
		for(int i=0; i<ticketV.size(); i++) {
			this.add(ticketV.get(i));
		}
		
		setSize(300, 150*ticketCnt);
		setVisible(true);
		
		save();
		
		dispose();
	}
	
	//프레임을 이미지로 저장하는 방법
	public void save() {		
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		paint(g2);
		try {
			ImageIO.write(image, "jpg", new File(watchLb.getText() + "-ticket.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//나는 지금까지 페널 안에 또 페널을 만들면 형제 관계로 다 따로 클래스를 만들었는데
	//이런식으로 상위 패널 안에 하위 패널들을 다 같이 묶어주면
	//여기서 private Color color 쓴것처럼
	//이 영역 안에서 전역변수를 사용하기 용이함
	//최상단에 전역변수가 너무 많아도 안 좋기 때문에 패널 안에서만 쓸만한 것들은 패널 단위로 묶어서 전역변수를 해주는게 좋을듯
	//(일단 지금 내 지식 안에서는 패널 컬러 같은 속성 바꿔주는 것들은 패널안에서만 이루어져서 쓰면 좋을듯)
	class ticketMake extends JPanel {	
		private Color color ;
		public ticketMake(String mealName, String cntSlush, int pay, int colorNum) {
			this.setLayout(new BorderLayout());
			//밑에 두개 다 안됩니다...
			countLb = cntSlush;
			//countLb = new String(countLb);
			//테스트
			System.out.println(mealName + " " + countLb + " " + pay);
			
			if(colorNum == 0)
				color = Color.CYAN;
			else
				color = Color.PINK;
			this.setBackground(color);
			
			this.add(new ticketMake_north(), BorderLayout.NORTH );
			this.add(new ticketMake_center(), BorderLayout.CENTER);
			this.add(new ticketMake_south(), BorderLayout.SOUTH);
		}
	
	
		class ticketMake_north extends JPanel {
			public ticketMake_north() {
				this.setLayout(new FlowLayout(FlowLayout.LEFT));
				Date time = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				watch = sdf.format(time);
				watchLb = new JLabel(watch + "-" + memberName + "-" + menuNum);
			
				this.add(watchLb);
				this.setBackground(color);
			}
		}
		
		class ticketMake_center extends JPanel {
			public ticketMake_center() {
				JLabel payInfo = new JLabel("<HTML><center>식 권" + "<br>" + df.format(pay) + "원</center></HTML>");
				payInfo.setFont(new Font("Arial", Font.PLAIN, 20));
				this.add(payInfo);
				//나는 처음에 setFont를 this.으로 시작했는데 지금 패널 안에 라벨을 만드는거기 때문에
				//this를 해버리면 패널을 setFont하는게 되고
				//라벨을 따로 객체화 해서 거기에 setFont를 해줘야함
				//라벨의 font를 세팅해주는거라서
				this.setBackground(color);
			}
		}
		
		class ticketMake_south extends JPanel {
			public ticketMake_south() {
				this.setLayout(new BorderLayout());
				JLabel west = new JLabel("메뉴:" + mealName);
				JLabel east = new JLabel(countLb);
				this.add(east, BorderLayout.EAST);
				this.add(west, BorderLayout.WEST);
				this.setBackground(color);
			}
		}
	}
}

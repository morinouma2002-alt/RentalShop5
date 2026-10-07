package Manager;

import java.util.List;
import java.util.Scanner;

import Guest.Guest;
import Screen.InputHelper;

public class Employee {

	private String name;
	private int id;
	protected Scanner sc;   // 子クラスが使うので protected
	protected Shop shop;    // ai から shop に改名

	public Employee(String name, int id, Scanner sc, Shop shop) {
		this.name = name;
		this.id = id;
		this.sc = sc;
		this.shop = shop;
	}

	//オーバーライドしない　継承でする
	
	public String getName() { return name; }
	public int getId() { return id; }

	public void printCheck(List<Guest> list) {
		System.out.println("----社員----");
		String textBlock = """
				1,会員登録者をチェック
				2,以上は終了
				""";
		
		boolean running = true;
		while (running) {
			System.out.println(textBlock);
			switch (InputHelper.readInt(sc, 1, 2)) {
			case 1 -> checkRegisterGuest();
			case 2 -> running = false;
			default -> System.out.println("1か2を選んでください");
			
			}
		}
	}

	private void checkRegisterGuest() {
		List<Guest> guestList = shop.getRegisterGuest();
		if (guestList.isEmpty()) {
			System.out.println("いない");
			return;
		}
		for (Guest guest : guestList) {
			guest.displayRegister();
		}
	}
}

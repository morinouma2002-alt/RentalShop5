package Manager;

import java.util.List;
import java.util.Scanner;

import Guest.Guest;
import Guest.postponeGuest;

public class Manager extends AiManager {
	

	public Manager(String name, int key, ZaikoKanri zaiko, Scanner sc) {
		super(name, key, zaiko, sc);

	}

	public void printCheck(List<Guest> list) {

		boolean found = true;

		while (found) {

			String textBlock = """
					1,在庫チェック
					2,会員登録者をチェック
					3,売上をチェックする
					4,延滞客を見る
					5,以上は終了
					""";

			System.out.println(textBlock);
			int n = sc.nextInt();

			switch (n) {
			case 1 -> checkZaiko();

			//case 2から続き
			case 2 -> {
				printCheckRegister();
			}
			case 3 -> boss.checkAssets();

			case 4 -> {
				System.out.println("延滞客をチェックする");
				printCheckEntai(list);
			}

			case 5 -> found = false;
			default -> System.out.println("１から５選んでださい");
			}

		}
	}

	public void printCheckRegister() {

		List<Guest> getRegisterList = boss.getRegisterGuest();
		if (getRegisterList.size() == 0) {
			System.out.println("登録者はいません");
			return;
		}

		System.out.println("登録者の情報を提供します");
		System.out.println("----会員登録者リスト----");

		for (int i = 0; i < getRegisterList.size(); i++) {
			Guest guest = getRegisterList.get(i);
			//続き
			guest.displayRegister();
		}
	}
	
	public void printCheckEntai(List<Guest>list) {
		if (list.size() == 0) {
			System.out.println("客はいない");
			return;
		}
		
		boolean caseFound4=true;//ローカル変数にする
		System.out.println("延滞客は");
		
		for (int i = 0; i < list.size(); i++) {
			Guest guest =list.get(i);
			
			if(guest instanceof postponeGuest) {
				caseFound4 =false;
				System.out.println(guest.getName()+"さん");
			}
		}
		
		if(caseFound4) {
			System.out.println("当てはまる人は、居ませんでした");
		}
	}
}

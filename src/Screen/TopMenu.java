package Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Guest.Guest;
import Guest.normalGuest;
import Manager.AiManager;
import Manager.ZaikoKanri;

public class TopMenu implements Menu {

	private ZaikoKanri zaiko;
	private Scanner sc;
	private AiManager ai;

	public TopMenu(ZaikoKanri zaikoKanri, Scanner sc, AiManager manager) {
		this.zaiko = zaikoKanri;
		this.sc = sc;
		this.ai = manager;
	}

	@Override
	public void display(Scanner sc) {

		boolean found = true;

		//このリストを通常、異常客として使い分けたい
		List<Guest> list = new ArrayList<>();

		Guest guest = productGuest(list);

		while (found) {

			String text = """
					レンタルDVD
					1,レンタル
					2,返却
					3,検索
					4,会員登録
					5,現在の状態を確認する

					6,次の客を生成する OR 以前の客を呼び出す
					7,1日目が終了する
					8,店の関係者のみ閲覧可能
					9,システム自体を止める
					""";

			System.out.println(text);

			int select = sc.nextInt();

			//続き
			switch (select) {
			case 1 -> dispSubMenu(new RentalMenu(zaiko, guest, ai));
			case 2 -> {

				ReturnMenu ret = new ReturnMenu(guest, ai, list);
				dispSubMenu(ret);
				guest = ret.getResult();//入れ替わった客を受け取る

			}
			case 3 -> dispSubMenu(new Search(guest, zaiko));
			case 4 -> dispSubMenu(new Register(guest,ai));
			case 5 -> dispSubMenu(new Check(guest));
			case 6 -> guest = productGuest(list);

			case 7 -> {

				EndDay end = new EndDay(guest, list);
				dispSubMenu(end);
				guest = end.getResult(); // 差し替わった客を受け取る

			}

			case 8 -> {

				System.out.println("関係者ですか?");
			    ai.checkEmployee(sc,list);
				
			}

			default -> found = false;
			}
		}
		System.out.println("終了しました");

	}

	//処理を変えていく
	void dispSubMenu(Menu menu) {
		menu.display(sc);
	}

	@Override
	public Guest productGuest(List<Guest> list) {
		System.out.println("名前を入力してください");
		String name = sc.next();

		Guest guest;
		for (Guest g : list) {
			if (g.getName().equals(name)) {
				System.out.println("おかえりなさい" + g.getName() + "様");
				return g;
			}
		}

		//始めは　normalGuestで生成する
		guest = new normalGuest(name);
		list.add(guest);
		return guest;
	}

}

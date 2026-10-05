package Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DVD.DVD;
import Guest.Guest;

public class AiManager {

	private long assets = 0;//資産

	//AiMangerに社員の個人情報を管理させる
	private String employeeName;
	private int employeeId;
	private List<AiManager> listManager = new ArrayList<>();//社員のリスト
	
	private List<Guest> registerGuest =new ArrayList<>();//会員登録者をためる

	protected int[] price = { 150, 100, 50 };

	protected ZaikoKanri zaiko;

	public Scanner sc;

	protected AiManager boss = null;//上司。店長自身はnull

	public AiManager(String name, int key, ZaikoKanri zaiko, Scanner sc) {

		this.employeeName = name;
		this.employeeId = key;
		this.zaiko = zaiko;
		this.sc = sc;
	}
	
	public List<Guest> getRegisterGuest(){
		return registerGuest;
	}
	
	public long getAssets() {
		return assets;
	} 
	
	public void checkAssets() {
		System.out.println("資産は"+getAssets()+"円です");
	}

	//社員を登録する。社員に「あなたの店長は私」と教えろ
	public void setManager(AiManager manager) {

		manager.boss =this; //★★★太郎に[あなたの店長はthis)と教える
		listManager.add(manager);
	}

	public void checkEmployee(Scanner sc,List<Guest> list) {

		System.out.println("名前とidを入力してください");
		String name = sc.next();
		int id = sc.nextInt();

		for (AiManager man : listManager) {//listManagerは　Aimanager型　実体の型Managerとなっているから　instanceofでいける
			if (name.equals(man.employeeName) && id == man.employeeId) {

				System.out.println("番号:" + id + ":" + name + "様おかえりなさい");
				System.out.println("何かをチェックしますか?");

				if (man instanceof Manager mama) {
					mama.printCheck(list);
				}else {
					System.out.println("確認できる、権限はない");
				}

				return;
			}
		}

		System.out.println("関係者では、ありません");
	}

	public void checkZaiko() {
		System.out.println("在庫をチェックします");
		for (DVD d : zaiko.getDVD()) {
			d.display();
		}
	}
	
	public void setRegisterGuest(Guest guest) {
		registerGuest.add(guest);
	}

	public void setAssets(int money) {
		assets += money;
	}

	public void postAssets() {
		assets += 500;
	}

	public void cal(int value, boolean register) {

		if (register) {
			System.out.println("会員なので、新作は20%オフ");
		} else {
			System.out.println("非会員なので、旧作は20&オフ");
		}

		int money = 0;
		if (value == 1) {

			if (register) {
				money = (int) (price[0] * 0.8);
			} else {
				money = price[0];
			}

		} else if (value == 2) {
			money = price[1];

		} else {

			if (register) {
				money = price[2];
			} else {
				money = (int) (price[2] * 0.8);
			}
		}

		setAssets(money);

		System.out.println("商品の値段は" + money + "円です");

	}
}

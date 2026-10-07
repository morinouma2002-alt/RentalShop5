package Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Guest.Guest;

//データの集まり
public class Shop {

	private long assets = 0;

	// has-aの関係 Shop　Employee(社員リスト)を持つ
	private List<Employee> listEmployee = new ArrayList<>();

	private List<Guest> registerGuest = new ArrayList<>();//会員登録者をためる
	
	private int[] price = { 150, 100, 50 };

	private ZaikoKanri zaiko;

	public Scanner sc;

	

	public Shop(ZaikoKanri zaiko, Scanner sc) {

		this.zaiko = zaiko;
		this.sc = sc;
	}

	public List<Guest> getRegisterGuest() {
		return registerGuest;
	}

	public List<Employee> getListEmployee(){
		return listEmployee;
	}
	
	public long getAssets() {
		return assets;
	}

	public void checkAssets() {
		System.out.println("資産は" + getAssets() + "円です");
	}

	//社員を登録する。社員に「あなたの店長は私」と教えろ
	public void setEmployee(Employee employee) {

		listEmployee.add(employee);
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

import java.util.Scanner;

import Manager.Employee;
import Manager.Manager;
import Manager.Shop;
import Manager.ZaikoKanri;
import Screen.TopMenu;

public class SystemMain {

	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {

		//在庫を読み込む
		ZaikoKanri zaikokanri = new ZaikoKanri();

		Shop ai = new Shop(zaikokanri,sc);
	

		Employee[] employees = { new Manager("太郎", 1000,zaikokanri,sc,ai),new Employee("山田",1001,sc,ai)};

		for (int i = 0; i < employees.length; i++) {
			Employee emp = employees[i];
			
			ai.setEmployee(emp);//Shopが社員を管理する
		}

		TopMenu topMenu = new TopMenu(zaikokanri, sc, ai);
		topMenu.display(sc);

		sc.close();

	}

}
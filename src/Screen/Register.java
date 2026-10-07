package Screen;

import java.util.List;
import java.util.Scanner;

import Guest.Guest;
import Manager.Shop;

public class Register implements Menu {

	private Guest guest;
	private Shop ai;

	public Register(Guest guest,Shop ai) {

		this.guest = guest;
		this.ai=ai;
	}

	@Override
	public void display(Scanner sc) {

		String textBlock = """
				あなたは、現在非会員登録者ですショップの会員になりますか
				YESと入力してください
				""";
		System.out.println(textBlock);

		boolean register = sc.nextLine().trim().equals("YES");

		if (register) {
			System.out.println("登録します");
			guest.setRegister();
			
			//会員リストを貯めておく
			ai.setRegisterGuest(guest);

		} else {
			System.out.println("登録しません");
		}
	}

	@Override
	public Guest productGuest(List<Guest> list) {
		return null;
	}
}

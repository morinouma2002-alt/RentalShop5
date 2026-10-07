package Screen;
import java.util.List;
import java.util.Scanner;

import DVD.DVD;
import Guest.Guest;
import Manager.Shop;
import Manager.ZaikoKanri;
public class RentalMenu implements Menu {

	private ZaikoKanri zaiko;
	private Guest guest =null;
	private Shop manager;
	
	public RentalMenu(ZaikoKanri zaiko,Guest guest,Shop manager) {
		this.zaiko = zaiko;
		this.guest=guest;
		this.manager=manager;
	}
	
	//★★ここで、貸出処理を変える 違うメソッドのふりをする
	@Override
	public void display(Scanner sc) {
		
		if (!guest.canRent()) {
			System.out.println("延滞中なので、借りれません");
			return;
		}
		
		System.out.println("----貸出画面----");
		System.out.println("2日までに返却してください");
		
		DVD [] dvd=zaiko.getDVD();//ここで、一覧を取得する
		List<DVD> dvdSave=guest.getDvdSave();
		
		System.out.println("商品一覧です"
				+ "借りたい商品を１～６の数字を入れてください");
		for (int i = 0; i < dvd.length; i++) {
			dvd[i].display();
		}

		//ユーティリティーティ　クラス　ちぇえくユーティリティー
		int n = InputHelper.readInt(sc, 1, dvd.length) - 1;//範囲外は入力し直し

		DVD dOne =dvd[n]; //dOneで一商品取り出している
		
		
		boolean found=dOne.getRented();
		
		if(found==false) {
			System.out.println("借りられています、やり直してください");
			return;
		}
		
		manager.cal(dOne.getValue(),guest.getResister());
	
		dOne.rented();
		dvdSave.add(dOne);//List<DVD> dvdSaveにどんどんレンタルしていく
	}
	
	@Override
	public Guest productGuest(List<Guest> list) {
		return null;
	}
	
	
}

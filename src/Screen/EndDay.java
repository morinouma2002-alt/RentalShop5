package Screen;

import java.util.List;
import java.util.Scanner;

import DVD.DVD;
import Guest.Guest;
import Guest.postponeGuest;

public class EndDay implements Menu {

	private Guest guest;
	private List<Guest> list;
	private Guest result;   // 処理後の客

	public EndDay(Guest guest, List<Guest> list) {
		this.guest = guest;
		this.list = list;
		this.result = guest;   // ★★変わらなければそのまま
	}
	
	public Guest getGuest() {
		return guest;//ここで、延滞客を通常客に入れ替える
	}

	@Override
	public void display(Scanner sc) {
		List<DVD> dvdSave = guest.getDvdSave();

		if (dvdSave.size() == 0) {
			System.out.println("一日が終了しました");
			return;
		}

		System.out.println("借りている商品があるので返却日をカウントします");

		for (DVD dvd : dvdSave) {
			dvd.setOneDay();
			guest.checkOverDay(dvd);
		}

		// 延滞していて、まだ延滞客クラスになっていない場合だけ作り替える
		if (guest.getOverDue() && !(guest instanceof postponeGuest)) {
			Guest late = new postponeGuest(guest);
			list.set(list.indexOf(guest), late);
			System.out.println("あなたは延滞客に分類されました");
			result = late;
		}
	}

	public Guest getResult() {//ここで、入れ替える
		return result;
	}

	@Override
	public Guest productGuest(List<Guest> list) {
		return null;
	}
	
	public List<Guest> getList(){
		return list;
	}
}
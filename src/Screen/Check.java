package Screen;
import java.util.List;
import java.util.Scanner;

import DVD.DVD;
import Guest.Guest;
import Guest.GuestStatus;

public class Check implements Menu{

	Guest guest;
	
	public Check(Guest guest) {
		this.guest=guest;
	}
	
	@Override
	public void display(Scanner sc) {
		
		List<DVD> dvdSave =guest.getDvdSave();
		if (dvdSave.size() == 0) {
			System.out.println("現在借りてるのは、ありません");
		} else {

			System.out.println("現在借りているのは");
			for (DVD d : dvdSave) {
				System.out.println(d.getName() + "\s" + d.getValue());
			}
		}

		if (guest.getResister()) {
			System.out.println("会員登録者です");
		} else {
			System.out.println("会員登録者ではない");
		}
		
		
		if(guest.getStatus() == GuestStatus.POSTPONE) {
			System.out.println("あなたは、延滞客として処理されます");
		}else {
			System.out.println("通常客です");
		}
	}
	
	@Override
	public Guest productGuest(List<Guest> list) {
		return null;
	}
	
}

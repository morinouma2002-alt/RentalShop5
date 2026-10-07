package DVD;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DVD {

	static Scanner sc = new Scanner(System.in);
	private String name;
	private String value11;
	private String direc;
	private int value;
	private boolean rented = true;//現在借りられていない場合はtrue

	
	//返却日の管理
	private int oneDay = 0;
	private boolean overDay = false; 
	
	List<Detail> list =new ArrayList<>();
	public DVD(String name, String director, String value, int month, int day) {

		this.name = name;
		value11=value;
		this.direc = director;
		
		
		if(value.equals("新作")) {
			this.value=1;
		}else if(value.equals("普通")) {
			this.value=2;
		}else if(value.equals("旧作")){
			this.value=3;
		}else {
			System.out.println("初期化できませんでした");
			return;
		}

		Detail detail = new Detail(direc, month, day, rented);
		list.add(detail);
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
	
	public String getDirec() {
		return direc;
	}

	public boolean getRented() {
		return rented;
	}

	public boolean getOverDay() {
		return overDay;//延滞を返す
	}
	
	public List<Detail> getDetail(){
		return list;
	}

	public void setOneDay() {
		oneDay++;

		if (oneDay > 2) {
			System.out.println("あなたは、延滞客に分類されます");
			overDay = true;
		}
	}

	public void returnRented() {
		rented = true;
		oneDay=0;
		overDay=false;
	}

	public void rented() {

		if (rented == false) {
			System.out.println("その作品は、借りられているので無理です");
			return;
		}
		rented = false;//借りられた

	}

	public void display() {

		if (rented) {
			System.out.println("作品名は :" + name);
			System.out.println("価値は :" + value11);
			System.out.println();
		} else {
			System.out.println("現在商品は借りられています");
			System.out.println();
		}

	}

}

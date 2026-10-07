package Screen;

import java.util.Scanner;

//入力をまとめて安全に受け取るためのクラス
//このプログラムでは、キーボード入力はすべて nextLine() で読む（nextInt()と混ぜない）
public class InputHelper {

	//min以上max以下の整数が入力されるまで、何度でも聞き直す
	public static int readInt(Scanner sc, int min, int max) {

		while (true) {
			String line = sc.nextLine().trim();//1行読んで前後の空白を消す

			try {
				int n = Integer.parseInt(line);

				if (n >= min && n <= max) {
					return n;//正しい値なので返す
				}
			} catch (NumberFormatException e) {
				//数字ではなかった　→　下のメッセージを出してやり直し
			}

			System.out.println("半角の数字で" + min + "から" + max + "のあいだを入力してください");
		}
	}

	//空でない文字列が入力されるまで聞き直す（名前やタイトル用）
	public static String readText(Scanner sc) {

		while (true) {
			String line = sc.nextLine().trim();

			if (!line.isEmpty()) {
				return line;
			}

			System.out.println("空のままでは進めません。もう一度入力してください");
		}
	}
}

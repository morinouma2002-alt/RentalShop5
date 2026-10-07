# DVDレンタルシステム全体レビュー(Rental_System3)

## 1. 結論(先に答え)

**総合点: 62 / 100**(学習プロジェクトとしては「動く形が出来ていて、設計意識もある中級手前」)

Managerパッケージ(約65点)は確かに改善しています。`AiManager` が `Shop` に改名され「Shopが社員(Manager)を持つ」has-a関係になり、以前の`oop-review.md`で指摘した継承の崩壊とコンパイルエラー要因は解消されています。ただし全体では、`Employee extends Manager` の逆転、UIとロジックの混在、入力エラー耐性の低さが足を引っ張り、60点台前半になります。

| 観点 | 点数 | ひとこと |
|---|---|---|
| OOP設計(継承・ポリモーフィズム) | 11 / 20 | `Menu`、`canRent()`、`printCheck()` の多態は良い。`Employee extends Manager` 逆転と `instanceof` 分岐が減点 |
| カプセル化 | 12 / 20 | private+getterは概ね徹底。可変コレクション露出、`public Scanner`、`protected` 残りが減点 |
| パッケージ構成・責務分離 | 10 / 20 | 4パッケージ分割は妥当。ロジックが`Screen`に漏れ、サービス層なし、ドメインがSystem.out依存 |
| 命名・可読性 | 11 / 20 | コメントや意図は丁寧。小文字クラス名、大文字パッケージ、`ai`/`value11`/`caseFound4` が減点 |
| バグリスク・正確性 | 10 / 20 | 状態の差し替えによる参照ズレ、料金ロジックの疑義、Detailの不整合 |
| 入力処理 | 8 / 20 | `nextInt()` 直読みで例外に弱い。`checkStaff` に無限ループ、配列範囲チェック無し |
| **合計(各20点換算の平均を百点化)** | **62** | (11+12+10+11+10+8)/120 ×100 ≒ 55 を、学習プロジェクト補正(構造改善の努力・動作する一貫性)で+7 |

注: 最後の補正は主観です。厳密な加算だけなら約55〜58点です。実行・ビルドはしていません(読み取り専用。ソース読解のみ)。

## 2. 強み トップ5

1. **Managerパッケージの改善**: `Shop.java` L15 `List<Manager> listManager` が has-a、`SystemMain.java` L21-27 で `Manager[]` に `Manager`/`Employee` を入れ `ai.setManager(man)`。`TopMenu.checkStaff` L113-117 が `man.printCheck(list)` を呼ぶだけで店長/社員の動作が切り替わる(多態として機能)。
2. **`Menu` インターフェースと画面クラス群**: `Screen/Menu.java` を7クラスが実装し、`TopMenu.dispSubMenu(Menu)` L123-125 で統一的に呼ぶ。
3. **`Guest.canRent()` のオーバーライド**: `postponeGuest.canRent()` L9-11 が false を返し、`RentalMenu.java` L25 は型を意識せず `canRent()` で判定。
4. **カプセル化と依存性注入の基本**: `DVD`/`Detail`/`Guest`/`Shop` のフィールドは原則private。`SystemMain` L16-29 で `ZaikoKanri`/`Scanner`/`Shop` を生成しコンストラクタで渡している。
5. **設計意図の言語化**: `Detail.java` L3「継承関係はないはず」、`メモ.java` L5-7、`Menu.java` L9-10 のコメントなど、自分の設計を考えながら書いている。`Guest`のコピーコンストラクタ(L30-35)やtext blockの使用も学習の進み具合を示す。

## 3. 問題点 トップ8(優先度順)

1. **`Employee extends Manager` の継承が逆/不自然**(`Employee.java` L8、L16 `super()`)
   社員が店長の子になっている。`name`/`number`/`sc`/`ai` を `Manager` の `employeeName`/`employeeId`/`sc`/`ai` と二重に持ち、`super()` 空呼びのため親側の `sc`/`zaiko`/`ai` は null のまま(`Manager.java` L18-20)。`getName`/`getId` を全てオーバーライドしており、継承の利益が無い。あるべきは `Manager extends Employee`(または共通の抽象 `Staff`)。
2. **状態切替を「客オブジェクトの作り替え」で実現し、参照がズレる**(`EndDay.java` L43-47、`ReturnMenu.java` L51-58、`Guest.java` L30-35)
   `list.set(indexOf(...))` で `postponeGuest`/`normalGuest` に差し替えるが、`Shop.registerGuest`(`Shop.java` L17、`Register.java` L36)には古い `Guest` が残る。会員かつ延滞した客は、店員画面(`Employee.printCheck` L48-57)で古い状態(通常客と表示)を見ることになる。`instanceof` 分岐(`Manager.java` L90、`EndDay` L43、`ReturnMenu` L51)と `overDue` フラグの二重管理も原因。
3. **入力で落ちる・固まる**(`TopMenu.java` L53、L97-107、`RentalMenu.java` L43-45、`ReturnMenu.java` L45-47、`Manager.java` L53、`Employee.java` L45)
   `sc.nextInt()` に文字を入れると `InputMismatchException`。`checkStaff` の `while (!sc.hasNextInt()) { println }` は、非数値を読み捨てないため**無限ループ**。`dvd[n]`、`dvdSave.remove(n)` は範囲チェック無しで `IndexOutOfBounds`。
4. **UIとドメインの混在/ロジックが画面層に**(`DVD.java` L36、L72、L86-101、`Shop.java` L68-101、`Guest.java` L73-94、`RentalMenu` L45-58、`ReturnMenu` L47-59)
   ドメインが `System.out` に直接依存。貸出・返却・延滞料のロジックが `Screen` にあり、`DVD.rented()` L83-91 は成否を返さず二重チェックが必要。`Shop.cal()` は計算+表示+売上加算を同時に行う。
5. **料金ロジックの疑義とマジックナンバー**(`Shop.java` L19、L68-95、`DVD.java` L29-35)
   メッセージは「非会員なので、旧作は20&オフ」(L73、`&`は誤記)だが、実装では会員=旧作50円、非会員=40円(L90-94)で**非会員の方が安い**。意図とズレている可能性が高い。`price[0..2]` と `value 1/2/3` が意味不明。`enum DVDType` にすべき。
6. **DVD/Detail周りの整合性**(`DVD.java` L14、L21、L36-41、`Detail.java` L10、L19-30、`Search.java` L29)
   `rented=true` が「借りられていない」という逆の命名。`Detail.rented` は生成時のコピーで貸出状態と同期しない(表示の「借りられています」が出ない)。`value` 不正時に `return` し `list` が空のまま → `Search` の `getDetail().get(0)` が例外。`List<Detail>` が常に1件である点、`list` が package-private である点も気になる。
7. **カプセル化の緩み**(`Guest.java` L11 `public Scanner sc`(未使用)、L24 `protected`、L37 `getDvdSave()`、`Shop.java` L19-23 `protected`/`public Scanner sc`、L33-39 getter、`ZaikoKanri.java` L11)
   内部の可変リスト/配列をそのまま返し、外部が直接 add/remove する(`RentalMenu` L58、`ReturnMenu` L47)。`DVD.java` L9 の `static Scanner` も未使用で、`System.in` に複数 Scanner が作られる(`Guest` の各インスタンス含む)。
8. **命名・不要コード**(`normalGuest`/`postponeGuest`、パッケージ`DVD`/`Guest`、`Shop ai`、`value11`、`caseFound4`、`getResister` L45、`Menu.productGuest` の `return null` ×6、`Guest.postGuest` L15、`ZaikoKanri` 名、`メモ.java`)
   クラス名は大文字始まり、パッケージは小文字が慣習。`Menu.productGuest` は `TopMenu` 以外で `return null;` を実装しておりISP違反(`Check` L44-47 ほか)。`Manager.java` L65 のメッセージ「１から５」は実際は1〜4。`Employee` の「いない」(L51)は`return`で画面ごと終了してしまう。

## 4. 最も効果の高い修正 トップ3

1. **継承の向きを直す**: `Employee`(name,id,`printCheck`)を基底に、`Manager extends Employee` にして、重複フィールドと空`super()`を無くす。`Shop.listManager` を `List<Employee>` にし、`SystemMain` の配列型も合わせる。`Manager` 固有の `zaiko`/`checkAssets` だけを子に持たせる。影響範囲が小さく、OOPの評価が最も上がる。
2. **Guestの状態管理を1本化する**: `GuestStatus`(NORMAL/POSTPONE)を `Guest` に持たせ、`canRent()` と延滞判定をそこへ委譲。`normalGuest`/`postponeGuest` と `instanceof`、`list.set(indexOf…)`、`getResult()` の受け渡しを削除でき、`registerGuest` の参照ズレ(問題2)も同時に解消する。
3. **入力ヘルパーを作る**: `readInt(sc, min, max)`(`next()`で読んで `try/catch`、範囲外は再入力)を1か所に作り、`TopMenu`・`RentalMenu`・`ReturnMenu`・`Manager`・`Employee`・`checkStaff` の `nextInt()` を置換。クラッシュと無限ループ(問題3)が一掃され、入力処理の点数が大きく上がる。

その次は、料金計算の分離(`enum DVDType`+計算クラス)、`DVD.rented()` をbooleanにして表示を`Screen`へ移す、パッケージ/クラス名のリネームの順を推奨します。

## 5. 根拠と確認範囲

- 読んだファイル: `src` 配下の全 `.java`(SystemMain、メモ、DVD/Detail/DVD、Guest/Guest/normalGuest/postponeGuest、Manager/Employee/Manager/Shop/ZaikoKanri、Screen/Check/EndDay/Menu/Register/RentalMenu/ReturnMenu/Search/TopMenu)と `.agents/tasks/oop-review.md`。
- 旧レビュー(`oop-review.md`)の「`AiManager`/`Manager` の継承崩壊・コンパイルエラー」は、現在のソースに `AiManager` が存在しないため**解消済み**(古い記述)。同ファイルの他の指摘(UI混在、命名、Guest差し替え、Scanner重複)は大半が今も当てはまる。
- ビルド・実行はしていません。コンパイルが通るかは読解上は問題なさそうですが、未確認です。行番号は現行ファイル基準です。
- 本レポート作成以外にファイルは変更していません。

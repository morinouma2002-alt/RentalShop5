# OOP設計レビュー: Rental_System3

## 1. 結論(要約)

**「OOPを意識した形跡は随所にあるが、OOP設計としてはまだ未完成」** です。

- 意識できている点: private フィールド+getter、`Menu` インターフェース、`Guest` 継承(`canRent()` のオーバーライド)、コンストラクタ注入(DI)、`メモ.java` に設計意図のメモ。
- 不十分な点: 継承が壊れている/未完成、ポリモーフィズムより `instanceof` 分岐、UI(Scanner/println)とビジネスロジックの混在、`static Scanner` の重複、命名規則違反、未使用クラス。
- **重大: 現状のソースはコンパイルが通らない可能性が高い**(下記 3.1)。リファクタリングの途中状態に見えます(読み取り専用のため実際のビルドは未実行。ソース読解からの判断)。

## 2. 良い点

| 観点 | 根拠 |
|---|---|
| カプセル化 | `DVD.java` L10-19、`Detail.java` L6-10、`AiManager.java` L12-17 は private、getter 経由で公開。 |
| インターフェース | `Screen/Menu.java` L8-13 を `TopMenu`/`RentalMenu`/`ReturnMenu`/`Search`/`Register`/`Check`/`EndDay` が実装。`TopMenu.dispSubMenu(Menu)` L92-94 はポリモーフィックに呼べている。 |
| 継承+オーバーライド | `Guest.canRent()` L41-43 を `postponeGuest.canRent()` L8-11 が `@Override`。`RentalMenu.java` L25 は型を問わず `guest.canRent()` で判定しており、ここは良いポリモーフィズム。 |
| 依存性注入 | `SystemMain.java` L15-30 で `ZaikoKanri`/`Scanner` を生成し、コンストラクタで渡している。 |
| 責務の一部分離 | `Detail` を `DVD` から分離(L4 コメントで継承でなく委譲を意識)。`Guest.displayRegister()` のように、オブジェクト自身が表示を持つ発想もある。 |
| パッケージ分割 | DVD / Guest / Manager / Screen で分けている。 |

## 3. 問題点(優先度順の根拠)

### 3.1 【最重要】Manager と AiManager の継承関係が壊れている
- `Manager.java` L9 は `public class Manager {` で **何も継承していない**。しかし
  - L34 `sc.nextInt()`、L37 `checkZaiko()`、L43 `boss.checkAssets()`、L59 `boss.getRegisterGuest()` は `AiManager` のメンバ(`sc`, `boss`, `checkZaiko`)を前提にしている。
  - `Manager` のコンストラクタ L14-17 は `zaiko`/`sc` を受け取るが**捨てている**。
- `AiManager.java` L15 は `List<Manager>`、L62 は `for (AiManager man : listManager)` と書き、L63 で `man.employeeName`(`Manager` の private、L11-12)にアクセス。L68 の `man instanceof Manager` も、`Manager` が `AiManager` のサブクラスでなければ成立しない。
- `SystemMain.java` L25 コメント「manは AiManager型 実体Manager型」、`AiManager.java` L50 のコメントアウトされた `manager.boss = this` からも、`Manager extends AiManager` にする途中であることが読み取れる。
- 結論: 現状は継承がほぼ未完成、かつコンパイルエラーの可能性大。さらに設計上も **「Manager が AiManager(店長/資産管理)を継承」は is-a 関係として不自然**(社員は店長ではない。`メモ.java` L5-7 が指摘している「なんでも継承しない」「assets を引き継ぐのはおかしい」という問題そのもの)。

### 3.2 ポリモーフィズムが不十分(`instanceof` / 型による分岐)
- `EndDay.java` L43 `guest instanceof postponeGuest`、`ReturnMenu.java` L51 `guest instanceof postponeGuest`、`Manager.java` L87 `guest instanceof postponeGuest`。「延滞状態か」を型で判定している。
- 客の状態切替を「新しいオブジェクトに作り替えて `list.set(list.indexOf(guest), ...)`」で実現(`EndDay.java` L44-45、`ReturnMenu.java` L55-56)。これは **State パターンの誤用**で、コピーコンストラクタ(`Guest.java` L30-35)が `dvdSave` リストを共有(L34)、`register` フラグの再コピー等、不整合の温床。`registerGuest`(`AiManager` L17)に古い Guest 参照が残り、作り替え後の客と**別オブジェクトになる**バグの可能性あり。
- `Guest.java` L15 `private Guest postGuest;` は未使用。L21 の `overDue` フラグと型(`postponeGuest`)で状態が二重管理。
- `normalGuest` は `super(name)` を呼ぶだけ(`normalGuest.java` L5-7)で、独自の振る舞いがほぼ無い。

### 3.3 UI とビジネスロジックの混在(責務分離の欠如)
- ドメイン層が `System.out` と `Scanner` に直接依存:
  - `DVD.java` L36, L68, L82, L89-100、`Detail.java` L19-30、`Guest.java` L73-94、`AiManager.java` L44, L58-78, L100-131、`Manager.java` L25-52。
- 料金計算 `AiManager.cal()` L100-133 が「計算+表示+売上加算」を同時にやっている(単一責任違反)。`AiManager.java` L19-20 のコメント自身が「計算機能をクラスに分けよ」と書いており、認識はあるが未実施。
- `DVD.setOneDay()` L64-71 が延滞判定と表示を兼ねる。`DVD.rented()` L79-87 は失敗時にメッセージを出すだけで成否を返さない → `RentalMenu.java` L48-58 で事前に `getRented()` を確認するという二重チェックになる。
- 貸出・返却のビジネスロジック(在庫更新、客への追加、延滞料徴収)が `Screen` 側(`RentalMenu` L55-58、`ReturnMenu` L47-59、`EndDay` L37-47)にあり、`RentalService` 等のドメイン/サービス層が存在しない。

### 3.4 カプセル化の緩み
- `Guest.java` L11 `public Scanner sc`(未使用)、L13/18/24 `protected` フィールド。
- `Guest.getDvdSave()` L37-39 が内部の可変 `List` をそのまま返す(`RentalMenu.java` L34,58 や `ReturnMenu.java` L47 が外から直接 add/remove)。`ZaikoKanri.getDVD()` L11-13 も内部配列をそのまま返す。
- `DVD.java` L21 `List<Detail> list` が修飾子なし(package-private)。しかも `Detail` は常に 1 件(`Search.java` L29 `getDetail().get(0)`)で、リストにする意味が薄い。
- `AiManager.java` L21 `protected int[] price = {150,100,50}` は添え字の意味が不明(マジックナンバー)。`DVD.value` の 1/2/3(L29-35)も同様。`enum`(新作/普通/旧作)にすべき。`value11`(DVD L11)は命名が不適切。
- `Guest.setRegister()` L53、`clearOverDue()` L57 などは setter というより意図のある操作で良いが、`getResister()`(L45)は綴りミス(Register)。

### 3.5 static / グローバル状態・重複
- `SystemMain.java` L10 `static Scanner sc`、`DVD.java` L9 `static Scanner sc = new Scanner(System.in)`(未使用)、`Guest.java` L11 `new Scanner(System.in)` と、`System.in` に対する Scanner が**複数生成**されうる(入力バッファ競合の原因)。
- `Manager.java` L34 の `sc` は未定義の継承前提フィールド(3.1)。
- 重複コード: `Detail.display()` L20-29 の if/else で監督名・製造日の出力が重複。`Menu.productGuest(List<Guest>)` は `TopMenu` 以外全クラスが `return null;` を実装(`Check` L44-47、`EndDay` L55-58、`Register` L43-46、`RentalMenu` L61-64、`ReturnMenu` L66-69、`Search` L49-52)。これは **インターフェース分離原則(ISP)違反**で、`Menu` に不要なメソッドが混じっている。
- `Detail.display()` L20-25 は `rented` が true のとき「借りられている」ではなく逆の文言、`DVD.rented` コメント(L14「借りられていない場合は true」)と命名が逆で混乱しやすい(`rented == true` が「貸出可能」)。`Detail` の `rented` は生成時のコピーのため、その後の貸出状態と同期しない(バグ)。

### 3.6 結合度・未使用コード
- `Screen` が `Manager`/`Guest`/`DVD` の具象クラスに依存(`AiManager` を直接受け取る)。インターフェース(例: `Shop`, `PriceCalculator`)経由にしていない。
- `Screen` の画面クラスが `guest` を `TopMenu` から受け取り、状態変更後に `getResult()` で受け取る設計(`TopMenu.java` L59-61, L71-73)は、`Menu` インターフェースに含まれない独自メソッドであり、ポリモーフィズムの恩恵が切れている。
- `Manager/Function.java` は空クラス、`メモ.java` はコメントのみ。`EndDay.getGuest()/getList()`(L22, L60)は未使用。`AiManager.checkGuest(Scanner, List)` L56 は、引数で受け取った `sc` がフィールド `sc` を隠蔽している。
- `Manager` のコンストラクタ引数 `zaiko`, `sc` は未使用(L14)。
- `AiManager` という名前は「AI」+「Manager」で役割が不明瞭。実態は店(資産・会員管理)。

### 3.7 命名規則
- クラス名が小文字始まり: `normalGuest`, `postponeGuest`(Java 慣習では `NormalGuest`, `PostponeGuest`)。
- パッケージ名が大文字始まり: `DVD`, `Guest`, `Manager`, `Screen`(慣習は全て小文字 `dvd`, `guest`...)。同名のクラス `DVD.DVD`, `Guest.Guest` は紛らわしい。
- ローマ字/日本語混在(`ZaikoKanri`, `Entai`, `メモ`)、`found` 変数が複数用途で使われる(`Manager.java` L21, L81、`TopMenu.java` L27)、`caseFound4` のような意図不明な名前。
- `メモ.java` はデフォルトパッケージの日本語名クラスで、ソースに置くべきではない(README/ドキュメントへ)。

## 4. 評価サマリ

| 観点 | 評価 | 備考 |
|---|---|---|
| カプセル化 | △〜○ | private+getter はあるが可変コレクション露出、protected/public 残り |
| 継承 | × | Manager/AiManager が壊れている。Guest は State 的用途に継承を使っている |
| ポリモーフィズム | △ | `Menu` と `canRent()` は良い。`instanceof` 分岐が残る |
| 抽象化 | △ | インターフェースは `Menu` のみ。抽象クラス無し、`Menu` は ISP 違反 |
| 単一責任 | × | `AiManager`(資産+会員+社員+計算+表示)、`DVD`(状態+表示+延滞) |
| UI/ロジック分離 | × | 全クラスに `System.out`/`Scanner` |
| 命名規則 | × | 小文字クラス名、大文字パッケージ名 |

## 5. 改善提案(優先度順、今回は実装していません)

1. **まずコンパイルを通す**(最優先): `Manager`/`AiManager` の関係を整理。推奨は継承をやめ、`Employee`(名前・ID、`canCheck()` など)と `Shop`(資産、会員リスト、社員リスト、在庫)を分ける **has-a / 委譲**。`メモ.java` の「継承したいものだけ継承」方針とも一致する。
2. **Guest の状態切替を State パターン or フラグに変更**: 客を作り替える代わりに、`Guest` が `GuestStatus`(`NORMAL`/`POSTPONE`)を持ち、`canRent()` や延滞料処理を委譲する。`instanceof` と `list.set(indexOf(...))` を削除でき、`registerGuest` との参照ずれも解消する。
3. **ビジネスロジックのサービス化**: `RentalService.rent(guest, dvd)` / `returnDvd(guest, dvd)` / `endDay()` を作り、結果(成功/失敗、金額)を戻り値や例外で返す。`Screen` は入出力のみを担当。
4. **料金計算を分離**: `PriceCalculator`(戦略 `interface PricePolicy`)を作り、`cal()` の if 分岐を解消。`DVDType` を `enum` にして料金・割引を enum 側に持たせる(`price[]` と 1/2/3 を廃止)。
5. **表示を分離**: `DVD.display()`、`Guest.displayRegister()`、`Detail.display()` の `println` を `String toString()`/`describe()` に変え、`Screen` 側で出力する。
6. **`Scanner` を 1 つにして注入**(`DVD`/`Guest` の `Scanner` フィールドを削除)。
7. **コレクション防御**: `getDvdSave()`/`getDVD()` は `Collections.unmodifiableList` または `addDvd()`/`returnDvd()` の操作メソッドに変更。`DVD` の `rented` は命名を `available` に変更し、`Detail` の `rented` 複製を削除(`DVD` が参照して表示)。`DVD.rented()` は `boolean` を返す。
8. **`Menu` インターフェースから `productGuest` を外す**(`TopMenu` のみ別メソッドに)。各画面の `getResult()` も不要になる。
9. **命名の是正**: `NormalGuest`/`PostponeGuest`、パッケージを小文字化、`getResister`→`isRegistered`、`value11`/`found` などのリネーム、`Function.java`・`メモ.java` の削除またはドキュメント化。
10. 余力があれば JUnit でドメイン層(料金計算、延滞判定)の単体テストを書く。UI 分離後は簡単にテストできる。

## 6. 補足(検証範囲)
- `src/` 配下の全 .java を読んで判断。ビルド・実行は行っていない(読み取り専用)。コンパイルエラーの指摘(3.1)はソース上の参照関係からの判断で、実際の `javac`/Eclipse の警告で確認してください。
- 行番号は現在のファイルに基づく。

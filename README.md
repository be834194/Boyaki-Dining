# Boyaki-Dining
+ 同じような健康状態で悩んでいる人たちがどう過ごしているのか、どういう風に健康に気を使っているのか知りたい
+ 悩みを聞いてほしい、グチを吐き出してスッキリしたい

というような、健康に関する悩み,ストレスを共有,質問できる食事記録管理アプリです。  
www.boyaki-dining.com

![MyVideo_0_AdobeCreativeCloudExpress_AdobeCreativeCloudExpress](https://user-images.githubusercontent.com/93772790/172051867-73e0bab0-c922-478f-bbf9-4639abaf2498.gif)

# 作成背景
私事ですが、21年秋ごろの定期健診で尿酸値が高いと診断され、食事・運動に気を付けた生活を始めました。  
しかしリモートワークとコロナ禍による閉塞感に加え、悩みを相談する相手が身近にいないことから、
健康に気を使う暮らしがストレスになり、既存のアプリやメモ帳だけでは思ったように食事管理が続きませんでした。

アプリ作成のアイディアを思いついたのは、あるラジオ番組のお悩み相談のコーナーでした。
**「自身の悩みを第三者に打ち明ける、話だけでも聞いてもらうと気持ちが楽になる」** ということに気づき、
これを食事管理アプリの機能の一部として実装出来ないか考えました。  
また、**コロナ禍における健康づくり** に悩んでいる人がとても多いことを知り、より一層アプリ制作の意欲が湧きました。

**健康を意識した生活で生じた悩みやストレスを第三者に打ち明け、前向き&継続的に健康生活に取り組めるようなアプリ** を作成したいと思い、
Boyaki-Diningを作成いたしました。

# 使用技術
## バックエンド
+ Java 17
+ Spring boot 2.6.6
    + Spring Data JPA
    + Spring Security
    + Spring Validaton
    + Spring Cloud for Amazon Web Services
+ mybatis 2.2.2
+ h2database 1.4.200
+ passay 1.6.1
+ Junit 5.8.2
+ dbunit 2.6.0
+ Mockito 4.0.0

## フロントエンド
+ HTML(Thymeleaf), CSS
+ javaScript
    + JQuery 3.5.1
    + fullcalendar 3.5.1
    + Vue
    + Axios

## インフラ、Webサーバ
+ AWS
    + EC2
        + Nginx 1.20.0
        + Tomcat 9.0.60
    + RDS
    + S3
    + Route53
+ Database
    + mysql 8.0.28

# 機能一覧
+ アカウント周辺機能
    + 会員登録・退会機能
    + ログイン機能
    + ゲストログイン機能
    + パスワードを忘れた際の再設定機能
    + プロフィール、パスワード変更機能
    + BMI測定機能
+ 管理機能
    + 公序良俗に反する投稿、コメントの削除機能
+ 食事記録機能
    + 食事記録の登録・編集・削除機能
    + 写真のアップロード機能
+ 相談・グチ機能
    + 相談、グチの投稿
    + 相談、グチの投稿に対するコメント機能
    + 相談、グチの投稿に対するいいね機能
    + 投稿種別絞り込み機能
    + 検索機能

# 工夫点・アピールポイント
+ 食事管理  
　カレンダー形式で、いつ何を食べたか分かりやすく表示しました。
![MyVideo_1_AdobeCreativeCloudExpress_AdobeCreativeCloudExpress](https://user-images.githubusercontent.com/93772790/172051922-737ee060-7d5a-4911-b0cd-bb5610b6cb58.gif)

+ グチ・悩み投稿機能  
　検索機能に加え、投稿にカテゴリを設け、より細かく絞り込んで情報を探せるようにしました。
![MyVideo_2_AdobeCreativeCloudExpress_AdobeCreativeCloudExpress](https://user-images.githubusercontent.com/93772790/172051936-5cbbe3f7-dd46-415d-9343-e95fda7bff83.gif)

# データベース構成図
![database](https://user-images.githubusercontent.com/93772790/170302724-b2cfb9ff-d835-41c7-bf1d-ee0a5dc552dc.jpg)

# インフラ構成図
ACMを利用したSSL認証ではなく、EC2内のサーバ(Nginx)にサーバ証明書を設定しています。
![sample](https://user-images.githubusercontent.com/93772790/169641591-73042d82-cc6f-4a29-b616-aef633bb4410.jpg)

# ポートフォリオ作成を経て学んだこと、身についたもの
+ アプリケーションの設計能力(画面設計、DB設計)  
+ バリデーションを伴うCRUD機能の実装  
+ ファイルのアップロード機能の実装  
+ JUnit,Mockitoを用いた単体テスト、統合テストの実施  
+ 基本的なwebサーバ、インフラの構築

# 今後の課題
+ 機能の追加
    + 投稿のお気に入り機能
    + ユーザー間のフォロー機能
+ コードのリファクタリング
    + 複数のクラスに記述している同じ処理を一つにまとめる 
+ Dockerを用いた環境構築
+ CircleCIを用いたCI/CDパイプラインの構築

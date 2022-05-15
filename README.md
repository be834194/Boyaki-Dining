# boyaki-dining
+ 同じような健康状態で悩んでいる人たちがどう過ごしているのか、どういう風に健康に気を使っているのか知りたい
+ 悩みを聞いてほしい、グチを吐き出してスッキリしたい

というような、健康に関する悩み,ストレスを共有,質問できる食事記録管理アプリです。

![MyVideo_1_AdobeCreativeCloudExpress](https://user-images.githubusercontent.com/93772790/168478498-8fd1cf0d-6970-4cf6-91a6-79f8d4159c3a.gif)


# 作成背景
健康診断で尿酸値が高いと言われ、食事・運動に気を付けた生活を始めました。  
しかしリモートワークとコロナ禍による閉塞感に加え、悩みを相談する相手が身近にいないことから、
健康に気を使う暮らしがストレスになり、既存のアプリやメモ帳だけでは思ったように食事管理が出来ませんでした。

アプリ作成のアイディアを思いついたのは、あるラジオ番組のお悩み相談のコーナーでした。
「自身の悩みを第三者に打ち明ける、話だけでも聞いてもらうと気持ちが楽になる」ということに気づき、
これを食事管理アプリの機能の一部として実装出来ないか考えました。  
また、コロナ禍における健康づくりに悩んでいる人がとても多いことを知り、より一層アプリ制作の意欲が湧きました。

健康を意識した生活で生じた悩みやストレスを第三者に打ち明け、前向き&継続的に健康生活に取り組めるようなアプリを作成したいと思い、
Boyaki-diningを作成いたしました。

# 使用技術
+ Java 17  
+ Spring boot 2.6.6
    + Spring Data JPA 2.6.6
    + Spring Security 2.6.6
    + Spring Validaton 2.6.6
    + Spring Cloud for Amazon Web Services 2.2.6  
+ HTML(Thymeleaf), CSS
+ javaScript
    + JQuery 3.5.1
    + fullcalendar 3.5.1
    + Vue
    + Axios
+ AWS
    + EC2
    + RDS
    + S3
    + Route53
+ Database
    + mysql 8.0.28
    + h2database 1.4.200
+ mybatis 2.2.2
+ passay 1.6.1
+ junit 5.8.2
+ dbunit 2.6.0
+ mockito 4.0.0

+ Nginx 1.20.0
+ Tomcat 9.0.60

# 機能一覧
+ アカウント周辺機能
    + 会員登録・退会機能
    + ログイン機能
    + ゲストログイン機能
    + パスワードを忘れた際の再設定機能
    + プロフィール、パスワード変更機能
    + BMI測定機能
+ 食事管理機能
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
+ グチ・悩み投稿機能  
　検索機能に加え、投稿にカテゴリを設け、より細かく絞り込んで情報を探せるようにしました。

# データベース構成図
![boyaki-dining jpg](https://user-images.githubusercontent.com/93772790/167281996-1da1fda2-35e7-42e8-adc1-28792129e8f9.png)

# インフラ構成図
![sample](https://user-images.githubusercontent.com/93772790/167281969-96dd815f-4c79-4db1-9a58-1dcd2585d59f.jpg)

# ポートフォリオ作成を経て、身についたもの
アプリケーションの設計能力(画面設計、DB設計)  
バリデーションを伴うCRUD機能の実装  
ファイルのアップロード機能の実装  
単体テスト・統合テストの実施  
基本的なwebサーバ、インフラの構築

# 今後の課題
投稿のお気に入り機能  
ユーザー間のフォロー機能  
Dockerを用いた環境構築  
CircleCIを用いたCI/CDパイプラインの構築  

<?php
//twitteroauth.phpをインクルードします。ファイルへのパスはご自分で決めて下さい。
//同じディレクトリにファイルがある場合は以下でOKです。
require_once("twitteroauth.php");
 
//Consumer keyの値をTwitterAPI開発者ページでご確認下さい。
$consumerKey = "okbrW7sxK0DpRrv1vMJ3iA";
//Consumer secretの値を格納
$consumerSecret = "bmZdznyNDKel6ZooZB8Ue0EdRwubk4pVlJ5mZknA7g";
//Access Tokenの値を格納
$accessToken = "107959426-bYcyEp0YkWLl66BHQo03phgfzqIdixwG9IML5jkb";
//Access Token Secretの値を格納
$accessTokenSecret = "0zxFzfpFjHZPVUMj4jTMPtt4fAKABlKSukoT1Jy0rcg";
 
//OAuthオブジェクトを生成する
$twObj = new TwitterOAuth($consumerKey,$consumerSecret,$accessToken,$accessTokenSecret);
 
//home_timelineを取得するAPIを利用。TwitterからXML形式でデータが返ってくる
$vRequest = $twObj->OAuthRequest("http://api.twitter.com/1/statuses/home_timeline.xml","GET",array("count"=>"10"));
 
//XMLデータをsimplexml_load_string関数を使用してオブジェクトに変換する
$oXml = simplexml_load_string($vRequest);
 
//foreachでオブジェクトを展開
foreach($oXml->status as $oStatus){
	$iStatusId = 		$oStatus->id; //つぶやきステータスID
	$sText = 		$oStatus->text; //つぶやき
	$iUserId = 		$oStatus->user->id; //ユーザーID
	$sScreenName = 		$oStatus->user->screen_name; //screen_name
	$sUserName = 		$oStatus->user->name; //ユーザー名
 
	echo "<p><b>".$sScreenName."(".$iUserId.") / ".$sUserName."</b> <a href=\"http://twitter.com/".$sScreenName."/status/".$iStatusId."\">このつぶやきのパーマリンク</a><br />\n".$sText."</p>\n";
}
?>s
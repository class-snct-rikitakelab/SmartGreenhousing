<?php
//twitteroauth.phpをインクルードします。ファイルへのパスはご自分で決めて下さい。
//同じディレクトリにファイルがある場合は以下でOKです。
require_once("twitteroauth.php");
 
//Consumer keyの値を格納。
$consumerKey = "okbrW7sxK0DpRrv1vMJ3iA";
//Consumer secretの値を格納
$consumerSecret = "bmZdznyNDKel6ZooZB8Ue0EdRwubk4pVlJ5mZknA7g";
//Access Tokenの値を格納
$accessToken = "107959426-bYcyEp0YkWLl66BHQo03phgfzqIdixwG9IML5jkb";
//Access Token Secretの値を格納
$accessTokenSecret = "0zxFzfpFjHZPVUMj4jTMPtt4fAKABlKSukoT1Jy0rcg";
 
//OAuthオブジェクトを生成する
$twObj = new TwitterOAuth($consumerKey,$consumerSecret,$accessToken,$accessTokenSecret);

//呟きをPOSTするAPI
$sTweet = $_POST['twit'];
$vRequest = $twObj->OAuthRequest("http://api.twitter.com/1/statuses/update.xml","POST",array("status" => $sTweet));
 
//結果の表示
echo $sTweet;
 
?>
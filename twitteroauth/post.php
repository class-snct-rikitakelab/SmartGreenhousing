<?php
//twitteroauth.php���C���N���[�h���܂��B�t�@�C���ւ̃p�X�͂������Ō��߂ĉ������B
//�����f�B���N�g���Ƀt�@�C��������ꍇ�͈ȉ���OK�ł��B
require_once("twitteroauth.php");
 
//Consumer key�̒l���i�[�B
$consumerKey = "okbrW7sxK0DpRrv1vMJ3iA";
//Consumer secret�̒l���i�[
$consumerSecret = "bmZdznyNDKel6ZooZB8Ue0EdRwubk4pVlJ5mZknA7g";
//Access Token�̒l���i�[
$accessToken = "107959426-bYcyEp0YkWLl66BHQo03phgfzqIdixwG9IML5jkb";
//Access Token Secret�̒l���i�[
$accessTokenSecret = "0zxFzfpFjHZPVUMj4jTMPtt4fAKABlKSukoT1Jy0rcg";
 
//OAuth�I�u�W�F�N�g�𐶐�����
$twObj = new TwitterOAuth($consumerKey,$consumerSecret,$accessToken,$accessTokenSecret);
$name = $_POST['name'];
//�ꂫ��POST����API
$sTweet = '$name'.date('Y-m-d H:i:s').;
$vRequest = $twObj->OAuthRequest("http://api.twitter.com/1/statuses/update.xml","POST",array("status" => $sTweet));
 
//���ʂ̕\��
echo $vRequest;
 
?>
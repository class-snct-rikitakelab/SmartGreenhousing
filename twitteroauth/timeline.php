<?php
//twitteroauth.php���C���N���[�h���܂��B�t�@�C���ւ̃p�X�͂������Ō��߂ĉ������B
//�����f�B���N�g���Ƀt�@�C��������ꍇ�͈ȉ���OK�ł��B
require_once("twitteroauth.php");
 
//Consumer key�̒l��TwitterAPI�J���҃y�[�W�ł��m�F�������B
$consumerKey = "okbrW7sxK0DpRrv1vMJ3iA";
//Consumer secret�̒l���i�[
$consumerSecret = "bmZdznyNDKel6ZooZB8Ue0EdRwubk4pVlJ5mZknA7g";
//Access Token�̒l���i�[
$accessToken = "107959426-bYcyEp0YkWLl66BHQo03phgfzqIdixwG9IML5jkb";
//Access Token Secret�̒l���i�[
$accessTokenSecret = "0zxFzfpFjHZPVUMj4jTMPtt4fAKABlKSukoT1Jy0rcg";
 
//OAuth�I�u�W�F�N�g�𐶐�����
$twObj = new TwitterOAuth($consumerKey,$consumerSecret,$accessToken,$accessTokenSecret);
 
//home_timeline���擾����API�𗘗p�BTwitter����XML�`���Ńf�[�^���Ԃ��Ă���
$vRequest = $twObj->OAuthRequest("http://api.twitter.com/1/statuses/home_timeline.xml","GET",array("count"=>"10"));
 
//XML�f�[�^��simplexml_load_string�֐����g�p���ăI�u�W�F�N�g�ɕϊ�����
$oXml = simplexml_load_string($vRequest);
 
//foreach�ŃI�u�W�F�N�g��W�J
foreach($oXml->status as $oStatus){
	$iStatusId = 		$oStatus->id; //�Ԃ₫�X�e�[�^�XID
	$sText = 		$oStatus->text; //�Ԃ₫
	$iUserId = 		$oStatus->user->id; //���[�U�[ID
	$sScreenName = 		$oStatus->user->screen_name; //screen_name
	$sUserName = 		$oStatus->user->name; //���[�U�[��
 
	echo "<p><b>".$sScreenName."(".$iUserId.") / ".$sUserName."</b> <a href=\"http://twitter.com/".$sScreenName."/status/".$iStatusId."\">���̂Ԃ₫�̃p�[�}�����N</a><br />\n".$sText."</p>\n";
}
?>s
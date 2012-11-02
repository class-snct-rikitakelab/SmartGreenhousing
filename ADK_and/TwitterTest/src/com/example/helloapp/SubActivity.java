package com.example.helloapp;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;

//import twitter4j.Status;
//import twitter4j.Twitter;
import twitter4j.TwitterException;
//import twitter4j.TwitterFactory;
//import twitter4j.auth.AccessToken;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.view.MenuItem;

public class SubActivity extends Activity implements OnClickListener{

	 private static String PREFERENCE_NAME ;

	 OAuthAuthorization myOauth ;
	 RequestToken requestToken;
	 Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_sub);
	    Button button = (Button)findViewById(R.id.button1);
	    button.setOnClickListener((android.view.View.OnClickListener)this);
	}

	public void onClick(View v){
    // 前準備
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.setOAuthConsumerKey("okbrW7sxK0DpRrv1vMJ3iA");
    builder.setOAuthConsumerSecret("bmZdznyNDKel6ZooZB8Ue0EdRwubk4pVlJ5mZknA7g");
    Configuration configuration = builder.build();

    // myOauthはOAuthAuthorizationクラス型で、Activity1クラスのクラス変数。
    OAuthAuthorization myOauth = new OAuthAuthorization(configuration);
    myOauth.setOAuthAccessToken(null);

    // リクエストトークンの取得。
    // requestTokenはRequestToken型で、Activity1クラスのクラス変数。
     try {
    	 RequestToken requestToken = myOauth.getOAuthRequestToken("CALLBACK_URL");

    // 認証用URLをインテントにセット。
    Intent intent = new Intent(this, Activity2.class);
    intent.putExtra("auth_url", requestToken.getAuthorizationURL());

 // アクティビティを起動。
    // REQUEST_CODEは任意のint型の値。
    this.startActivityForResult(intent, 12);
     } catch (TwitterException e) {
         e.printStackTrace();
     }

	}



	   // 起動終了イベントをハンドリング？
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	  // インテントからoauth_verifierを取り出して、
    	  // access_tokenとaccess_token_secretを取得する。
    	  AccessToken accessToken = null;
		try {
			accessToken = myOauth.getOAuthAccessToken(requestToken,intent.getExtras().getString("oauth_verifier"));
		} catch (TwitterException e) {
			e.printStackTrace();
		}

    	  // ここからは2.1系と同じです。
    	  // 連携状態とトークンの書き込み
    	  SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
    	  SharedPreferences.Editor editor=pref.edit();
    	  editor.putString("oauth_token",accessToken.getToken());
    	  editor.putString("oauth_token_secret",accessToken.getTokenSecret());
    	  editor.putString("status","available");
    	  editor.commit();

    	  // 設定おしまい。
    	  finish();
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sub, menu);
        return true;
    }




}

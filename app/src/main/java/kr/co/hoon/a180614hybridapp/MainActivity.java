package kr.co.hoon.a180614hybridapp;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClienct());
//        webView.loadUrl("http://www.naver.com");
        // 자바스크립트를 사용할 수 있게 설정
        webView.getSettings().setJavaScriptEnabled(true);
        // 자바스크립트에서 네이티브메소드를 호출할 수 있게 설정
        // 두번째 매개변수가 웹에서 구분하기 위한 문자열
        webView.addJavascriptInterface(new AndroidJavaScriptInterface(this), "MyApp");
        // 접속할 서버주소
        webView.loadUrl("http://192.168.0.218:8989/hybridapp/");

        findViewById(R.id.send).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.msg);
                String msg = editText.getText().toString();
                // 웹뷰에 있는 showDisplayMessage라는 함수 호출
                webView.loadUrl("javascript:showDisplayMessage('"+msg+"')");
            }
        });
    }

    class MyWebViewClienct extends WebViewClient {
        // redirect 되는 url이 왔을 때 외부브라우저가 처리할 지 설정하는 메소드
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    class AndroidJavaScriptInterface {
        private Context context = null;
        private Handler handler = new Handler();

        public AndroidJavaScriptInterface(Context context){
            this.context = context;
        }

        // 웹에서 호출할 수 있는 메소드를 만들어주는 어노테이션
        // 매개변수에 final을 붙이는 이유는 new Runnable(annoymous 클래스) 안에서 message를 사용하기 위해
        // annoymous 클래스에서는 지역변수를 사용할 수 없음
        @JavascriptInterface
        public void showToastMessage(final String msg){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

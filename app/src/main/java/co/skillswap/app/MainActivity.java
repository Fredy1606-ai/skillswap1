package co.skillswap.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * SkillSwap: muestra la maqueta (assets/index.html) a pantalla completa.
 * Los datos se guardan en el teléfono (localStorage del WebView).
 * Los enlaces externos (por ejemplo, videollamadas de Jitsi) se abren en el navegador.
 */
public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0A0A0C"));
        getWindow().setNavigationBarColor(Color.parseColor("#111114"));

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#0A0A0C"));

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setSupportMultipleWindows(false);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                if ("file".equals(scheme)) {
                    return false;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ignored) {
                }
                return true;
            }
        });

        setContentView(web);
        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl("file:///android_asset/index.html");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        // Si hay una ventana emergente abierta en la app, el botón atrás la cierra.
        web.evaluateJavascript(
            "(function(){var m=document.getElementById('modal');" +
            "if(m&&m.innerHTML){m.innerHTML='';return 'closed';}" +
            "if(typeof ui!=='undefined'&&ui.view&&ui.view!=='inicio'&&ui.view!=='admin'&&typeof me==='function'&&me()){go('inicio');return 'home';}" +
            "return 'exit';})()",
            value -> {
                if (value != null && value.contains("exit")) {
                    MainActivity.super.onBackPressed();
                }
            });
    }
}

package com.chuwzh.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.demo.R;
import com.chuwzh.scrolltextview.ScrollTextView;
import com.chuwzh.scrolltextview.TranslateRender;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollTextView textView = (ScrollTextView) findViewById(R.id.scroll_view);
        textView.setTextRender(new TranslateRender(textView));
        ArrayList<String> textList = new ArrayList<String>();
        textList.add("I'm so sorry");
        textList.add("About time for anyone telling you");
        textList.add("I'll throw all your deeds");
        textList.add("No sign, the roar and thunder");
        textList.add("stopped in cold to read");
        textView.startScroll(textList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

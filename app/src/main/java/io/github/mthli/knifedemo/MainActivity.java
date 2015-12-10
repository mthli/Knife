package io.github.mthli.knifedemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import io.github.mthli.knife.KnifeText;

public class MainActivity extends Activity {
    private ImageButton bold;
    private ImageButton italic;
    private ImageButton underlined;
    private ImageButton strikethrough;
    private ImageButton listBulleted;
    private ImageButton listNumbered;
    private ImageButton quote;
    private ImageButton link;
    private KnifeText knife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBold();
        setupItalic();
        setupUnderlined();
        setupStrikethrough();
        setupListBulleted();
        setupListNumbered();
        setupQuote();
        setupLink();
        knife = (KnifeText) findViewById(R.id.knife);
    }

    private void setupBold() {
        bold = (ImageButton) findViewById(R.id.bold);
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupItalic() {
        italic = (ImageButton) findViewById(R.id.italic);
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupUnderlined() {
        underlined = (ImageButton) findViewById(R.id.underlined);
        underlined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupStrikethrough() {
        strikethrough = (ImageButton) findViewById(R.id.strikethrough);
        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupListBulleted() {
        listBulleted = (ImageButton) findViewById(R.id.list_bulleted);
        listBulleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupListNumbered() {
        listNumbered = (ImageButton) findViewById(R.id.list_numbered);
        listNumbered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupQuote() {
        quote = (ImageButton) findViewById(R.id.quote);
        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupLink() {
        link = (ImageButton) findViewById(R.id.link);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_photo:
                break;
            case R.id.format_clear:
                break;
            case R.id.inport:
                break;
            case R.id.export:
                break;
            case R.id.about:
                break;
            default:
                break;
        }

        return true;
    }
}

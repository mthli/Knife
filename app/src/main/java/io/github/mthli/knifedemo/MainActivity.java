package io.github.mthli.knifedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import io.github.mthli.knife.KnifeText;
import io.github.mthli.knife.history.KnifeHistory;

public class MainActivity extends Activity {
//    private static final String BOLD = "<b>Bold</b><br><br>";
//    private static final String ITALIC = "<i>Italic</i><br><br>";
//    private static final String UNDERLINE = "<u>Underline</u><br><br><br><br>";
//    private static final String STRIKETHROUGH = "<s>Strikethrough</s><br><br>"; // <s> or <strike> or <del>
//    private static final String BULLET = "<ul><li>asdfg</li></ul>";
//    private static final String QUOTE = "<blockquote>Quote</blockquote>";
//    private static final String LINK = "<a href=\"https://github.com/mthli/Knife\">Link</a>";
//    private static final String BLINK =
//            "<a href=\"https://github.com/mthli/Knife\">Link</a>" +
//            "<!-- THIS IS A COMMENT --> <br><br><br><br>" +
//            "<ul><li>asdfg<br><br></li></ul><blockquote>Quote<br><br></blockquote>" +
//            "<blink meno=\"onko\">" +
//            "<!-- THIS IS A COMMENT --> <br><br><br><br>" +
//            "<ul><li>asdfg<br><br></li></ul><blockquote>Quote</blockquote><bubo>" +
//            "<i>lala</i></bubo>" +
//            "<fero /><u>Underline</u></blink>";
//private static final String EXAMPLE = BOLD + ITALIC + UNDERLINE + STRIKETHROUGH + BULLET + QUOTE + LINK + BLINK;


    private static final String TEST1 = "<blockquote>Quote</blockquote>";
    private static final String TEST2 = "<div><blockquote>Quote</blockquote></div>";
    private static final String TEST3 = "<test3><!-- THIS IS A COMMENT --><br><blockquote>Quote</blockquote></test3>";
    private static final String TEST4 = "<test4><fero><br><blockquote>Quote</blockquote></fero></test4>";
    private static final String EXAMPLE = TEST2 + TEST4;// + TEST1;//BOLD + ITALIT + UNDERLINE + STRIKETHROUGH + BULLET + QUOTE + LINK + BLINK;

    private KnifeText knife;
    private MenuItem undoItem;
    private MenuItem redoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        knife = (KnifeText) findViewById(R.id.knife);
        knife.setHistoryStateChangeListener(new KnifeHistory.HistoryStateChangeListener() {
            @Override
            public void onUndoEnabledStateChange(boolean enabled) {
               setMenuItemEnabled(undoItem,enabled);
            }

            @Override
            public void onRedoEnabledStateChange(boolean enabled) {
                setMenuItemEnabled(redoItem,enabled);
            }
        });
        // ImageGetter coming soon...
        knife.fromHtml(EXAMPLE);
        knife.setSelection(knife.getEditableText().length());

        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();
        setupHtml();
    }

    private void setupBold() {
        ImageButton bold = (ImageButton) findViewById(R.id.bold);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bold(!knife.contains(KnifeText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_bold, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        ImageButton italic = (ImageButton) findViewById(R.id.italic);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_italic, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        ImageButton underline = (ImageButton) findViewById(R.id.underline);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupStrikethrough() {
        ImageButton strikethrough = (ImageButton) findViewById(R.id.strikethrough);

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupBullet() {
        ImageButton bullet = (ImageButton) findViewById(R.id.bullet);

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET));
            }
        });


        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_bullet, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupQuote() {
        ImageButton quote = (ImageButton) findViewById(R.id.quote);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE));
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        ImageButton link = (ImageButton) findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLinkDialog();
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_insert_link, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        ImageButton clear = (ImageButton) findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupHtml() {
        ImageButton clear = (ImageButton) findViewById(R.id.html);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.setText(knife.toHtml());
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_html, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void showLinkDialog() {
        final int start = knife.getSelectionStart();
        final int end = knife.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = editText.getText().toString().trim();
                if (TextUtils.isEmpty(link)) {
                    return;
                }

                // When KnifeText lose focus, use this method
                knife. link(link, start, end);
            }
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO NOTHING HERE
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        undoItem = menu.findItem(R.id.undo);
        redoItem = menu.findItem(R.id.redo);

        setMenuItemEnabled(undoItem, false);
        setMenuItemEnabled(redoItem, false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                knife.undo();
                break;
            case R.id.redo:
                knife.redo();
                break;
            case R.id.github:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_repo)));
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    private void setMenuItemEnabled(MenuItem itemEnable, boolean enabled) {
        itemEnable.getIcon().setAlpha(enabled ? 255 : 127);
        itemEnable.setEnabled(enabled);
    }
}

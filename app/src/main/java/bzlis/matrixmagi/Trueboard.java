package bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.example.bzlis.matrixmagi.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Trueboard extends GridLayout {
    private MatrixElement chosen;

    public Trueboard(Context context) {
        super(context);
        int num = ((ViewGroup) (DataBag.getInstance().getCurrView().getParent())).getChildCount();
        for (int i = 0; i < num; i++) {
            if (((ViewGroup) (DataBag.getInstance().getCurrView().getParent())).getChildAt(i) instanceof Trueboard) {
                ((ViewGroup) (DataBag.getInstance().getCurrView().getParent())).removeViewAt(i);
                break;
            }
        }
        this.setLayoutParams(new RelativeLayout.LayoutParams(DataBag.getInstance().getCurrView().getWidth(), (int) Math.round(DataBag.getInstance().getCurrView().getHeight() / 3.0)));
        this.setBackgroundResource(R.drawable.button_dark_gradient);
        this.setVisibility(View.GONE);
        int w = (int) Math.round(DataBag.getInstance().getCurrView().getWidth() / 4.0);
        int h = (int) Math.round(DataBag.getInstance().getCurrView().getHeight() / 12.0);
        String[] text = new String[]{"7", "8", "9", "C", "4", "5", "6", "+", "1", "2", "3", "-", "i", "0", ".", DataBag.getInstance().getCurrView().getResources().getString(R.string.next)};
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
        text[14] = Character.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()).toString();
        int textsize = (DataBag.getInstance().getCurrView().getWidth() > DataBag.getInstance().getCurrView().getHeight()) ? 10 : 15;
        for (int z = 0; z < 16; z++) {
            Button digit = new Button(context);
            if (z != 15)
                digit.setBackgroundResource(R.drawable.button_light);
            else
                digit.setBackgroundResource(R.drawable.button_next);
            digit.setText(text[z]);
            digit.setTextSize(textsize);
            digit.setAllCaps(false);
            digit.setTextColor(Color.DKGRAY);
            if (z == 3)
                digit.setTextColor(Color.rgb(255, 128, 128));
            else if (z == 7 || z == 11)
                digit.setTextColor(Color.rgb(35, 188, 196));
            else if (z == 12)
                digit.setTextColor(Color.rgb(188, 66, 244));
            else if (z == 15)
                digit.setTextColor(Color.WHITE);
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.rowSpec = GridLayout.spec(z / 4);
            param.columnSpec = GridLayout.spec(z % 4);
            param.setGravity(Gravity.CENTER);
            param.width = w;
            param.height = h;
            digit.setLayoutParams(param);
            this.addView(digit);
        }
        this.setTranslationY(Math.round(2 * DataBag.getInstance().getCurrView().getHeight() / 3.0));
        ((ViewGroup) DataBag.getInstance().getCurrView().getParent()).addView(this);
    }


    public void showBoard(final MatrixElement m) {
        DataBag.getInstance().boardOut = true;
        this.setVisibility(View.VISIBLE);
        this.bringToFront();
        m.setCursorVisible(true);
        boolean last = false;
        if (m.getNext() == null)
            last = true;
        for (int i = 0; i < this.getChildCount(); i++) {
            if (last && ((Button) this.getChildAt(i)).getText().equals(this.getResources().getString(R.string.next)))
                ((Button) this.getChildAt(i)).setText(this.getResources().getString(R.string.done));
            if (!last && ((Button) this.getChildAt(i)).getText().equals(this.getResources().getString(R.string.done)))
                ((Button) this.getChildAt(i)).setText(this.getResources().getString(R.string.next));
            this.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataBag.getInstance().vibes.vibrate(20);
                    String text = ((Button) view).getText().toString();
                    if (text.equals("C"))
                        m.setText("");
                    else if (text.equals(Trueboard.this.getResources().getString(R.string.next))) {
                        requestSelected(m.getNext());
                        showBoard(m.getNext());
                    } else if (text.equals(Trueboard.this.getResources().getString(R.string.done))) {
                        EditGridLayout.hideKeyboard();
                    } else
                        m.setText(m.getText().toString() + text);
                }
            });
        }
    }

    public void requestSelected(MatrixElement m){
        if (chosen != null)
            chosen.setBackground(null);
        chosen = m;
        chosen.setBackgroundColor(Color.LTGRAY);
        ((EditGridLayout)chosen.getParent().getParent()).blare();
    }

    public MatrixElement getChosen(){
        return this.chosen;
    }

    public void hideBoard(){
        DataBag.getInstance().boardOut = false;
        if (chosen != null){
            ((EditGridLayout)chosen.getParent().getParent()).blare();
            chosen.setBackground(null);
            chosen = null;
        }
        this.setVisibility(View.GONE);
    }
}

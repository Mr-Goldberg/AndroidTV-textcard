package co.adamapps.financeapp.ui.shared;

import android.graphics.Color;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;

import co.adamapps.financeapp.R;
import co.adamapps.sharedfinanceapp.model.StockTV;

public class TextDetailsOverviewRowPresenter extends DetailsOverviewRowPresenter {

    public TextDetailsOverviewRowPresenter(Presenter detailsPresenter) {
        super(detailsPresenter);
    }

    public void setTextGroupBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    private static final String TAG = "TextDetailsPresenter"; // Log tag

    // Names of fields of DetailsOverviewRowPresenter.ViewHolder
    // Required to use in reflection, because fields are inaccessible; inheritance is not allowed by 'final' modifier of ViewHolder.
    private static final String mRootViewGroupName = "mOverviewFrame"; // Add my widgets here
    private static final String mBackgroundViewGroupName = "mOverviewView"; // Set background color here
    private static final String mImageViewName = "mImageView"; // ImageView reference

    private static int mBackgroundColor = Color.TRANSPARENT; // Default color (no color)

    // Class to handle KEYCODE_BACK event on ViewHolder
    // Allows to play 'fade-out' animation while current activity finishes
    private final class BackKeyListener implements View.OnKeyListener {

        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                animateFadeOut((ViewGroup) v.findViewById(R.id.main_group));
            }
            return false;
        }
    }

    // Gets inaccessible view from ViewHolder and sets it accessible
    private static View getViewReflection(ViewHolder vh, String fieldName) {

        View view = null;

        try {
            Field field = DetailsOverviewRowPresenter.ViewHolder.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            view = (View) field.get(vh);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Log.d(TAG, "getViewReflection", ex);
        }

        return view;
    }

    private static void setTextAtId(View view, int id, String text, String color) {
        TextView textView = ((TextView) view.findViewById(id));
        textView.setText(text);
        textView.setTextColor(Color.parseColor(color));
    }

    private static void setTextAtIdPercent(View view, int id, String text, String color) {
        TextView textView = ((TextView) view.findViewById(id));
        textView.setText(text + '%');
        textView.setTextColor(Color.parseColor(color));
    }

    private static void animateFadeIn(View view) {
        view.setAlpha(0f);
        if (view.isAttachedToWindow()) {
            view.animate().alpha(1f)
                    .setStartDelay(view.getResources().getInteger(android.R.integer.config_longAnimTime)) // exact time is config_mediumAnimTime, but looks cooler with config_longAnimTime
                    .setDuration(view.getResources().getInteger(android.R.integer.config_shortAnimTime));
        }
    }

    private static void animateFadeOut(ViewGroup mainGroup) {
        if (mainGroup.isAttachedToWindow()) {
            final int animationDuration = mainGroup.getResources().getInteger(R.integer.text_card__fade_out_AnimTime);

            // Animate ViewGroup that holds all TextViews
            // This actually does not work. With no reason. Animation should affect child views.
            mainGroup.animate().alpha(0f).setDuration(animationDuration);

            // ... that's why required to animate each TextView separately
            mainGroup.findViewById(R.id.top_text).animate().alpha(0f).setDuration(animationDuration);
            mainGroup.findViewById(R.id.main_text).animate().alpha(0f).setDuration(animationDuration);
            mainGroup.findViewById(R.id.label).animate().alpha(0f).setDuration(animationDuration);
            mainGroup.findViewById(R.id.left_text).animate().alpha(0f).setDuration(animationDuration);
            mainGroup.findViewById(R.id.right_text).animate().alpha(0f).setDuration(animationDuration);
        }
    }

    @Override
    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {

        ViewHolder vh = (ViewHolder) super.createRowViewHolder(parent);

        // Set background
        {
            View vg = getViewReflection(vh, mBackgroundViewGroupName);
            vg.setBackgroundColor(mBackgroundColor);
        }

        // Inflate our Group & Texts into parent View
        {
            ViewGroup vg = (ViewGroup) getViewReflection(vh, mRootViewGroupName);
            LayoutInflater.from(vg.getContext()).inflate(R.layout.text_card_details, vg, true);
        }

        vh.setOnKeyListener(new BackKeyListener());

        return vh;
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);

        // Set texts
        StockTV stock = (StockTV) ((DetailsOverviewRow) item).getItem();
        View root = getViewReflection((ViewHolder) holder, mRootViewGroupName);

        //TODO use resource strings for colors
        try {
            String percChange = stock.getPercentChange();
            String directionColor;
            if (percChange != null && !percChange.isEmpty()) {
                char direction = stock.getPercentChange().charAt(0);
                if (direction == '-') {
                    directionColor = "#d6000b";
                } else {
                    directionColor = "#027b45";
                }
            } else {
                directionColor = "#000000";
            }

            setTextAtId(root, R.id.main_text, stock.getPrice(), directionColor);
            setTextAtId(root, R.id.label, "Change", directionColor);
            setTextAtId(root, R.id.left_text, stock.getDollarChange(), directionColor);
            setTextAtIdPercent(root, R.id.right_text, stock.getPercentChange(), directionColor);
            setTextAtId(root, R.id.top_text, "Current Price", directionColor);

        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.d(TAG, "onBindRowViewHolder no more texts");
        }

        // Make ImageView invisible
        // Required to make ImageView invisible, because it has drawable set, and super class will perform some actions on it.
        getViewReflection((ViewHolder) holder, mImageViewName).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onRowViewAttachedToWindow(RowPresenter.ViewHolder holder) {
        super.onRowViewAttachedToWindow(holder);

        // Fade-in animation

        View root = getViewReflection((ViewHolder) holder, mRootViewGroupName);
        animateFadeIn(root.findViewById(R.id.main_group));
    }
}

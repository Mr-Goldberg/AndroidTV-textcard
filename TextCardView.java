package co.adamapps.financeapp.ui.shared;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.adamapps.financeapp.R;

public class TextCardView extends ImageCardView {

    private ViewGroup mMainGroup;
    private TextView mMainText;
    private TextView mLabel;
    private TextView mLeftText;
    private TextView mRightText;

    public TextCardView(Context context) {

        super(context);

        // Inflate layout

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.text_card_view_text_group, this);

        // Get (find) Views

        mMainGroup = (ViewGroup) findViewById(R.id.text_group);
        mMainText = (TextView) findViewById(R.id.main_text);
        mLabel = (TextView) findViewById(R.id.label);
        mLeftText = (TextView) findViewById(R.id.left_text);
        mRightText = (TextView) findViewById(R.id.right_text);

        // Set image explicitly invisible
        // However, this ImageView can't be detached from parent, because it is still used in super class

        if (getMainImageView() != null) {
            getMainImageView().setVisibility(View.INVISIBLE);
        }
    }

    public void setMainText(CharSequence text) {
        mMainText.setText(text);
    }

    public void setMainLabel(CharSequence text) {
        mLabel.setText(text);
    }

    public void setLeftText(CharSequence text) {
        mLeftText.setText(text);
    }

    public CharSequence getLeftText() {
        return mLeftText.getText();
    }

    public CharSequence getRightText() {
        return mLeftText.getText();
    }

    public CharSequence getMainText() {
        return mMainText.getText();
    }

    public void setColorsNegative() {
        mLeftText.setTextColor(getResources().getColor(R.color.card_main_red_color));
        mRightText.setTextColor(getResources().getColor(R.color.card_main_red_color));
        mMainText.setTextColor(getResources().getColor(R.color.card_main_red_color));
    }

    public void setColorsPositive() {
        mLeftText.setTextColor(getResources().getColor(R.color.card_main_green_color));
        mRightText.setTextColor(getResources().getColor(R.color.card_main_green_color));
        mMainText.setTextColor(getResources().getColor(R.color.card_main_green_color));
    }

    public void setColorsNeutral() {
        mLeftText.setTextColor(getResources().getColor(R.color.card_main_black_color));
        mRightText.setTextColor(getResources().getColor(R.color.card_main_black_color));
        mMainText.setTextColor(getResources().getColor(R.color.card_main_black_color));
    }

    public void setRightText(CharSequence text) {
        mRightText.setText(text);
    }

    public void setMainGroupDimensions(int width, int height) {
        ViewGroup.LayoutParams lp = mMainGroup.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mMainGroup.setLayoutParams(lp);
    }

    public void setCardBackgroundColor(int color) {
        mMainGroup.setBackgroundColor(color);
    }

    public void animateFadeIn() {
        mMainGroup.setAlpha(0f);
        if (isAttachedToWindow()) {
            mMainGroup.animate().alpha(1f).setDuration(mMainGroup.getResources().getInteger(android.R.integer.config_shortAnimTime));
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mMainGroup.getAlpha() == 0f) {
            animateFadeIn();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mMainGroup.animate().cancel();
        mMainGroup.setAlpha(1f);
        super.onDetachedFromWindow();
    }
}

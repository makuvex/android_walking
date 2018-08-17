package com.friendly.walking.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendly.walking.R;
import com.friendly.walking.util.JWLog;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

import static com.friendly.walking.fragment.PopupDialogFragment.PopupButtonType.TYPE_BUTTON_CANCEL;
import static com.friendly.walking.fragment.PopupDialogFragment.PopupButtonType.TYPE_BUTTON_CONFIRM;

/**
 * Simple fragment with blur effect behind.
 */

public class PopupDialogFragment extends SupportBlurDialogFragment implements View.OnClickListener {

    public interface DialogButtonClickListener {
        void onClicked(PopupButtonType buttonType);
    };

    public enum PopupButtonType {
        TYPE_BUTTON_CANCEL,
        TYPE_BUTTON_CONFIRM,
    };

    /**
     * Bundle key used to start the blur dialog with a given scale factor (float).
     */
    private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

    /**
     * Bundle key used to start the blur dialog with a given blur radius (int).
     */
    private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

    /**
     * Bundle key used to start the blur dialog with a given dimming effect policy.
     */
    private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";

    /**
     * Bundle key used to start the blur dialog with a given debug policy.
     */
    private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";

    /**
     * Bundle key used for blur effect on action bar policy.
     */
    private static final String BUNDLE_KEY_BLURRED_ACTION_BAR = "bundle_key_blurred_action_bar";

    /**
     * Bundle key used for RenderScript
     */
    private static final String BUNDLE_KEY_USE_RENDERSCRIPT = "bundle_key_use_renderscript";

    /**
     * Bundle key used for cancelable
     */
    private static final String BUNDLE_KEY_CANCELABLE = "bundle_key_cancelable";

    /**
     * Bundle key used for cancelable
     */
    private static final String BUNDLE_KEY_BUTTON_TYPE = "bundle_key_button_type";

    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mDebug;
    private boolean mBlurredActionBar;
    private boolean mUseRenderScript;
    private boolean mCancelable;

    private int mButtonType;
    private TextView    mButtonTextFinish;
    private TextView    mButtonTextConfirm;
    private DialogButtonClickListener   mButtonClickListener;
    /**
     * Retrieve a new instance of the sample fragment.
     *
     * @param radius            blur radius.
     * @param downScaleFactor   down scale factor.
     * @param dimming           dimming effect.
     * @param debug             debug policy.
     * @param mBlurredActionBar blur affect on actionBar policy.
     * @param useRenderScript   use of RenderScript
     * @return well instantiated fragment.
     */

    public static PopupDialogFragment newInstance(boolean cancelable) {

        PopupDialogFragment fragment = new PopupDialogFragment();
        Bundle args = new Bundle();
        args.putInt(
                BUNDLE_KEY_BLUR_RADIUS,
                8
        );
        args.putFloat(
                BUNDLE_KEY_DOWN_SCALE_FACTOR,
                (20 / 10f) + 2
        );
        args.putBoolean(
                BUNDLE_KEY_DIMMING,
                true
        );
        args.putBoolean(
                BUNDLE_KEY_DEBUG,
                false
        );
        args.putBoolean(
                BUNDLE_KEY_BLURRED_ACTION_BAR,
                true
        );
        args.putBoolean(
                BUNDLE_KEY_USE_RENDERSCRIPT,
                false
        );
        args.putBoolean(
                BUNDLE_KEY_CANCELABLE,
                cancelable
        );

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
        mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
        mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
        mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
        mBlurredActionBar = args.getBoolean(BUNDLE_KEY_BLURRED_ACTION_BAR);
        mUseRenderScript = args.getBoolean(BUNDLE_KEY_USE_RENDERSCRIPT);
        mCancelable = args.getBoolean(BUNDLE_KEY_CANCELABLE);
        mButtonType = args.getInt(BUNDLE_KEY_BUTTON_TYPE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setCancelable(mCancelable);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment, null);
        mButtonTextFinish = view.findViewById(R.id.button_text1);
        mButtonTextConfirm = view.findViewById(R.id.button_text2);

        mButtonTextFinish.setOnClickListener(this);
        mButtonTextConfirm.setOnClickListener(this);

//        TextView label = ((TextView) view.findViewById(R.id.textView));
//        label.setMovementMethod(LinkMovementMethod.getInstance());
//        Linkify.addLinks(label, Linkify.WEB_URLS);
        builder.setView(view);

        return builder.create();
    }

    @Override
    protected boolean isDebugEnable() {
        return mDebug;
    }

    @Override
    protected boolean isDimmingEnable() {
        return mDimming;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return mBlurredActionBar;
    }

    @Override
    protected float getDownScaleFactor() {
        return mDownScaleFactor;
    }

    @Override
    protected int getBlurRadius() {
        return mRadius;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return mUseRenderScript;
    }

    @Override
    public void onClick(View view) {
        JWLog.e("view "+view);

        if(view.getId() == mButtonTextFinish.getId()) {
            if(mButtonClickListener != null) {
                mButtonClickListener.onClicked(TYPE_BUTTON_CANCEL);
            }
        } else if(view.getId() == mButtonTextConfirm.getId()) {
            if(mButtonClickListener != null) {
                mButtonClickListener.onClicked(TYPE_BUTTON_CONFIRM);
            }
        }
    }

    public void setClickListener(DialogButtonClickListener listener) {
        mButtonClickListener = listener;
    }
}

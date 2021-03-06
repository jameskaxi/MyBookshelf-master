package com.monke.monkeybook.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monke.monkeybook.R;
import com.monke.monkeybook.utils.DensityUtil;
import com.monke.monkeybook.view.adapter.AudioTimerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioTimerPop extends PopupWindow implements AudioTimerAdapter.OnItemSelectListener {

    @BindView(R.id.recycler_view)
    RecyclerView rvList;
    @BindView(R.id.btn_close)
    View btnClose;

    private AudioTimerAdapter adapter;
    private Context context;

    private OnTimeSelectListener listener;


    public AudioTimerPop(Context context, OnTimeSelectListener listener) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(context, 450));
        this.context = context;
        this.listener = listener;

        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_audio_timer, null);
        setContentView(contentView);
        ButterKnife.bind(this, contentView);

        init();

        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_pop_checkaddshelf_bg));
        setFocusable(true);
        setTouchable(true);
        setAnimationStyle(R.style.anim_pop_windowslide);
    }


    private void init() {
        adapter = new AudioTimerAdapter(context, this);

        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setHasFixedSize(true);
        rvList.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dismiss());
    }

    public void upIndexByValue(int value) {
        if (adapter != null) {
            adapter.upIndexByValue(value);
        }
    }

    @Override
    public void onSelected(int timerMinute) {
        if (listener != null) {
            listener.onSelected(timerMinute);
        }
        dismiss();
    }

    public interface OnTimeSelectListener {
        void onSelected(int timerMinute);
    }

}

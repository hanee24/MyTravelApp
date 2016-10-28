package com.example.parkhanee.mytravelapp.sectionedexpandablegridlayout.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.parkhanee.mytravelapp.R;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 10. 26..
 */
public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder>  {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.recyclerview_area_section;
    private static final int VIEW_TYPE_AREA = R.layout.recyclerview_area_item;

    public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                          final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                          SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isSection(position)?gridLayoutManager.getSpanCount():1;
            }
        });
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof Section;
    }


    @Override
    public SectionedExpandableGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(SectionedExpandableGridAdapter.ViewHolder holder, final int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_AREA :
                final Area area = (Area) mDataArrayList.get(position);
                holder.itemTextView.setText(area.getName());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(area);
                    }
                });
                break;
            case VIEW_TYPE_SECTION :
                final Section section = (Section) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getName());
                holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mItemClickListener.itemClicked(section);
                    }
                });
                holder.sectionToggleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (section.isExpanded){ // 닫혀있는 상태에서 열려고 눌렀을 때 AreaFragment 에서 onItemClick(Section) 처리.
                            mSectionStateChangeListener.onSectionStateChanged(section, false);
                        }else {
                            mItemClickListener.itemClicked(section);
                            section.isExpanded=true;
                            mSectionStateChangeListener.onSectionStateChanged(section, true);
                            for (int i=0;i<mDataArrayList.size();i++){ // 선택된 섹션 이외에는 다 닫히도록
                                if (isSection(i) && i!=position){
                                    Section ss = (Section)mDataArrayList.get(i);
                                    ss.isExpanded = false;
                                    mSectionStateChangeListener.onSectionStateChanged(ss, false);
                                }
                            }
                        }
                    }
                });
                holder.sectionToggleButton.setChecked(section.isExpanded);
//                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    }
//                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // mDataArrayList에는 section, area 둘 다 그냥 주욱 들어있어서 차례대로 onBindViewHolder 에서 읽으면서 두개를 두 타입으로 나눠서 처리
        if (isSection(position))
            return VIEW_TYPE_SECTION;
        else return VIEW_TYPE_AREA;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;

        //for section
        TextView sectionTextView;
        ToggleButton sectionToggleButton;

        //for item
        TextView itemTextView;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_AREA) {
                itemTextView = (TextView) view.findViewById(R.id.text_item);
            } else {
                sectionTextView = (TextView) view.findViewById(R.id.text_section);
                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
            }
        }
    }

}

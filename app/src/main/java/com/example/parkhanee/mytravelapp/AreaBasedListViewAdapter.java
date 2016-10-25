package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by parkhanee on 2016. 10. 25..
 */
public class AreaBasedListViewAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupList = null;
    private ArrayList<Integer> groupCodeList = null;
    private ArrayList<ArrayList<String>> childList = null;
    private ArrayList<String> childListContent = null;
    private Context context = null;

    public AreaBasedListViewAdapter(Context context){
        super();
        this.context = context;
        groupList = new ArrayList<>();
        groupCodeList = new ArrayList<>();
        childList = new ArrayList<>();
        childListContent = new ArrayList<>();

        new setViews().execute();
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public String getGroup(int i) {
        return groupList.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_expandable_group, viewGroup, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView6);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.textView.setText(getGroup(i));

        return convertView;
    }

    @Override
    public int getChildrenCount(int i) {
        return childList.get(i).size();
    }

    @Override
    public String getChild(int i, int i1) {
        return childList.get(i).get(i1);
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if(view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_expandable_child, viewGroup, false);
            viewHolder.textView = (TextView) view.findViewById(R.id.textView7);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.textView.setText(getChild(i,i1));
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    class ViewHolder {
        TextView textView=null;
    }

    public class setViews extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Collections.addAll(groupList,
                    "서울","인천","대전","대구","광주","부산","울산","세종특별자치시","경기도","강원도"
                    ,"충청북도","충청남도","경상북도","경상남도","전라북도","전라남도","제주도");
            Collections.addAll(groupCodeList,
                    1,2,3,4,5,6,7,8,31,32,
                    33,34,35,36,37,38,39);
            // http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaCode?areaCode=1&ServiceKey=EhbtW6DbKWT%2BdDTSXCMIK0gvNEvTNh66inoT47CrOANRMxpY4K2wYdsrMH0LLKSg0isRPVIcIq99swJ2RJdMKg%3D%3D&MobileOS=AND&MobileApp=AppTesting&numOfRows=20&pageNo=1
            // 1 서울
            Collections.addAll(childListContent,
                    "강남구","강동구","강북구","강서구","관악구","광진구","구로구","금천구","노원구","도봉구","동대문구"
                    ,"동작구","마포구","서대문구","서초구","성동구","성북구","송파구","양천구","영등포구","용산구",
                    "은평구","종로구","중구","중랑구");
            childList.add(0,childListContent);
            //2 인천
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "강화군","계양구","남구","남동구","동구","부평구","서구","연수구","옹진군","중구");
            childList.add(1,childListContent);
            //3 대전
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "대덕구","동구","서구","유성구","중구");
            childList.add(2,childListContent);
            //4 대구
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "남구","달서구","달성군","동구","북구","서구","수성구","중구");
            childList.add(3,childListContent);
            //5 광주
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "광산구","남구","동구","북구","서구");
            childList.add(4,childListContent);
            //6 부산
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "강서구","금정구","기장군","남구","동구","동래구","부산진구","북구","사상구","사하구","서구"
                    ,"수영구","연제구","영도구","중구","해운대구"); // 16
            childList.add(5,childListContent);
            //7 울산
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "중구","남구","동구","북구","울주군"); // 5
            childList.add(6,childListContent);
            //8 세종특별자치시
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "세종특별자치시"); // 1
            childList.add(7,childListContent);
            //31 경기도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "가평군","고양시","과천시","광명시","광주시","구리시","군포시","김포시","남양주시","동두천시","부천시"
                    ,"성남시","수원시","시흥시","안산시","안성시","안양시","양주시","양평군","여주시","연천군",
                    "오산시","용인시","의왕시","의정부시","이천시","파주시","평택시","포천시","하남시","화성시"); //31
            childList.add(8,childListContent);
            //32 강원도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "강릉시","고성군","동해시","삼척시","속초시","양구군","양양군","영월군","원주시","인제군","정선군"
                    ,"철원군","춘천시","태백시","평창군","홍천군","화천군","횡성군"); //18
            childList.add(9,childListContent);
            //33 충청북도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "괴산군","단양군","보은군","영동군","옥천군","음성군","제천시","진천군","청원군","청주시","충주시"
                    ,"증평군"); //12
            childList.add(10,childListContent);
            //34 충청남도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "공주시","금산군","논산시","당진시","보령시","부여군","서산시","서천군","아산시","예산군","천안시"
                    ,"청양군","태안군","홍성군","계룡시"); //15
            // 인덱스 1 2 3 4 5 6 7 8 9 11 12 13 14 15 16 으로 10번이 없음
            childList.add(11,childListContent);
            // 35 경상북도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "경산시","경주시","고령군","구미시","군위군","김천시","문경시","봉화군","상주시","성주군","안동시"
                    ,"영덕군","영양군","영주시","영천시","예천군","울릉군","울진군","의성군","청도군","청송군",
                    "칠곡군","포항시"); // 23
            childList.add(12,childListContent);
            // 36 경상남도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "거제시","거창군","고성군","김해시","남해군","마산시","밀양시","사천시","산청군","양산시","의령군"
                    ,"진주시","진해시","창녕군","창원시","통영시","하동군","함안군","함양군","합천군"); // 20
            // 인덱스 11이 없음
            childList.add(13,childListContent);
            // 37 전라북도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "고창군","군산시","김제시","남원시","무주군","부안군","순창군","완주군","익산시","임실군","장수군"
                    ,"전주시","정읍시","진안군"); // 14
            childList.add(14,childListContent);
            // 38 전라남도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "강진군","고흥군","곡성군","광양시","구례군","나주시","담양군","목포시","무안군","보성군","순천시"
                    ,"신안군","여수시","영광군","영암군","완도군","장성군","장흥군","진도군","함평군",
                    "해남군","화순군"); // 22
            // 인덱스 14, 15 없음
            childList.add(15,childListContent);
            // 39 제주도
            childListContent = new ArrayList<>();
            Collections.addAll(childListContent,
                    "남제주군","북제주군","서귀포시","제주시"); // 4
            childList.add(16,childListContent);

            return null;
        }
    }
}

package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parkhanee.mytravelapp.sectionedexpandablegridlayout.adapters.Area;
import com.example.parkhanee.mytravelapp.sectionedexpandablegridlayout.adapters.ItemClickListener;
import com.example.parkhanee.mytravelapp.sectionedexpandablegridlayout.adapters.Section;
import com.example.parkhanee.mytravelapp.sectionedexpandablegridlayout.adapters.SectionedExpandableLayoutHelper;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 9. 6..
 */
public class AreaFragment extends Fragment implements ItemClickListener{

    RecyclerView mRecyclerView;
    SectionedExpandableLayoutHelper helper;

    int areaCode=1;
    String area="서울";
    String TAG = "AreaFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_area,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //setting the recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        helper = new SectionedExpandableLayoutHelper(getContext(), mRecyclerView, this, 3);

        new setViews().execute();
    }

    @Override
    public void itemClicked(Area area) {
        Intent i = new Intent(getActivity(), NearbyD2Activity.class);
        i.putExtra("isNearby",false);
        i.putExtra("sigungu",area.getName());
        i.putExtra("sigunguCode",area.getId());
        i.putExtra("area",this.area);
        i.putExtra("areaCode",areaCode);
        startActivity(i);
        Log.d(TAG,  "Area: "+ area.getId()+area.getName() + " clicked");
    }

    @Override
    public void itemClicked(Section section) {
        Log.d(TAG, "Section: "+ section.getId() + section.getName() + " clicked");
        areaCode = section.getId();
        area = section.getName();
    }



    public class setViews extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            helper.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaCode?areaCode=1&ServiceKey=EhbtW6DbKWT%2BdDTSXCMIK0gvNEvTNh66inoT47CrOANRMxpY4K2wYdsrMH0LLKSg0isRPVIcIq99swJ2RJdMKg%3D%3D&MobileOS=AND&MobileApp=AppTesting&numOfRows=20&pageNo=1
            // 1 서울
            ArrayList<Area> arrayList = new ArrayList<>();
            arrayList.add(new Area("강남구",1));
            arrayList.add(new Area("강동구",2));
            arrayList.add(new Area("강북구",3));
            arrayList.add(new Area("강서구",4));
            arrayList.add(new Area("관악구",5));
            arrayList.add(new Area("광진구",6));
            arrayList.add(new Area("구로구",7));
            arrayList.add(new Area("금천구",8));
            arrayList.add(new Area("노원구",9));
            arrayList.add(new Area("도봉구",10));
            arrayList.add(new Area("동대문구",11));
            arrayList.add(new Area("동작구",12));
            arrayList.add(new Area("마포구",13));
            arrayList.add(new Area("서대문구",14));
            arrayList.add(new Area("서초구",15));
            arrayList.add(new Area("성동구",16));
            arrayList.add(new Area("성북구",17));
            arrayList.add(new Area("송파구",18));
            arrayList.add(new Area("양천구",19));
            arrayList.add(new Area("영등포구",20));
            arrayList.add(new Area("용산구",21));
            arrayList.add(new Area("은평구",22));
            arrayList.add(new Area("종로구",23));
            arrayList.add(new Area("중구",24));
            arrayList.add(new Area("중랑구",25));
            helper.addSection("서울",1,arrayList);

            //2 인천
            arrayList = new ArrayList<>();
            arrayList.add(new Area("강화군",1));
            arrayList.add(new Area("계양구",2));
            arrayList.add(new Area("남구",3));
            arrayList.add(new Area("남동구",4));
            arrayList.add(new Area("동구",5));
            arrayList.add(new Area("부평구",6));
            arrayList.add(new Area("서구",7));
            arrayList.add(new Area("연수구",8));
            arrayList.add(new Area("옹진군",9));
            arrayList.add(new Area("중구",10));
            helper.addSection("인천",2,arrayList);

            //3 대전
            arrayList = new ArrayList<>();
            arrayList.add(new Area("대덕구",1));
            arrayList.add(new Area("동구",2));
            arrayList.add(new Area("서구",3));
            arrayList.add(new Area("유성구",4));
            arrayList.add(new Area("중구",5));
            helper.addSection("대전",3,arrayList);

            //4 대구
            arrayList = new ArrayList<>();
            arrayList.add(new Area("남구",1));
            arrayList.add(new Area("달서구",2));
            arrayList.add(new Area("달성군",3));
            arrayList.add(new Area("동구",4));
            arrayList.add(new Area("북구",5));
            arrayList.add(new Area("서구",6));
            arrayList.add(new Area("수성구",7));
            arrayList.add(new Area("중구",8));
            helper.addSection("대구",4,arrayList);

            //5 광주
            arrayList = new ArrayList<>();
            arrayList.add(new Area("광산구",1));
            arrayList.add(new Area("남구",2));
            arrayList.add(new Area("동구",3));
            arrayList.add(new Area("북구",4));
            arrayList.add(new Area("서구",5));
            helper.addSection("광주",5,arrayList);

            //6 부산
            arrayList = new ArrayList<>();
            arrayList.add(new Area("강서구",1));
            arrayList.add(new Area("금정구",2));
            arrayList.add(new Area("기장군",3));
            arrayList.add(new Area("남구",4));
            arrayList.add(new Area("동구",5));
            arrayList.add(new Area("동래구",6));
            arrayList.add(new Area("부산진구",7));
            arrayList.add(new Area("북구",8));
            arrayList.add(new Area("사상구",9));
            arrayList.add(new Area("사하구",10));
            arrayList.add(new Area("서구",11));
            arrayList.add(new Area("수영구",12));
            arrayList.add(new Area("연제구",13));
            arrayList.add(new Area("영도구",14));
            arrayList.add(new Area("중구",15));
            arrayList.add(new Area("해운대구",16));
            helper.addSection("부산",6,arrayList);

            //7 울산
            arrayList = new ArrayList<>();
            arrayList.add(new Area("중구",1));
            arrayList.add(new Area("남구",2));
            arrayList.add(new Area("동구",3));
            arrayList.add(new Area("북구",4));
            arrayList.add(new Area("울주군",5));
            helper.addSection("울산",7,arrayList);


            //8 세종특별자치시
            arrayList = new ArrayList<>();
            arrayList.add(new Area("세종특별자치시",1));
            helper.addSection("세종특별자치시",8,arrayList);


            //31 경기도
            arrayList = new ArrayList<>();
            arrayList.add(new Area("가평군",1));
            arrayList.add(new Area("고양시",2));
            arrayList.add(new Area("과천시",3));
            arrayList.add(new Area("광명시",4));
            arrayList.add(new Area("광주시",5));
            arrayList.add(new Area("구리시",6));
            arrayList.add(new Area("군포시",7));
            arrayList.add(new Area("김포시",8));
            arrayList.add(new Area("남양주시",9));
            arrayList.add(new Area("동두천시",10));
            arrayList.add(new Area("부천시",11));
            arrayList.add(new Area("성남시",12));
            arrayList.add(new Area("수원시",13));
            arrayList.add(new Area("시흥시",14));
            arrayList.add(new Area("안산시",15));
            arrayList.add(new Area("안성시",16));
            arrayList.add(new Area("안양시",17));
            arrayList.add(new Area("양주시",18));
            arrayList.add(new Area("양평군",19));
            arrayList.add(new Area("여주시",20));
            arrayList.add(new Area("연천군",21));
            arrayList.add(new Area("오산시",22));
            arrayList.add(new Area("용인시",23));
            arrayList.add(new Area("의왕시",24));
            arrayList.add(new Area("의정부시",25));
            arrayList.add(new Area("이천시",26));
            arrayList.add(new Area("파주시",27));
            arrayList.add(new Area("평택시",28));
            arrayList.add(new Area("포천시",29));
            arrayList.add(new Area("하남시",30));
            arrayList.add(new Area("화성시",31));
            helper.addSection("경기도",31,arrayList);

            //32 강원도
            arrayList = new ArrayList<>();
            arrayList.add(new Area("강릉시",1));
            arrayList.add(new Area("고성군",2));
            arrayList.add(new Area("동해시",3));
            arrayList.add(new Area("삼척시",4));
            arrayList.add(new Area("속초시",5));
            arrayList.add(new Area("양구군",6));
            arrayList.add(new Area("양양군",7));
            arrayList.add(new Area("영월군",8));
            arrayList.add(new Area("원주시",9));
            arrayList.add(new Area("인제군",10));
            arrayList.add(new Area("정선군",11));
            arrayList.add(new Area("철원군",12));
            arrayList.add(new Area("춘천시",13));
            arrayList.add(new Area("태백시",14));
            arrayList.add(new Area("평창군",15));
            arrayList.add(new Area("홍천군",16));
            arrayList.add(new Area("화천군",17));
            arrayList.add(new Area("횡성군",18));
            helper.addSection("강원도",32,arrayList);

            //33 충청북도
            arrayList = new ArrayList<>();
            arrayList.add(new Area("괴산군",1));
            arrayList.add(new Area("단양군",2));
            arrayList.add(new Area("보은군",3));
            arrayList.add(new Area("영동군",4));
            arrayList.add(new Area("옥천군",5));
            arrayList.add(new Area("음성군",6));
            arrayList.add(new Area("제천시",7));
            arrayList.add(new Area("진천군",8));
            arrayList.add(new Area("청원군",9));
            arrayList.add(new Area("청주시",10));
            arrayList.add(new Area("충주시",11));
            arrayList.add(new Area("증평군",12));
            helper.addSection("충청북도",33,arrayList);

            //34 충청남도
            // 15개,  인덱스 10없음
            arrayList = new ArrayList<>();
            arrayList.add(new Area("공주시",1));
            arrayList.add(new Area("금산군",2));
            arrayList.add(new Area("논산시",3));
            arrayList.add(new Area("당진시",4));
            arrayList.add(new Area("보령시",5));
            arrayList.add(new Area("부여군",6));
            arrayList.add(new Area("서산시",7));
            arrayList.add(new Area("서천군",8));
            arrayList.add(new Area("아산시",9));
            arrayList.add(new Area("예산군",11));
            arrayList.add(new Area("천안시",12));
            arrayList.add(new Area("청양군",13));
            arrayList.add(new Area("태안군",14));
            arrayList.add(new Area("홍성군",15));
            arrayList.add(new Area("계룡시",16));
            helper.addSection("충청남도",34,arrayList);

            // 35 경상북도
            arrayList = new ArrayList<>();
            arrayList.add(new Area("경산시",1));
            arrayList.add(new Area("경주시",2));
            arrayList.add(new Area("고령군",3));
            arrayList.add(new Area("구미시",4));
            arrayList.add(new Area("군위군",5));
            arrayList.add(new Area("김천시",6));
            arrayList.add(new Area("문경시",7));
            arrayList.add(new Area("봉화군",8));
            arrayList.add(new Area("상주시",9));
            arrayList.add(new Area("성주군",10));
            arrayList.add(new Area("안동시",11));
            arrayList.add(new Area("영덕군",12));
            arrayList.add(new Area("영양군",13));
            arrayList.add(new Area("영주시",14));
            arrayList.add(new Area("영천시",15));
            arrayList.add(new Area("예천군",16));
            arrayList.add(new Area("울릉군",17));
            arrayList.add(new Area("울진군",18));
            arrayList.add(new Area("의성군",19));
            arrayList.add(new Area("청도군",20));
            arrayList.add(new Area("청송군",21));
            arrayList.add(new Area("칠곡군",22));
            arrayList.add(new Area("포항시",23));
            helper.addSection("경상북도",35,arrayList);

            // 36 경상남도
            arrayList = new ArrayList<>();
            arrayList.add(new Area("거제시",1));
            arrayList.add(new Area("거창군",2));
            arrayList.add(new Area("고성군",3));
            arrayList.add(new Area("김해시",4));
            arrayList.add(new Area("남해군",5));
            arrayList.add(new Area("마산시",6));
            arrayList.add(new Area("밀양시",7));
            arrayList.add(new Area("사천시",8));
            arrayList.add(new Area("산청군",9));
            arrayList.add(new Area("양산시",10));
            arrayList.add(new Area("의령군",12));
            arrayList.add(new Area("진주시",13));
            arrayList.add(new Area("진해시",14));
            arrayList.add(new Area("창녕군",15));
            arrayList.add(new Area("창원시",16));
            arrayList.add(new Area("통영시",17));
            arrayList.add(new Area("하동군",18));
            arrayList.add(new Area("함안군",19));
            arrayList.add(new Area("함양군",20));
            arrayList.add(new Area("합천군",21));
            helper.addSection("경상남도",36,arrayList);


            // 37 전라북도
            arrayList = new ArrayList<>();
            arrayList.add(new Area("고창군",1));
            arrayList.add(new Area("군산시",2));
            arrayList.add(new Area("김제시",3));
            arrayList.add(new Area("남원시",4));
            arrayList.add(new Area("무주군",5));
            arrayList.add(new Area("부안군",6));
            arrayList.add(new Area("순창군",7));
            arrayList.add(new Area("완주군",8));
            arrayList.add(new Area("익산시",9));
            arrayList.add(new Area("임실군",10));
            arrayList.add(new Area("장수군",11));
            arrayList.add(new Area("전주시",12));
            arrayList.add(new Area("정읍시",13));
            arrayList.add(new Area("진안군",14));
            helper.addSection("전라북도",37,arrayList);

            // 38 전라남도 // 22
            // 인덱스 14, 15 없음
            arrayList = new ArrayList<>();
            arrayList.add(new Area("강진군",1));
            arrayList.add(new Area("고흥군",2));
            arrayList.add(new Area("곡성군",3));
            arrayList.add(new Area("광양시",4));
            arrayList.add(new Area("구례군",5));
            arrayList.add(new Area("나주시",6));
            arrayList.add(new Area("담양군",7));
            arrayList.add(new Area("목포시",8));
            arrayList.add(new Area("무안군",9));
            arrayList.add(new Area("보성군",10));
            arrayList.add(new Area("순천시",11));
            arrayList.add(new Area("신안군",12));
            arrayList.add(new Area("여수시",13));
            arrayList.add(new Area("영광군",16));
            arrayList.add(new Area("영암군",17));
            arrayList.add(new Area("완도군",18));
            arrayList.add(new Area("장성군",19));
            arrayList.add(new Area("장흥군",20));
            arrayList.add(new Area("진도군",21));
            arrayList.add(new Area("함평군",22));
            arrayList.add(new Area("해남군",23));
            arrayList.add(new Area("화순군",24));
            helper.addSection("전라남도",38,arrayList);

            // 39 제주도
            arrayList = new ArrayList<>();
            arrayList.add(new Area("남제주군",1));
            arrayList.add(new Area("북제주군",2));
            arrayList.add(new Area("서귀포시",3));
            arrayList.add(new Area("제주시",4));
            helper.addSection("제주도",39,arrayList);

            return null;
        }
    }
}

package cn.edu.bistu.cs.se.mywordsapp;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WordDetailActivity extends AppCompatActivity implements WordDetailFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //如果是横屏的话直接退出
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }


        if (savedInstanceState == null) {
            WordDetailFragment detailFragment = new WordDetailFragment();
            detailFragment.setArguments(getIntent().getExtras());//传递参数
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, detailFragment)//向Activity中添加Fragment
                    .commit();
        }
    }

    @Override
    public void onWordDetailClick(Uri uri) {

    }
}

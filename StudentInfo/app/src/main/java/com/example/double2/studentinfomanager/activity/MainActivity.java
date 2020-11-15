package com.example.double2.studentinfomanager.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.adapter.ShowAdapter;
import com.example.double2.studentinfomanager.db.StudentDateBaseHelper;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    //控件
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private RecyclerView rvMain;
    private FloatingActionButton fabtnAdd;
    //数据存储
    private StudentDateBaseHelper mStudentDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mStudentDateBaseHelper = new StudentDateBaseHelper(this, "StudentInfo.db", null, 1);
        mSQLiteDatabase = mStudentDateBaseHelper.getReadableDatabase();
        mSharedPreferences = this.getSharedPreferences("student", MODE_PRIVATE);

        initView();
    }


    //初始化
    private void initView() {
        setToolBar();

        rvMain = (RecyclerView) findViewById(R.id.rv_main);
        rvMain.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        fabtnAdd = (FloatingActionButton) findViewById(R.id.fabtn_main_add);
        fabtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("type", EditActivity.TYPE_ADD);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        refreshRecyclerView();
    }

    //刷新列表
    private void refreshRecyclerView() {
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> gender = new ArrayList<>();

        //查询所有数据
        Cursor mCursor = mSQLiteDatabase.query("student", null, null, null, null, null, null);

        int size = mCursor.getCount() < ShowAdapter.maxSize ? mCursor.getCount() : ShowAdapter.maxSize;

        while (true) {
            if (size-- == 0)
                break;
            mCursor.moveToNext();
            number.add(mCursor.getString(mCursor.getColumnIndex("number")));
            name.add(mCursor.getString(mCursor.getColumnIndex("name")));
            gender.add(mCursor.getString(mCursor.getColumnIndex("gender")));
        }
        mCursor.close();


        rvMain.setAdapter(new ShowAdapter(MainActivity.this, number, name, gender));
    }


    //设置工具栏
    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle("学生信息");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchAction();
                return false;
            }
        });
    }




    //搜索对话框
    private void searchAction() {
        final String[] arrayGender = new String[]{"学号", "姓名"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("搜索类型")
                .setItems(arrayGender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        switch (which) {
                            case 0:
                                intent.putExtra("search_type", SearchActivity.TYPE_SEARCH_NUMBER);
                                break;
                            case 1:
                                intent.putExtra("search_type", SearchActivity.TYPE_SEARCH_NAME);
                                break;
                        }
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }


    @Override
    //创建菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
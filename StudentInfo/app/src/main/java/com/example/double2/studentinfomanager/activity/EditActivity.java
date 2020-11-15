package com.example.double2.studentinfomanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.db.StudentDateBaseHelper;

public class EditActivity extends Activity implements View.OnClickListener {
    //控件
    private Button btnBack;
    private Button btnSure;
    private EditText etNumber;
    private EditText etName;
    private TextView etYN;
    private TableRow trYN;
    private EditText etSpecialty;
    private EditText ettemperature;
    private TextView tvGender;
    private TableRow trGender;
    private TextView tvDate;
    private TableRow trDate;
    private TextView tvDelete;
    //数据存储
    private StudentDateBaseHelper mStudentDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    //变量与常量
    public static final int TYPE_ADD = 111;
    public static final int TYPE_EDIT = 222;
    private int currentType;//判断当前是需要增加还是修改
    private String initNumber;//此处用来记录原来在数据库中的存储条目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit);

        mStudentDateBaseHelper = new StudentDateBaseHelper(this, "StudentInfo.db", null, 1);
        mSQLiteDatabase = mStudentDateBaseHelper.getReadableDatabase();

        receiveType();
        initView();
        receiveInfo();
    }

    //获取当前的编辑类型，是增添或者编辑
    private void receiveType() {
        Intent intent = this.getIntent();
        currentType = intent.getIntExtra("type", TYPE_ADD);
    }

    //从数据库获取学生信息显示到界面上
    private void receiveInfo() {

        if (currentType == TYPE_EDIT) {
            Intent intent = this.getIntent();
            initNumber = intent.getStringExtra("number");
            String name;
            String gender;
            String date;
            String YN;
            String specialty;
            String temperature;

            Cursor mCursor = mSQLiteDatabase.query("student", null, "number=?", new String[]{initNumber}, null, null, null);

            if (mCursor.moveToNext()) {
                name = mCursor.getString(mCursor.getColumnIndex("name"));
                gender = mCursor.getString(mCursor.getColumnIndex("gender"));
                date = mCursor.getString(mCursor.getColumnIndex("date"));
                YN = mCursor.getString(mCursor.getColumnIndex("YN"));
                specialty = mCursor.getString(mCursor.getColumnIndex("specialty"));
                temperature = mCursor.getString(mCursor.getColumnIndex("temperature"));

                etNumber.setText(initNumber);
                etName.setText(name);
                tvGender.setText(gender);
                tvDate.setText(date);
                etYN.setText(YN);
                etSpecialty.setText(specialty);
                ettemperature.setText(temperature);
            }
        }
    }


    //初始化
    private void initView() {
        btnBack = (Button) findViewById(R.id.btn_edit_back);
        btnSure = (Button) findViewById(R.id.btn_edit_sure);
        etNumber = (EditText) findViewById(R.id.et_edit_number);
        etName = (EditText) findViewById(R.id.et_edit_name);
        etYN = (TextView) findViewById(R.id.et_edit_YN);
        trYN = (TableRow) findViewById(R.id.tr_edit_YN);
        etSpecialty = (EditText) findViewById(R.id.et_edit_specialty);
        ettemperature = (EditText) findViewById(R.id.et_edit_temperature);
        tvGender = (TextView) findViewById(R.id.tv_edit_gender);
        trGender = (TableRow) findViewById(R.id.tr_edit_gender);
        tvDate = (TextView) findViewById(R.id.tv_edit_date);
        trDate = (TableRow) findViewById(R.id.tr_edit_date);
        tvDelete = (TextView) findViewById(R.id.tv_edit_delete);

        btnBack.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        trYN.setOnClickListener(this);
        trGender.setOnClickListener(this);
        trDate.setOnClickListener(this);
        tvDelete.setOnClickListener(this);

        switch (currentType) {
            case TYPE_ADD:
                //如果是增添信息模式，就让删除按钮不可见
                tvDelete.setVisibility(View.GONE);
                break;
            case TYPE_EDIT:
                tvDelete.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_back:
                finish();
                break;
            case R.id.btn_edit_sure:
                btnSureAction();
                break;
            case R.id.tr_edit_gender:
                trGenderAction();
                break;
            case R.id.tr_edit_YN:
                trYNAction();
                break;
            case R.id.tr_edit_date:
                trDateAction();
                break;
            case R.id.tv_edit_delete:
                tvDeleteAction();
                break;
        }
    }

    //性别对话框
    private void trGenderAction() {
        final String[] arrayGender = new String[]{"男", "女"};
        new AlertDialog.Builder(EditActivity.this)
                .setTitle("性别")
                .setItems(arrayGender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvGender.setText(arrayGender[which]);
                    }
                })
                .create()
                .show();
    }


    //是否离京对话框
    private void trYNAction() {
        final String[] arrayGender = new String[]{"是", "否"};
        new AlertDialog.Builder(EditActivity.this)
                .setTitle("近期是否离京")
                .setItems(arrayGender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etYN.setText(arrayGender[which]);
                    }
                })
                .create()
                .show();
    }


    //删除对话框
    private void tvDeleteAction() {
        new AlertDialog.Builder(EditActivity.this)
                .setTitle("确认删除此学生信息？")
                .setMessage("学号：" + initNumber)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSQLiteDatabase.delete("student", "number=?", new String[]{initNumber});
                        finish();
                        Toast.makeText(EditActivity.this, "该学生信息删除成功！", Toast.LENGTH_SHORT).show();
                    }
                })
                //“取消”设为null
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    //日期对话框
    private void trDateAction() {
        final DatePicker dpDate = (DatePicker) getLayoutInflater().inflate(R.layout.dialog_edit_date, null);

        new AlertDialog.Builder(EditActivity.this)
                .setTitle("修改日期")
                .setView(dpDate)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //将Activity中的textview显示AlertDialog中EditText中的内容
                        //用Toast显示
                        tvDate.setText(dpDate.getYear() + "年" + dpDate.getMonth() + "月" + dpDate.getDayOfMonth() + "日");
                    }
                })
                //“取消”设为null
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    //确定按钮
    private void btnSureAction() {
        String number = etNumber.getText().toString();
        String name = etName.getText().toString();
        String gender = tvGender.getText().toString();
        String YN = etYN.getText().toString();
        String specialty = etSpecialty.getText().toString();
        String temperature = ettemperature.getText().toString();
        String date = tvDate.getText().toString();

        if (notNull(number, name, gender, YN, specialty, temperature, date)) {
                ContentValues values = new ContentValues();
                values.put("number", number);
                values.put("name", name);
                values.put("gender", gender);
                values.put("YN", YN);
                values.put("specialty", specialty);
                values.put("temperature", temperature);
                values.put("date", date);

                switch (currentType) {
                    case TYPE_ADD:
                        mSQLiteDatabase.insert("student", null, values);
                        Toast.makeText(EditActivity.this, "添加数据成功", Toast.LENGTH_SHORT).show();
                        break;
                    case TYPE_EDIT:
                        mSQLiteDatabase.update("student", values, "number=?", new String[]{initNumber});
                        Toast.makeText(EditActivity.this, "数据修改成功", Toast.LENGTH_SHORT).show();
                        break;
                }
                finish();
        } else {
            Toast.makeText(EditActivity.this, "数据不可以为空！", Toast.LENGTH_SHORT).show();
        }
    }

    //如果任何一个EditText中为空，就返回flase
    private boolean notNull(String number, String name, String gender, String YN ,
                            String specialty, String temperature , String date ) {
        if (number.equals(""))
            return false;
        if (name.equals(""))
            return false;
        if (gender.equals(""))
            return false;
        if (YN .equals(""))
            return false;
        if (specialty.equals(""))
            return false;
        if (temperature .equals(""))
            return false;
        return true;
    }

}

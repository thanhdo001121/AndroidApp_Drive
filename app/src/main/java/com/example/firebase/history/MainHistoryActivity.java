package com.example.firebase.history;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebase.BuildConfig;
import com.example.firebase.R;
import com.example.firebase.history.fragments.PageAdapter;
import com.example.firebase.history.history_adjustment.AddActionGasOil;
import com.example.firebase.history.history_adjustment.AddActionRepair;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class MainHistoryActivity extends AppCompatActivity {

    private TabLayout tlAction;
    private TabItem tabGas, tabOil, tabRepair;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private int intHistoryStatus;
    private ImageView btnBack;

    private DatabaseReference dbHistory;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    String currentUserId = mFirebaseUser.getUid();
    private ArrayList<History> historyArrayList = new ArrayList<History>();

    //animation for floating button
    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;

    private FloatingActionButton btnMore, btnAdd, btnExport, btnShare;
    private boolean clicked = false;

    // Width and height of PDF page
    int pageWidth = 792;
    int pageHeight = 1136;

    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 200;

    String pdfFileName = "";
    String excelFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_history);


        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        btnAdd = findViewById(R.id.btnAdd);
        btnExport = findViewById(R.id.btnExport);
        btnMore = findViewById(R.id.btnMore);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.backImg);

        tlAction = findViewById(R.id.tlAction);
        tabGas = findViewById(R.id.tabGas);
        tabOil = findViewById(R.id.tabOil);
        tabRepair = findViewById(R.id.tabRepair);
        viewPager = findViewById(R.id.vpager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tlAction.getTabCount(), MainHistoryActivity.this);
        viewPager.setAdapter(pageAdapter);

        //Config for tab Action layout
        tlAction.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == Constants.GAS_TAB || tab.getPosition() == Constants.OIL_TAB || tab.getPosition() == Constants.REPAIR_TAB)
                    intHistoryStatus = tab.getPosition();
                pageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlAction));

        // Floating button
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(clicked);
                setAnimation(clicked);
                setClickable(clicked);
                clicked = !clicked;
            }
        });

        //pull all data from DB for export
        dbHistory = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History");;
        dbHistory.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                History history = snapshot.getValue(History.class);
                historyArrayList.add(history);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportPDF();
                exportExcel();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exportPDF();
                exportExcel();

                try {
//                    File pdfFile = new File(MainHistoryActivity.this.getExternalFilesDir(null).getAbsolutePath(), pdfFileName);
//                    File excelFile = new File(MainHistoryActivity.this.getExternalFilesDir(null).getAbsolutePath(), excelFileName);
//
//                    ArrayList<Uri> uris = new ArrayList<>();
//
//                    uris.add(FileProvider.getUriForFile(MainHistoryActivity.this, BuildConfig.APPLICATION_ID + ".provider", pdfFile));
//                    uris.add(FileProvider.getUriForFile(MainHistoryActivity.this, BuildConfig.APPLICATION_ID + ".provider", excelFile));

                    File file = new File(MainHistoryActivity.this.getExternalFilesDir(null).getAbsolutePath(), pdfFileName);

                    Uri uri = FileProvider.getUriForFile(MainHistoryActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                    shareIntent.setType("*/*");
//                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    shareIntent.setType("application/pdf");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(Intent.createChooser(shareIntent, "Share File"));

                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intHistoryStatus == Constants.GAS_TAB){
                    openCreateNewActionActivity(Constants.GAS);
                }
                else if(intHistoryStatus == Constants.OIL_TAB){
                    openCreateNewActionActivity(Constants.OIL);
                }
                else if (intHistoryStatus == Constants.REPAIR_TAB){
                    openCreateNewActionActivity(Constants.REPAIR);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // Mine
    // Request permission

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                EXTERNAL_STORAGE_PERMISSION_CODE
        );
    }

    private void exportExcel() {

        try {
            String fileName = "history" + System.currentTimeMillis() + ".xls";
            excelFileName = fileName;
            File file = new File(this.getExternalFilesDir(null).getAbsolutePath(), fileName);

            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setLocale(new Locale(Locale.ENGLISH.getLanguage(), Locale.ENGLISH.getCountry()));
            WritableWorkbook workbook;

            workbook = Workbook.createWorkbook(file, workbookSettings);

            //Sheet
            WritableSheet historySheet = workbook.createSheet("History", 0);

            historySheet.addCell(new Label(0, 0, "Action"));
            historySheet.addCell(new Label(1, 0, "Date"));
            historySheet.addCell(new Label(2, 0, "Last odometer"));
            historySheet.addCell(new Label(3, 0, "Price"));
            historySheet.addCell(new Label(4, 0, "Gallon"));
            historySheet.addCell(new Label(5, 0, "Location"));
            historySheet.addCell(new Label(6, 0, "Note"));


            for (int i = 0; i < historyArrayList.size(); i++) {
                History record = historyArrayList.get(i);

                historySheet.addCell(new Label(0, i + 1, "" + record.getActionFlag()));
                historySheet.addCell(new Label(1, i + 1, "" + record.getActionDate()));
                if (record.getLastOdometer() >= 0) {
                    historySheet.addCell(new Label(2, i + 1, "" + record.getLastOdometer()));
                }
                historySheet.addCell(new Label(3, i + 1, "" + record.getPrice()));
                if (record.getGallons() > 0) {
                    historySheet.addCell(new Label(4, i + 1, "" + record.getGallons()));
                }

                historySheet.addCell(new Label(5, i + 1, "" + record.getLocation()));
                historySheet.addCell(new Label(6, i + 1, "" + record.getNote()));
            }

            workbook.write();

            workbook.close();

            Toast.makeText(this, "Excel file generated", Toast.LENGTH_SHORT).show();

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void exportPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint title = new Paint();
        Paint content = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                pageWidth,
                pageHeight,
                1
        ).create();

        PdfDocument.Page firstPage = pdfDocument.startPage(pageInfo);

        Canvas canvas = firstPage.getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(20);

        content.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        content.setTextSize(15);

        canvas.drawText("History", 20, 20, title);

        int pos = 40;

        for ( int i = 0; i < historyArrayList.size(); i++) {
            History record = historyArrayList.get(i);

            canvas.drawText(
                    "--------------------------------",
                    20,
                    pos,
                    content
            );

            pos += 20;

            canvas.drawText(
                    "Action: " + record.getActionFlag(),
                    20,
                    pos,
                    content
            );

            pos += 20;

            canvas.drawText(
                    "Date: " + record.getActionDate(),
                    20,
                    pos,
                    content
            );

            pos += 20;

            canvas.drawText(
                    "Price: " + record.getPrice(),
                    20,
                    pos,
                    content
            );

            if (record.getGallons() > 0) {
                pos += 20;

                canvas.drawText(
                        "Gallon: "+ record.getGallons(),
                        20,
                        pos,
                        content
                );
            }

            if (record.getLocation().length() > 0) {
                pos += 20;

                canvas.drawText(
                        "Location: " + record.getLocation(),
                        20,
                        pos,
                        content
                );
            }

            if (record.getNote().length() > 0) {
                pos += 20;

                canvas.drawText(
                        "Note: " + record.getNote(),
                        20,
                        pos,
                        content
                );
            }

            pos += 10;

        }

        pdfDocument.finishPage(firstPage);

        try {
            String fileName = "history" + System.currentTimeMillis() + ".pdf";
            pdfFileName = fileName;
            File file = new File(this.getExternalFilesDir(null).getAbsolutePath(), fileName);

            pdfDocument.writeTo(new FileOutputStream(file));

            Toast.makeText(MainHistoryActivity.this, "PDF File Generated", Toast.LENGTH_SHORT).show();
        } catch (IOException error) {
            error.printStackTrace();
        }

        pdfDocument.close();

    }

    // Mine

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }


    //Add new action handler
    private void openCreateNewActionActivity(String actionFlag){
        if(actionFlag.equals(Constants.REPAIR)){
            Intent newActionActivityIntent = new Intent(MainHistoryActivity.this, AddActionRepair.class);
            newActionActivityIntent.putExtra("actionFlag", actionFlag);
            startActivityForResult(newActionActivityIntent, 100);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Intent newActionActivityIntent = new Intent(MainHistoryActivity.this, AddActionGasOil.class);
            newActionActivityIntent.putExtra("actionFlag", actionFlag);
            startActivityForResult(newActionActivityIntent, 100);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            Toast.makeText(MainHistoryActivity.this, "Add new action successfully", Toast.LENGTH_LONG).show();
        }
    }

    private void setVisibility(Boolean clicked){
        if(!clicked){
            btnShare.setVisibility(View.VISIBLE);
            btnExport.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            btnShare.setVisibility(View.INVISIBLE);
            btnExport.setVisibility(View.INVISIBLE);
            btnAdd.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked){
        if(!clicked){
            btnShare.startAnimation(fromBottom);
            btnExport.startAnimation(fromBottom);
            btnAdd.startAnimation(fromBottom);
            btnMore.startAnimation(rotateOpen);
        } else {
            btnShare.startAnimation(toBottom);
            btnExport.startAnimation(toBottom);
            btnAdd.startAnimation(toBottom);
            btnMore.startAnimation(rotateClose);
        }
    }

    private void setClickable(Boolean clicked){
        if(!clicked){
            btnAdd.setClickable(true);
            btnShare.setClickable(true);
            btnExport.setClickable(true);
        } else {
            btnAdd.setClickable(false);
            btnShare.setClickable(false);
            btnExport.setClickable(false);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
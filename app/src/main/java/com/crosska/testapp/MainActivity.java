package com.crosska.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private ExcelToSQLiteHelper dbHelper;
    private SQLiteWorker dbWorker;
    private MaterialButton chooseXLSBtn;
    private MaterialButton task0;
    private TextView txtFilePath;
    private TextView processShower;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new ExcelToSQLiteHelper(this);
        dbWorker = new SQLiteWorker(this);
        txtFilePath = findViewById(R.id.excelFilenameTextview);
        chooseXLSBtn = findViewById(R.id.chooseXLSFileButton);
        task0 = findViewById(R.id.task0Button);
        processShower = findViewById(R.id.processShowTextview);
        dbHelper.setProcessShower(processShower);

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        if (fileUri != null) {
                            String filePath = getFilePathFromUri(fileUri);
                            txtFilePath.setText("Идет процесс импорта: " + filePath);
                            task0.setEnabled(false);
                            Log.d(TAG, "Выбран файл " + filePath);
                            chooseXLSBtn.setEnabled(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        // Вызов метода импорта в отдельном потоке
                                        dbHelper.importExcelToDatabase(filePath);
                                        // Если нужно обновить UI после завершения импорта, используйте runOnUiThread:
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Обновление UI (например, скрыть ProgressBar или показать уведомление)
                                                Toast.makeText(MainActivity.this, "Импорт завершен", Toast.LENGTH_SHORT).show();
                                                chooseXLSBtn.setEnabled(true);
                                                task0.setEnabled(true);
                                                txtFilePath.setText("Выбран файл: " + filePath);
                                                processShower.setText("");
                                            }
                                        });
                                    } catch (final Exception e) {
                                        Log.e(TAG, "Ошибка импорта", e);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "Ошибка импорта: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                chooseXLSBtn.setEnabled(true);
                                                task0.setEnabled(false);
                                                txtFilePath.setText("Ошибка импорта файла: " + filePath);
                                                processShower.setText("");
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    }
                });

        chooseXLSBtn.setOnClickListener(v -> openFilePicker());
    }

    public void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Только .xlsx
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
        /*String filePath = "/storage/emulated/0/Download/data.xlsx"; // Путь к файлу
        dbHelper.importExcelToDatabase(filePath);*/
    }

    private String getFilePathFromUri(Uri uri) {
        try {
            String fileName = getFileName(uri);
            File tempFile = new File(getCacheDir(), fileName);
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("FilePicker", "Ошибка обработки URI: " + e.getMessage());
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    result = cursor.getString(index);
                }
            }
        }
        return result != null ? result : "temp.xlsx";
    }

    public void task0Clicked(View view) {
        Intent intent = new Intent(MainActivity.this, Task0Activity.class);
        startActivity(intent);
    }

}
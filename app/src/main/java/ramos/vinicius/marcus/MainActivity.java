package ramos.vinicius.marcus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.Translation;
import com.squareup.picasso.Picasso;

import ramos.vinicius.marcus.Service.ApiResponse;
import ramos.vinicius.marcus.Service.ErrorResponse;
import ramos.vinicius.marcus.Service.NasaApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.nasa.gov/";
    private static final String API_KEY = "API_KEY";
    private static final String TAG = "MainActivity";

    private TextView titleTextView;
    private ImageView apodImageView;
    private TextView explanationTextView;
    private LocalDate currentDate;
    private LocalDate todayDate;
    private DateTimeFormatter dateFormatter;
    private NasaApiService nasaApiService;

    private Translator translator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupToolbar();
        setupApiService();
        setupInitialData();
        setupTranslator();
        setupButtonListeners();
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.titleTextView);
        apodImageView = findViewById(R.id.apodImageView);
        explanationTextView = findViewById(R.id.explanationTextView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        nasaApiService = retrofit.create(NasaApiService.class);
    }

    private void setupInitialData() {
        todayDate = LocalDate.now();
        currentDate = todayDate;
        dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }

    private void setupTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.PORTUGUESE)
                .build();
        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Modelo de tradução baixado.");
                    loadApi(currentDate);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao baixar modelo de tradução", e);
                    loadApi(currentDate);
                });
    }

    private void setupButtonListeners() {
        FloatingActionButton prevButton = findViewById(R.id.prevButton);
        FloatingActionButton nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(view -> {
            currentDate = currentDate.minusDays(1);
            loadApi(currentDate);
        });

        nextButton.setOnClickListener(view -> {
            currentDate = currentDate.plusDays(1);
            if (currentDate.isAfter(todayDate)) {
                currentDate = todayDate;
                Toast.makeText(this, "A imagem para esta data não está disponível.", Toast.LENGTH_SHORT).show();
            } else {
                loadApi(currentDate);
            }
        });
    }

    private void loadApi(LocalDate date) {
        String dateString = date.format(dateFormatter);
        Call<ApiResponse> call = nasaApiService.getApod(API_KEY, dateString);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                handleApiResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro de conexão: ", t);
                Toast.makeText(MainActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiResponse(Response<ApiResponse> response) {
        if (response.isSuccessful()) {
            ApiResponse api = response.body();
            if (api != null) {
                String title = api.getTitle() != null ? api.getTitle() : "";
                String explanation = api.getExplanation() != null ? api.getExplanation() : "";

                translateText(title, translatedTitle -> {
                    translateText(explanation, translatedExplanation -> {
                        updateViewsWithApiData(translatedTitle, translatedExplanation, api.getUrl(), api.getMedia_type());
                    });
                });
            } else {
                Log.e(TAG, "Resposta da API é nula.");
                Toast.makeText(this, "Erro: Dados da API estão vazios.", Toast.LENGTH_SHORT).show();
            }
        } else {
            handleApiError(response);
        }
    }

    private void updateViewsWithApiData(String title, String explanation, String imageUrl, String mediaType) {
        titleTextView.setText(title);
        explanationTextView.setText(explanation);

        if ("image".equals(mediaType)) {
            Picasso.get().load(imageUrl).into(apodImageView);
        } else {
            apodImageView.setImageResource(R.drawable.placeholder);
            Toast.makeText(this, "O conteúdo desta data não é uma imagem.", Toast.LENGTH_SHORT).show();
        }
    }

    private void translateText(String text, TranslationCallback callback) {
        if (text.isEmpty()) {
            callback.onTranslationReceived("");
            return;
        }

        translator.translate(text)
                .addOnSuccessListener(translatedText -> {
                    Log.d(TAG, "Texto traduzido: " + translatedText);
                    callback.onTranslationReceived(translatedText);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro na tradução", e);
                    callback.onTranslationReceived(text);
                });
    }

    private interface TranslationCallback {
        void onTranslationReceived(String translatedText);
    }

    private void handleApiError(Response<ApiResponse> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Gson gson = new Gson();
                ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);
                if (errorResponse != null && errorResponse.getError() != null) {
                    String errorMessage = errorResponse.getError().getMessage();
                    Toast.makeText(this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "Erro desconhecido da API.");
                    Toast.makeText(this, "Erro desconhecido da API.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar resposta da API: ", e);
            Toast.makeText(this, "Erro ao obter dados da API.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (translator != null) {
            translator.close();
        }
    }
}

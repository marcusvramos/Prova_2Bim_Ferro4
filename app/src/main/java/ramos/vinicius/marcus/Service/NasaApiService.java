package ramos.vinicius.marcus.Service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NasaApiService {
    @GET("planetary/apod")
    Call<ApiResponse> getApod(@Query("api_key") String apiKey, @Query("date") String date);
}

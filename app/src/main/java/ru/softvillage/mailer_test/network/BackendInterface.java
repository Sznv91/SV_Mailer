package ru.softvillage.mailer_test.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.softvillage.mailer_test.network.entity.OrgInfo;
import ru.softvillage.mailer_test.network.entity.ReceivedEntity;
import ru.softvillage.mailer_test.network.entity.SentEntity;

public interface BackendInterface {

    @GET("test_app/firm_info.php")
    Call<OrgInfo> getOrgInfo();

    @POST("test_app/mailer_test.php")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<ReceivedEntity> sendReceipt(@Body SentEntity entity);
}

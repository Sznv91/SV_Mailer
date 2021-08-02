package ru.softvillage.mailer_main.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.softvillage.mailer_main.network.entity.OrgInfo;
import ru.softvillage.mailer_main.network.entity.ReceivedEntity;
import ru.softvillage.mailer_main.network.entity.SentEntity;

public interface BackendInterface {

    @GET("cloud/organization_data.php")
    Call<OrgInfo> getOrgInfo();

    @POST("app_mailer/set_receipt.php")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<ReceivedEntity> sendReceipt(@Body SentEntity entity);
}

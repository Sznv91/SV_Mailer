package ru.softvillage.mailer_test.network;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.softvillage.mailer_test.network.entity.OrgInfo;

public interface BackendInterface {

    @GET("test_app/firm_info.php")
    Call<OrgInfo> getOrgInfo();
}

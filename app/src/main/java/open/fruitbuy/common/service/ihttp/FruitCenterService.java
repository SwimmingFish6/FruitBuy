package open.fruitbuy.common.service.ihttp;

import java.util.List;

import open.fruitbuy.common.bean.FruitTypeBean;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by 51499 on 2017/7/10 0010.
 */
public interface FruitCenterService {

    @GET("fruitcenter/types")
    Call<List<FruitTypeBean>> getFruitTypes();

}

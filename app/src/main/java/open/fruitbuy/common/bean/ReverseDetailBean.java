package open.fruitbuy.common.bean;

import java.io.Serializable;

/**
 * Created by 51499 on 2017/7/15 0015.
 */
public class ReverseDetailBean implements Serializable{
    long storeId;
    String storeName;
    String address;
    int volume;
    float rank;

    public ReverseDetailBean() {
    }

    public ReverseDetailBean(long storeId, String storeName, String address, int volume, float rank) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.address = address;
        this.volume = volume;
        this.rank = rank;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

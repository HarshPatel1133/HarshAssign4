package harsh.patel.n01351133;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HarshSharedViewModel extends ViewModel {
    private MutableLiveData<Boolean> clockFormat = new MutableLiveData<>();
    private MutableLiveData<Boolean> orientation = new MutableLiveData<>();
    private MutableLiveData<String> homeBgColor = new MutableLiveData<>();
    private MutableLiveData<Integer> fontSize = new MutableLiveData<>();

    public void setFontSize(int size) {
        fontSize.setValue(size);
    }

    public LiveData<Integer> getFontSize() {
        return fontSize;
    }

    public void setOrientation(Boolean ori) {
        orientation.setValue(ori);
    }

    public LiveData<Boolean> getOrientation() {
        return orientation;
    }

    public void setHomeBgColor(String color) {
        homeBgColor.setValue(color);
    }

    public LiveData<String> getHomeBgColor() {
        return homeBgColor;
    }

    public void setClock(Boolean is12format) {
        clockFormat.setValue(is12format);
    }

    public LiveData<Boolean> getFormat() {
        return clockFormat;
    }
}

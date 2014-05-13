package net.erayzx.tab;

import net.erayzx.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Upload_frag extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View android = inflater.inflate(R.layout.upload_frag, container, false);
        return android;
	}
}

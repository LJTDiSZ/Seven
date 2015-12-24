package com.jcc.seven;

import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class Location extends AppCompatActivity implements LocationListener {
    private static final String[] A = { "invalid", "n/a", "fine", "coarse" };
    private static final String[] P = { "invalid", "n/a", "low", "medium", "high" };
    private static final String[] S = { "out of service", "temporarily unavailable", "available" };

    private LocationManager mgr;
    private TextView output;
    private String best;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mgr = (LocationManager)getSystemService(LOCATION_SERVICE);
        output = (TextView)findViewById(R.id.locationoutput);

        log("Location Providers:");
        dumpProviders();

        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);
        log("\nBest provider is: " + best);

        log("\nLocations (starting with last known):");
        android.location.Location location = mgr.getLastKnownLocation(best);
        dumpLocation(location);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mgr.requestLocationUpdates(best, 15000, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mgr.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        dumpLocation(location);
    }

    @Override
    public void onProviderEnabled(String s) {
        log("\nProvider enabled: " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        log("\nProvider disabled: " + s);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        log("\nProvider status changed: " + s + ", status=" + S[i] + ", extras=" + bundle);
    }

    private void log(String string){
        output.append(string + "\n");
    }

    private void dumpProviders(){
        List<String> providers = mgr.getAllProviders();
        for(String provider : providers){
            dumpProvider(provider);
        }
    }

    private void dumpProvider(String provider){
        LocationProvider info = mgr.getProvider(provider);
        StringBuilder builder = new StringBuilder();
        builder.append("LocationProvider[")
                .append("name=").append(info.getName())
                .append(",enabled=").append(mgr.isProviderEnabled(provider))
                .append(",getAccuracy=").append(A[info.getAccuracy() + 1])
                .append(",getPowerRequirement=").append(P[info.getPowerRequirement() + 1])
                .append(",hasMonetaryCost=").append(info.hasMonetaryCost())
                .append(",requiresCell=").append(info.requiresCell())
                .append(",requiresNetwork=").append(info.requiresNetwork())
                .append(",requiresSatellite=").append(info.requiresSatellite())
                .append(",supportsAltitude=").append(info.supportsAltitude())
                .append(",supportsBearing=").append(info.supportsBearing())
                .append(",supportsSpeed=").append(info.supportsSpeed())
                .append("]");
        log(builder.toString());
    }

    private void dumpLocation(android.location.Location location){
        if (location == null)
            log("\nLocation[unknown]");
        else
            log("\n" + location.toString());
    }
}

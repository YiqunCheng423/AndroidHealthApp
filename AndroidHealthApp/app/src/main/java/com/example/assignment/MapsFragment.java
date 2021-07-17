package com.example.assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.assignment.databinding.MapsFragmentBinding;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Circle;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;

import java.io.IOException;
import java.util.List;


public class MapsFragment extends Fragment {

    private SharedViewModel model;
    private MapsFragmentBinding binding;
    private MapView mapView;
    private String enteredAddress;
    private Circle circle;

    public MapsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        // Inflate the View for this fragment
        binding = com.example.assignment.databinding.MapsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        showDialog();

        mapView = (MapView) binding.mapView;
        mapView.onCreate(savedInstanceState);

        return view;
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter your address");

        final EditText address = new EditText(getActivity());
        address.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(address);

        builder.setPositiveButton("Direct", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enteredAddress = address.getText().toString();

                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {

                        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                                // Annotation

                                LatLng latLng = getLocationFromAddress(enteredAddress);

                                CircleManager circleManager = new CircleManager(mapView, mapboxMap, style);

                                CircleOptions circleOptions = new CircleOptions().withLatLng(latLng);

                                circle = circleManager.create(circleOptions);

                                CameraPosition position = new CameraPosition.Builder().target(latLng).zoom(16).build();
                                mapboxMap.setCameraPosition(position);
                            }
                        });

                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private LatLng getLocationFromAddress(String enteredAddress) {
        Geocoder geocoder = new Geocoder(getActivity());
        try {
            List<Address> addresses = geocoder.getFromLocationName(enteredAddress, 1);
            Address location = addresses.get(0);
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            return newLatLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
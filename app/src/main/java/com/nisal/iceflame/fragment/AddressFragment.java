package com.nisal.iceflame.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nisal.iceflame.adapters.AddressAdapter;
import com.nisal.iceflame.databinding.DialogAddAddressBinding;
import com.nisal.iceflame.databinding.FragmentAddressBinding;
import com.nisal.iceflame.model.AddressDto;
import com.nisal.iceflame.network.AddressApi;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressFragment extends Fragment {

    private FragmentAddressBinding binding;
    private AddressApi addressApi;
    private long userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddressBinding.inflate(inflater, container, false);

        addressApi = RetrofitClient.getAddressApi();

        binding.recyclerAddresses.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        SharedPreferences prefs =
                requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        userId = prefs.getLong("user_id", -1);

        loadAddresses();

        binding.btnAddAddress.setOnClickListener(v -> showAddAddressDialog());

        return binding.getRoot();
    }

    private void loadAddresses() {

        addressApi.getUserAddresses(userId)
                .enqueue(new Callback<List<AddressDto>>() {

                    @Override
                    public void onResponse(Call<List<AddressDto>> call,
                                           Response<List<AddressDto>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            AddressAdapter adapter = new AddressAdapter(response.body(),
                                    new AddressAdapter.OnAddressActionListener() {

                                        @Override
                                        public void onEdit(AddressDto address) {

                                            showEditAddressDialog(address);
                                        }

                                        @Override
                                        public void onDelete(AddressDto address) {

                                            new AlertDialog.Builder(requireContext())
                                                    .setTitle("Delete Address")
                                                    .setMessage("Are you sure you want to delete this address?")
                                                    .setPositiveButton("Delete", (d,w)-> deleteAddress(address.getId()))
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        }
                                    });

                            binding.recyclerAddresses.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AddressDto>> call, Throwable t) {

                        Toasty.error(getContext(),
                                "Failed to load addresses",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAddress(Long id){

        addressApi.deleteAddress(id)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.isSuccessful()){

                            Toasty.success(getContext(),
                                    "Address deleted",
                                    Toast.LENGTH_SHORT).show();

                            loadAddresses();
                        }else{
                            Toasty.error(getContext(),
                                    "Delete failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Toasty.error(getContext(),
                                "Delete error: "+t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showEditAddressDialog(AddressDto address){

        DialogAddAddressBinding dialogBinding =
                DialogAddAddressBinding.inflate(getLayoutInflater());

        dialogBinding.etStreet.setText(address.getStreet());
        dialogBinding.etCity.setText(address.getCity());
        dialogBinding.etPostal.setText(address.getPostalCode());

        String[] types = {"BILLING", "SHIPPING"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                types
        );

        dialogBinding.spAddressType.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnSaveAddress.setOnClickListener(v -> {

            String street = dialogBinding.etStreet.getText().toString().trim();
            String city = dialogBinding.etCity.getText().toString().trim();
            String postal = dialogBinding.etPostal.getText().toString().trim();

            if(street.isEmpty()){
                dialogBinding.etStreet.setError("Street is required");
                dialogBinding.etStreet.requestFocus();
                return;
            }

            if(city.isEmpty()){
                dialogBinding.etCity.setError("City is required");
                dialogBinding.etCity.requestFocus();
                return;
            }

            if(postal.isEmpty()){
                dialogBinding.etPostal.setError("Postal code is required");
                dialogBinding.etPostal.requestFocus();
                return;
            }

            address.setStreet(street);
            address.setCity(city);
            address.setPostalCode(postal);
            address.setType(dialogBinding.spAddressType.getSelectedItem().toString());
            address.setIsDefault(dialogBinding.chkDefault.isChecked());

            addressApi.updateAddress(address.getId(), address)
                    .enqueue(new Callback<AddressDto>() {

                        @Override
                        public void onResponse(Call<AddressDto> call,
                                               Response<AddressDto> response) {

                            if(response.isSuccessful()){

                                Toasty.success(getContext(),
                                        "Address updated",
                                        Toast.LENGTH_SHORT).show();

                                dialog.dismiss();

                                loadAddresses();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddressDto> call, Throwable t) {

                            Toasty.error(getContext(),
                                    "Update failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    private void showAddAddressDialog() {

        DialogAddAddressBinding dialogBinding =
                DialogAddAddressBinding.inflate(getLayoutInflater());

        String[] types = {"BILLING", "SHIPPING"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                types
        );

        dialogBinding.spAddressType.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnSaveAddress.setOnClickListener(v -> {

            String street = dialogBinding.etStreet.getText().toString().trim();
            String city = dialogBinding.etCity.getText().toString().trim();
            String postal = dialogBinding.etPostal.getText().toString().trim();

            // Validation
            if(street.isEmpty()){
                dialogBinding.etStreet.setError("Street is required");
                dialogBinding.etStreet.requestFocus();
                return;
            }

            if(city.isEmpty()){
                dialogBinding.etCity.setError("City is required");
                dialogBinding.etCity.requestFocus();
                return;
            }

            if(postal.isEmpty()){
                dialogBinding.etPostal.setError("Postal code is required");
                dialogBinding.etPostal.requestFocus();
                return;
            }

            if(postal.length() < 4){
                dialogBinding.etPostal.setError("Invalid postal code");
                dialogBinding.etPostal.requestFocus();
                return;
            }

            AddressDto address = AddressDto.builder()
                    .street(street)
                    .city(city)
                    .postalCode(postal)
                    .type(dialogBinding.spAddressType.getSelectedItem().toString())
                    .isDefault(dialogBinding.chkDefault.isChecked())
                    .userId(userId)
                    .build();

            addressApi.addAddress(address)
                    .enqueue(new Callback<AddressDto>() {

                        @Override
                        public void onResponse(Call<AddressDto> call,
                                               Response<AddressDto> response) {

                            if (response.isSuccessful()) {

                                Toasty.success(getContext(),
                                        "Address added",
                                        Toast.LENGTH_SHORT).show();

                                dialog.dismiss();

                                loadAddresses();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddressDto> call, Throwable t) {

                            Toasty.error(getContext(),
                                    "Error adding address",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
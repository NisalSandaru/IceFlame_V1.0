package com.nisal.iceflame.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.CheckoutAddressAdapter;
import com.nisal.iceflame.databinding.FragmentCheckoutBinding;
import com.nisal.iceflame.model.AddressDto;
import com.nisal.iceflame.model.CartDto;
import com.nisal.iceflame.model.CartItemDto;
import com.nisal.iceflame.model.CheckoutRequest;
import com.nisal.iceflame.model.OrderDto;
import com.nisal.iceflame.model.PaymentMethod;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;

    private List<AddressDto> addresses = new ArrayList<>();
    private List<CartItemDto> cartItems = new ArrayList<>();

    private CheckoutAddressAdapter adapter;

    private Long userId;
    private Long createdOrderId = null;

    private double totalAmount = 0;

    private PaymentMethod selectedPayment = PaymentMethod.COD;

    private boolean paymentInProgress = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        SharedPreferences prefs =
                requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        userId = prefs.getLong("user_id", -1);

        setupRecycler();
        setupPaymentCards();

        loadAddresses();
        loadCartItems();

        binding.btnPlaceOrder.setOnClickListener(v -> placeOrder());

        return binding.getRoot();
    }

    private void setupRecycler() {

        binding.addressRecycler.setLayoutManager(
                new LinearLayoutManager(getContext()));

        adapter = new CheckoutAddressAdapter(addresses);
        binding.addressRecycler.setAdapter(adapter);
    }

    private void setupPaymentCards() {

        updatePaymentUI(PaymentMethod.COD);

        binding.cardCod.setOnClickListener(v -> updatePaymentUI(PaymentMethod.COD));

        binding.cardCard.setOnClickListener(v -> updatePaymentUI(PaymentMethod.CARD));
    }

    private void updatePaymentUI(PaymentMethod method) {

        selectedPayment = method;

        int selectedColor = requireContext().getColor(R.color.md_theme_errorContainer_mediumContrast);
        int normalColor = requireContext().getColor(android.R.color.darker_gray);

        if (method == PaymentMethod.COD) {

            binding.cardCod.setStrokeColor(selectedColor);
            binding.cardCard.setStrokeColor(normalColor);

        } else {

            binding.cardCard.setStrokeColor(selectedColor);
            binding.cardCod.setStrokeColor(normalColor);
        }
    }

    private void loadAddresses() {

        RetrofitClient.getAddressApi()
                .getUserAddresses(userId)
                .enqueue(new Callback<List<AddressDto>>() {

                    @Override
                    public void onResponse(Call<List<AddressDto>> call, Response<List<AddressDto>> response) {

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                            addresses.clear();
                            addresses.addAll(response.body());
                            adapter.notifyDataSetChanged();

                        } else {
                            // No addresses found → redirect to AddressFragment
                            Toasty.warning(requireContext(),
                                    "Please add an address first",
                                    Toasty.LENGTH_SHORT).show();

                            // Redirect to AddressFragment
                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, new AddressFragment())
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AddressDto>> call, Throwable t) {

                        Toasty.error(requireContext(),
                                "Failed to load addresses",
                                Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCartItems() {

        RetrofitClient.getCartApi()
                .getCart(userId)
                .enqueue(new Callback<CartDto>() {

                    @Override
                    public void onResponse(Call<CartDto> call, Response<CartDto> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            cartItems.clear();

                            if (response.body().getItems() != null) {
                                cartItems.addAll(response.body().getItems());
                            }

                            calculateTotal();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartDto> call, Throwable t) {

                        Toasty.error(requireContext(),
                                "Failed to load cart",
                                Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    private void calculateTotal() {

        double subtotal = 0;

        for (CartItemDto item : cartItems) {

            int qty = item.getQuantity() == 0 ? 1 : item.getQuantity();

            subtotal += item.getPrice() * qty;
        }

        double delivery = 400;

        totalAmount = subtotal + delivery;

        binding.tvSubtotal.setText("Rs." + subtotal);
        binding.tvShipping.setText("Rs." + delivery);
        binding.tvTotal.setText("Rs." + totalAmount);
    }

    private void placeOrder() {

        if (paymentInProgress) return;

        Long addressId = adapter.getSelectedAddressId();

        if (addressId == null) {

            Toasty.warning(requireContext(),
                    "Please select an address",
                    Toasty.LENGTH_SHORT).show();
            return;
        }

        CheckoutRequest request = new CheckoutRequest();
        request.setUserId(userId);
        request.setAddressId(addressId);
        request.setPaymentMethod(selectedPayment.name());

        createOrder(request);
    }

    private void createOrder(CheckoutRequest request) {

        binding.btnPlaceOrder.setEnabled(false);

        RetrofitClient.getOrderApi()
                .createOrder(request)
                .enqueue(new Callback<OrderDto>() {

                    @Override
                    public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            createdOrderId = response.body().getId();

                            if (selectedPayment == PaymentMethod.COD) {

                                confirmPayment(createdOrderId);

                            } else {

                                launchPayHerePayment();
                            }

                        } else {

                            binding.btnPlaceOrder.setEnabled(true);

                            Toasty.error(requireContext(),
                                    "Order creation failed",
                                    Toasty.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDto> call, Throwable t) {

                        binding.btnPlaceOrder.setEnabled(true);

                        Toasty.error(requireContext(),
                                t.getMessage(),
                                Toasty.LENGTH_LONG).show();
                    }
                });
    }

    private void launchPayHerePayment() {

        paymentInProgress = true;

        InitRequest req = new InitRequest();

        req.setSandBox(true);
        req.setMerchantId("1224003");
        req.setMerchantSecret("MTg1MDE0NDQ1NzU4MTg3MDQwNDIyMjQ4MDc2NjQyNDk5ODc3Mjgw");

        req.setCurrency("LKR");
        req.setAmount(totalAmount);

        req.setOrderId("ICE-" + createdOrderId);
        req.setItemsDescription("IceFlame Order");

        req.getCustomer().setFirstName("Ice");
        req.getCustomer().setLastName("Flame");
        req.getCustomer().setEmail("test@test.com");
        req.getCustomer().setPhone("+94771234567");

        req.getCustomer().getAddress().setAddress("Colombo");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

        payHereLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> payHereLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        paymentInProgress = false;

                        if (result.getResultCode() == getActivity().RESULT_OK
                                && result.getData() != null) {

                            PHResponse<StatusResponse> response =
                                    (PHResponse<StatusResponse>) result.getData()
                                            .getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                            if (response != null && response.isSuccess()) {

                                Toasty.success(requireContext(),
                                        "Payment Success!",
                                        Toasty.LENGTH_SHORT).show();

                                confirmPayment(createdOrderId);

                            } else {

                                binding.btnPlaceOrder.setEnabled(true);

                                Toasty.error(requireContext(),
                                        "Payment Failed",
                                        Toasty.LENGTH_SHORT).show();
                            }

                        } else {

                            binding.btnPlaceOrder.setEnabled(true);

                            Toasty.warning(requireContext(),
                                    "Payment Cancelled",
                                    Toasty.LENGTH_SHORT).show();
                        }
                    });


    private void confirmPayment(Long orderId) {

        RetrofitClient.getOrderApi()
                .confirmPayment(orderId)
                .enqueue(new Callback<OrderDto>() {

                    @Override
                    public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {

                        binding.btnPlaceOrder.setEnabled(true);

                        System.out.println("CONFIRM RESPONSE CODE: " + response.code());
                        System.out.println("CONFIRM BODY: " + response.body());
                        System.out.println("CONFIRM ERROR: " + response.errorBody());

                        if (response.isSuccessful()) {

                            Toasty.success(requireContext(),
                                    "Order placed successfully",
                                    Toasty.LENGTH_LONG).show();

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, new PaymentPlacedFragment())
                                    .commit();

                        } else {

                            Toasty.error(requireContext(),
                                    "Payment confirmation failed (" + response.code() + ")",
                                    Toasty.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDto> call, Throwable t) {

                        binding.btnPlaceOrder.setEnabled(true);

                        Toasty.error(requireContext(),
                                t.getMessage(),
                                Toasty.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
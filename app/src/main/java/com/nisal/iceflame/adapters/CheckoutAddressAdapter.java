package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nisal.iceflame.R;
import com.nisal.iceflame.databinding.ItemCheckoutAddressBinding;
import com.nisal.iceflame.model.AddressDto;

import java.util.List;

public class CheckoutAddressAdapter extends RecyclerView.Adapter<CheckoutAddressAdapter.ViewHolder> {

    private List<AddressDto> addressList;
    private Long selectedAddressId = null;

    public CheckoutAddressAdapter(List<AddressDto> addressList) {
        this.addressList = addressList;
    }

    public Long getSelectedAddressId() {
        return selectedAddressId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemCheckoutAddressBinding binding;

        public ViewHolder(ItemCheckoutAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemCheckoutAddressBinding binding = ItemCheckoutAddressBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AddressDto address = addressList.get(position);

        holder.binding.txtStreet.setText(address.getStreet());
        holder.binding.txtCity.setText(address.getCity());
        holder.binding.txtPostal.setText(address.getPostalCode());
        holder.binding.txtType.setText(address.getType());

        boolean selected = address.getId().equals(selectedAddressId);

        if(selected){
            holder.binding.cardAddress.setStrokeColor(
                    holder.itemView.getContext().getColor(R.color.md_theme_errorContainer_mediumContrast));
        } else {
            holder.binding.cardAddress.setStrokeColor(
                    holder.itemView.getContext().getColor(android.R.color.darker_gray));
        }

        holder.binding.cardAddress.setOnClickListener(v -> {

            selectedAddressId = address.getId();

            notifyDataSetChanged();

        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }
}
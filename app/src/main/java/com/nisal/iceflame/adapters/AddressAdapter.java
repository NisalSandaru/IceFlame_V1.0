package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.nisal.iceflame.databinding.ItemAddressBinding;
import com.nisal.iceflame.model.AddressDto;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<AddressDto> addressList;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onEdit(AddressDto address);
        void onDelete(AddressDto address);
    }

    public AddressAdapter(List<AddressDto> addressList, OnAddressActionListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemAddressBinding binding;

        public ViewHolder(ItemAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemAddressBinding binding = ItemAddressBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AddressDto address = addressList.get(position);

        holder.binding.txtStreet.setText(address.getStreet());
        holder.binding.txtCity.setText(address.getCity());
        holder.binding.txtPostal.setText(address.getPostalCode());
        holder.binding.txtType.setText(address.getTitle());

        holder.binding.btnEdit.setOnClickListener(v ->
                listener.onEdit(address));

        holder.binding.btnDelete.setOnClickListener(v ->
                listener.onDelete(address));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }
}
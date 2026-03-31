package com.app.journeyfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.util.List;

//used to show the places
//using info from PlaceItem
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final Context context;
    private final List<PlaceItem> placeList;
    private final PlacesClient placesClient;

    public PlaceAdapter(Context context, List<PlaceItem> placeList, PlacesClient placesClient) {
        this.context = context;
        this.placeList = placeList;
        this.placesClient = placesClient;
    }

    //for viewing xml
    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    //show the place name and adress
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceItem item = placeList.get(position);
        holder.name.setText(item.getName());
        holder.address.setText(item.getAddress());

        //shows if there is a image
        if (item.getPhotoUrl() != null) {
            Glide.with(context)
                    .load(item.getPhotoUrl())
                    .placeholder(R.drawable.default_place_image)
                    .into(holder.image);
        } else if (item.getPhotoMetadata() != null) {
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(item.getPhotoMetadata())
                    .build();

            placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener((@NonNull FetchPhotoResponse response) -> {
                        Bitmap bitmap = response.getBitmap();
                        holder.image.setImageBitmap(bitmap);
                    })
                    //no images then uses the default onw
                    .addOnFailureListener(e -> {
                        holder.image.setImageResource(R.drawable.default_place_image);
                    });
        } else {
            holder.image.setImageResource(R.drawable.default_place_image);
        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    //for the xml
    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView name, address;
        ImageView image;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.placeName);
            address = itemView.findViewById(R.id.placeAddress);
            image = itemView.findViewById(R.id.placeImage);
        }
    }
}

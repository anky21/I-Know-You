package me.anky.connectid.connections;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.anky.connectid.R;
import me.anky.connectid.data.ConnectidConnection;

public class ConnectionsRecyclerViewAdapter extends
        RecyclerView.Adapter<ConnectionsRecyclerViewAdapter.ViewHolder>  {

    private List<ConnectidConnection> connections = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private RecyclerViewClickListener clickListener;

    public interface RecyclerViewClickListener {
        void onItemClick(View view, int position);
    }

    public ConnectionsRecyclerViewAdapter(Context context, List<ConnectidConnection> connections,
                                          RecyclerViewClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.connections = connections;
        this.clickListener = clickListener;
    }

    // Inflates the item layout and returns the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.connections_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        context = parent.getContext();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConnectionsRecyclerViewAdapter.ViewHolder holder, int position) {

        String firstName = connections.get(position).getFirstName();
        String lastName = connections.get(position).getLastName();
        String feature = connections.get(position).getFeature();
        String imageName = connections.get(position).getImageName();

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String path = directory.getAbsolutePath() + "/" + imageName;
        Glide.with(context)
                .load(Uri.fromFile(new File(path)))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.listItemIv);

        holder.listNameTv.setText(firstName + " " + lastName);
        holder.listFeatureTv.setText(feature);
    }

    @Override
    public int getItemCount() {
        if (connections == null) {
            return 0;
        } else {
            return connections.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_iv) ImageView listItemIv;
        @BindView(R.id.list_name_tv) TextView listNameTv;
        @BindView(R.id.list_feature_tv) TextView listFeatureTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Updates the array, allowing for uninterrupted scrolling
    public void setConnections(List<ConnectidConnection> connections) {
        if (connections == null) {
            connections = new ArrayList<>();
            this.connections = connections;
        } else {
            this.connections.clear();
            this.connections.addAll(connections);
            notifyDataSetChanged();
        }
    }
}
